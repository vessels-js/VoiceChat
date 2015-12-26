package net.gliby.voicechat.client.networking.voiceclients;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.gliby.voicechat.common.PlayerProxy;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;
import net.gliby.voicechat.common.networking.voiceservers.udp.UDPClientAuthenticationPacket;
import net.gliby.voicechat.common.networking.voiceservers.udp.UDPClientVoiceEnd;
import net.gliby.voicechat.common.networking.voiceservers.udp.UDPClientVoicePacket;
import net.gliby.voicechat.common.networking.voiceservers.udp.UDPPacket;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class UDPVoiceClient extends VoiceAuthenticatedClient {

	public volatile static boolean running;
	private final int port;

	private final String host;

	/** Considering MTU, arbitrary buffer size **/
	private final int BUFFER_SIZE = 2048;

	private final ClientStreamManager soundManager;

	private UDPVoiceClientHandler handler;
	private DatagramSocket datagramSocket;
	private InetSocketAddress address;
	public int key;

	public UDPVoiceClient(EnumVoiceNetworkType enumVoiceServer, String hash, String host, int udpPort) {
		super(enumVoiceServer, hash);
		this.port = udpPort;
		this.host = host;
		VoiceChat.getProxyInstance();
		this.soundManager = VoiceChatClient.getSoundManager();
		this.key = (int) new BigInteger(hash.replaceAll("[^0-9.]", "")).longValue();
	}

	@Override
	public void autheticate() {
		sendPacket(new UDPClientAuthenticationPacket(hash));
	}

	public void handleAuth() {
		VoiceChat.getLogger().info("Successfully authenticated with voice server, client functionical.");
		this.setAuthed(true);
	}

	@Override
	public void handleEnd(int id) {
		VoiceChat.getSynchronizedProxyInstance();
		VoiceChatClient.getSoundManager().alertEnd(id);
	}

	@Override
	public void handleEntityPosition(int entityID, double x, double y, double z) {
		final PlayerProxy proxy = soundManager.playerData.get(entityID);
		if (proxy != null) {
			proxy.setPosition(x, y, z);
		}
	}

	@Override
	public void handlePacket(int entityID, byte[] data, int chunkSize, boolean direct, byte volume) {
		VoiceChat.getSynchronizedProxyInstance();
		VoiceChatClient.getSoundManager().getSoundPreProcessor().process(entityID, data, chunkSize, direct, volume);
	}

	ByteArrayDataOutput packetBuffer = ByteStreams.newDataOutput();

	public void sendPacket(UDPPacket packet) {
		if (!datagramSocket.isClosed()) {
			packetBuffer.writeByte(packet.id());
			packet.write(packetBuffer);
			final byte[] data = packetBuffer.toByteArray();
			try {
				datagramSocket.send(new DatagramPacket(data, data.length, address));
			} catch (final SocketException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			packetBuffer = ByteStreams.newDataOutput();
		}
	}

	@Override
	public void sendVoiceData(byte divider, byte[] samples, boolean end) {
		if (this.authed) {
			if (end) sendPacket(new UDPClientVoiceEnd());
			else sendPacket(new UDPClientVoicePacket(divider, samples));
		}
	}

	@Override
	public void start() {
		running = true;
		address = new InetSocketAddress(host, port);
		try {
			datagramSocket = new DatagramSocket();
			datagramSocket.setSoTimeout(0);
			datagramSocket.connect(address);
			new Thread(handler = new UDPVoiceClientHandler(this), "UDP Voice Client Process").start();
		} catch (final SocketException e) {
			running = false;
			e.printStackTrace();
		}
		VoiceChat.getLogger().info("Connected to UDP[" + host + ":" + port + "] voice server, requesting authentication.");
		autheticate();
		while (running) {
			final byte[] packetBuffer = new byte[BUFFER_SIZE];
			final DatagramPacket p = new DatagramPacket(packetBuffer, BUFFER_SIZE);
			try {
				datagramSocket.receive(p);
				handler.packetQueue.offer(p.getData());
				synchronized (handler) {
					handler.notify();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		running = false;
		if (datagramSocket != null) datagramSocket.close();
	}
}
