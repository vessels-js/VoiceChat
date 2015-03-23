package net.gliby.voicechat.common.networking.voiceservers.udp;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.MathUtility;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class UDPVoiceServerHandler {

	private ExecutorService threadService;
	private Map<InetSocketAddress, UDPClient> clientNetworkMap = new HashMap<InetSocketAddress, UDPClient>();
	private UDPVoiceServer server;

	public UDPVoiceServerHandler(UDPVoiceServer server) {
		this.server = server;
		threadService = Executors.newFixedThreadPool((int) MathUtility.clamp(MinecraftServer.getServer().getMaxPlayers(), 1, 8));
	}

	public void close() {
		clientNetworkMap.clear();
		threadService.shutdown();
	}

	public void closeConnection(InetSocketAddress address) {
		clientNetworkMap.remove(address);
	}

	private void handleAuthetication(InetSocketAddress address, DatagramPacket packet, ByteArrayDataInput in) {
		String hash = null;
		try {
			hash = new String(UDPByteUtilities.readBytes(in), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		EntityPlayerMP player = server.waitingAuth.get(hash);
		if (player != null) {
			UDPClient client = new UDPClient(player, address, hash);
			clientNetworkMap.put(client.socketAddress, client);
			server.clientMap.put(player, client);
			server.waitingAuth.remove(hash);
			server.sendPacket(new UDPServerAuthenticationCompletePacket(), client);
		} 
	}

	private void handleVoice(UDPClient client, ByteArrayDataInput in) {
		server.handleVoiceData(client.player, UDPByteUtilities.readBytes(in), in.readByte(), client.player.getEntityId(), false);
	}

	private void handleVoiceEnd(UDPClient client) {
		server.handleVoiceData(client.player, null, (byte)0, client.player.getEntityId(), true);
	}

	public void read(final byte[] data, final DatagramPacket packet) throws Exception {
		VoiceChat.getLogger().info("Handlling packet!");
		final InetSocketAddress address = (InetSocketAddress) packet.getSocketAddress();
		final UDPClient client = clientNetworkMap.get(address);
		final ByteArrayDataInput in = ByteStreams.newDataInput(data);
		final long key = in.readLong();
		final byte id = in.readByte();
		threadService.execute(new Runnable() {
			@Override
			public void run() {
				if (id == 0) {
					handleAuthetication(address, packet, in);
				}
				
				if (client != null) {
					if(client.key == key) {
						switch (id) {
						case 1:
							handleVoice(client, in);
							break;
						case 2:
							handleVoiceEnd(client);
							break;
						}
					}
				}
			}
		});
	}
}
