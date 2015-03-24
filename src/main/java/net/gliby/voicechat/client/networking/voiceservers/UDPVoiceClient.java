package net.gliby.voicechat.client.networking.voiceservers;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import javax.sound.midi.VoiceStatus;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.networking.VoiceAuthenticatedClient;
import net.gliby.voicechat.client.sound.SoundManager;
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
	private int port;

	private String host;

	/** Considering MTU, arbitrary buffer size **/
	private final int BUFFER_SIZE = 2048;

	private SoundManager soundManager;

	private UDPVoiceClientHandler handler;
	private DatagramSocket datagramSocket;
	private InetSocketAddress address;
	public int key;

	public UDPVoiceClient(EnumVoiceNetworkType enumVoiceServer, String hash, String host, int udpPort) {
		super(enumVoiceServer, hash);
		this.port = udpPort;
		this.host = host;
		this.soundManager = VoiceChat.getProxyInstance().getSoundManager();
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
		VoiceChat.getSynchronizedProxyInstance().getSoundManager().alertEnd(id);
	}

	@Override
	public void handleEntityPosition(int entityID, double x, double y, double z) {
		PlayerProxy proxy = soundManager.playerData.get(entityID);
		if (proxy != null) {
			proxy.setPosition(x, y, z);
		}
	}

	@Override
	public void handlePacket(int entityID, byte[] data, int chunkSize, boolean direct) {
		VoiceChat.getSynchronizedProxyInstance().getSoundManager().getSoundPreProcessor().process(entityID, data, chunkSize, direct);
	}

	public void sendPacket(UDPPacket packet) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeInt(key);
		out.writeByte(packet.id());
		packet.write(out);
		byte[] data = out.toByteArray();
		try {
			datagramSocket.send(new DatagramPacket(data, data.length, address));
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
			new Thread(handler = new UDPVoiceClientHandler(this)).start();
		} catch (SocketException e) {
			running = false;
			e.printStackTrace();
		}
		VoiceChat.getLogger().info("Connected to UDP[" + host + ":" + port + "] voice server, requesting authentication.");
		autheticate();
		while (running) {
			byte[] packetBuffer = new byte[BUFFER_SIZE];
			DatagramPacket p = new DatagramPacket(packetBuffer, BUFFER_SIZE);
			try {
				datagramSocket.receive(p);
				handler.packetQueue.offer(p.getData());
				synchronized (handler) {
					handler.notify();
				}
			} catch (IOException e) {
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
