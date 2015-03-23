package net.gliby.voicechat.common.networking.voiceservers.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.DataManager;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;
import net.gliby.voicechat.common.networking.voiceservers.VoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.voiceservers.udp.UdpServer.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLCommonHandler;

public class UDPVoiceServer extends VoiceAuthenticatedServer {

	public volatile static boolean running;
	private VoiceChatServer voiceChat;
	private DataManager manager;
	private UDPVoiceServerHandler handler;
	public Map<EntityPlayerMP, UDPClient> clientMap = new HashMap<EntityPlayerMP, UDPClient>();

	private UdpServer server;

	public UDPVoiceServer(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
		this.manager = voiceChat.getServerNetwork().getDataManager();
		handler = new UDPVoiceServerHandler(this);
	}
	//TODO Fix crash
	@Override
	public void closeConnection(EntityPlayerMP player) {
		UDPClient client = clientMap.get(player);
		clientMap.remove(player);
		if(client.socketAddress != null)
		handler.closeConnection(client.socketAddress);
	}

	@Override
	public EnumVoiceNetworkType getType() {
		return EnumVoiceNetworkType.UDP;
	}

	@Override
	public void handleVoiceData(EntityPlayerMP player, byte[] data, byte divider, int id, boolean end) {
		manager.addQueue(player, data, divider, id, end);
	}

	@Override
	public void sendChunkVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples, byte chunkSize) {
		UDPClient client = clientMap.get(player);
		if (client != null) sendPacket(new UDPServerChunkVoicePacket(samples, entityID, direct, chunkSize), client);
	}

	@Override
	public void sendEntityPosition(EntityPlayerMP player, int entityID, double x, double y, double z) {
		UDPClient client = clientMap.get(player);
		if (client != null) sendPacket(new UDPServerEntityPositionPacket(entityID, x, y, z), client);
	}

	public void sendPacket(UDPPacket packet, UDPClient client) {
		VoiceChat.getLogger().info("Sending " + client + " packet: " + packet + " - " + client.socketAddress);
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeLong(client.key);
		out.writeByte(packet.id());
		packet.write(out);
		byte[] data = out.toByteArray();
		try {
			server.send(new DatagramPacket(data, data.length, client.socketAddress.getAddress(), voiceChat.getServerSettings().getUDPPort()));
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendVoiceData(EntityPlayerMP player, int entityID, boolean global, byte[] samples) {
		UDPClient client = clientMap.get(player);
		sendPacket(new UDPServerVoicePacket(samples, entityID, global), client);
	}

	@Override
	public void sendVoiceEnd(EntityPlayerMP player, int entityID) {
		UDPClient client = clientMap.get(player);
		sendPacket(new UDPServerVoiceEndPacket(entityID), client);
	}

	@Override
	public boolean start() {
		String hostname = "0.0.0.0";
		MinecraftServer mc = MinecraftServer.getServer();
		if (mc.isDedicatedServer()) hostname = mc.getServerHostname();
		server = new UdpServer(voiceChat.getLogger(), hostname, voiceChat.getServerSettings().getUDPPort());
		server.addUdpServerListener(new UdpServer.Listener() {
			@Override
			public void packetReceived(Event evt) {
				try {
					handler.read(evt.getPacketAsBytes(), evt.getPacket());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		server.start();
		return true;
	}

	@Override
	public void stop() {
		this.running = false;
		handler.close();
		server.stop();
		this.clientMap.clear();
	}
}
