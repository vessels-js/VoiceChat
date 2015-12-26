package net.gliby.voicechat.common.networking.voiceservers.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.ServerStreamManager;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;
import net.gliby.voicechat.common.networking.voiceservers.VoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.voiceservers.udp.UdpServer.Event;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class UDPVoiceServer extends VoiceAuthenticatedServer {

	public volatile static boolean running;
	private final VoiceChatServer voiceChat;
	private final ServerStreamManager manager;
	private UDPVoiceServerHandler handler;
	public Map<Integer, UDPClient> clientMap;

	private UdpServer server;

	public UDPVoiceServer(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
		this.manager = voiceChat.getServerNetwork().getDataManager();
	}

	@Override
	public void closeConnection(int id) {
		final UDPClient client = clientMap.get(id);
		if (client != null) handler.closeConnection(client.socketAddress);
		clientMap.remove(id);
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
	public void sendChunkVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples, byte chunkSize, byte volume) {
		final UDPClient client = clientMap.get(player.getEntityId());
		if (client != null) sendPacket(new UDPServerChunkVoicePacket(samples, entityID, direct, chunkSize, volume), client);
	}

	@Override
	public void sendEntityPosition(EntityPlayerMP player, int entityID, double x, double y, double z) {
		final UDPClient client = clientMap.get(player.getEntityId());
		if (client != null) sendPacket(new UDPServerEntityPositionPacket(entityID, x, y, z), client);
	}

	ByteArrayDataOutput packetBuffer = ByteStreams.newDataOutput();

	public void sendPacket(UDPPacket packet, UDPClient client) {
		packetBuffer.writeByte(packet.id());
		packet.write(packetBuffer);
		final byte[] data = packetBuffer.toByteArray();
		try {
			server.send(new DatagramPacket(data, data.length, client.socketAddress));
		} catch (final SocketException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		packetBuffer = ByteStreams.newDataOutput();
	}

	@Override
	public void sendVoiceData(EntityPlayerMP player, int entityID, boolean global, byte[] samples, byte volume) {
		final UDPClient client = clientMap.get(player.getEntityId());
		if (client != null) sendPacket(new UDPServerVoicePacket(samples, entityID, global, volume), client);
	}

	@Override
	public void sendVoiceEnd(EntityPlayerMP player, int entityID) {
		final UDPClient client = clientMap.get(player.getEntityId());
		if (client != null) sendPacket(new UDPServerVoiceEndPacket(entityID), client);
	}

	@Override
	public boolean start() {
		clientMap = new HashMap<Integer, UDPClient>();
		handler = new UDPVoiceServerHandler(this);
		final MinecraftServer mc = MinecraftServer.getServer();
		if (mc.isDedicatedServer()) {
			if (StringUtils.isNullOrEmpty(mc.getServerHostname())) {
				server = new UdpServer(VoiceChatServer.getLogger(), voiceChat.getServerSettings().getUDPPort());
			} else {
				server = new UdpServer(VoiceChatServer.getLogger(), mc.getServerHostname(), voiceChat.getServerSettings().getUDPPort());
			}
		} else {
			server = new UdpServer(VoiceChatServer.getLogger(), voiceChat.getServerSettings().getUDPPort());
		}
		server.addUdpServerListener(new UdpServer.Listener() {

			@Override
			public void packetReceived(Event evt) {
				try {
					handler.read(evt.getPacketAsBytes(), evt.getPacket());
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
		server.start();
		return true;
	}

	@Override
	public void stop() {
		UDPVoiceServer.running = false;
		handler.close();
		server.clearUdpListeners();
		server.stop();
		this.clientMap.clear();
		handler = null;
		server = null;
	}
}
