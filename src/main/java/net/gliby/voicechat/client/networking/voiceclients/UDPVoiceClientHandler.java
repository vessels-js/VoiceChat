package net.gliby.voicechat.client.networking.voiceclients;

import java.util.concurrent.LinkedBlockingQueue;

import net.gliby.voicechat.common.networking.voiceservers.udp.UDPByteUtilities;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

/**
 * Packet handler, this could of been done much better esthetically, but works
 * without flawless.
 **/
public class UDPVoiceClientHandler implements Runnable {

	public LinkedBlockingQueue<byte[]> packetQueue;
	private final UDPVoiceClient client;

	public UDPVoiceClientHandler(UDPVoiceClient client) {
		this.client = client;
		packetQueue = new LinkedBlockingQueue<byte[]>();
	}

	private void handleAuthComplete() {
		client.handleAuth();
	}

	private void handleChunkVoiceData(ByteArrayDataInput in) {
		final byte volume = in.readByte();
		final int entityId = in.readInt();
		final byte chunkSize = in.readByte();
		final boolean direct = in.readBoolean();
		final byte[] data = UDPByteUtilities.readBytes(in);
		client.handlePacket(entityId, data, chunkSize, direct, volume);
	}

	private void handleEntityPosition(ByteArrayDataInput in) {
		final int entityId = in.readInt();
		final double x = in.readDouble();
		final double y = in.readDouble();
		final double z = in.readDouble();
		client.handleEntityPosition(entityId, x, y, z);
	}

	private void handleVoiceData(ByteArrayDataInput in) {
		final byte volume = in.readByte();
		final int entityId = in.readInt();
		final boolean direct = in.readBoolean();
		final byte[] data = UDPByteUtilities.readBytes(in);
		client.handlePacket(entityId, data, data.length, direct, volume);
	}

	private void handleVoiceEnd(ByteArrayDataInput in) {
		final int entityId = in.readInt();
		client.handleEnd(entityId);
	}

	public void read(byte[] data) {
		final ByteArrayDataInput in = ByteStreams.newDataInput(data);
		final byte id = in.readByte();
		switch (id) {
		case 0:
			handleAuthComplete();
			break;
		case 1:
			handleVoiceData(in);
			break;
		case 2:
			handleVoiceEnd(in);
			break;
		case 4:
			handleEntityPosition(in);
			break;
		case 5:
			handleChunkVoiceData(in);
			break;
		}
	}

	@Override
	public void run() {
		while (UDPVoiceClient.running) {
			if (!packetQueue.isEmpty()) {
				read(packetQueue.poll());
			} else {
				synchronized (this) {
					try {
						this.wait();
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
}