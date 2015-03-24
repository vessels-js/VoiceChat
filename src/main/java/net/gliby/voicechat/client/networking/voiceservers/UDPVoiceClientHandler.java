package net.gliby.voicechat.client.networking.voiceservers;

import java.util.concurrent.LinkedBlockingQueue;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.voiceservers.udp.UDPByteUtilities;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

/**
 * Packet handler, this could of been done much better aesthetically, but works flawlessy now.
 **/
public class UDPVoiceClientHandler implements Runnable {

	public LinkedBlockingQueue<byte[]> packetQueue;
	private UDPVoiceClient client;

	public UDPVoiceClientHandler(UDPVoiceClient client) {
		this.client = client;
		packetQueue = new LinkedBlockingQueue<byte[]>();
	}

	private void handleAuthComplete() {
		client.handleAuth();
	}

	private void handleChunkVoiceData(ByteArrayDataInput in) {
		int entityId = in.readInt();
		byte chunkSize = in.readByte();
		boolean direct = in.readBoolean();
		byte[] data = UDPByteUtilities.readBytes(in);
		client.handlePacket(entityId, data, chunkSize, direct);
	}

	private void handleEntityPosition(ByteArrayDataInput in) {
		int entityId = in.readInt();
		double x = in.readDouble();
		double y = in.readDouble();
		double z = in.readDouble();
		client.handleEntityPosition(entityId, x, y, z);
	}

	private void handleVoiceData(ByteArrayDataInput in) {
		int entityId = in.readInt();
		boolean direct = in.readBoolean();
		byte[] data = UDPByteUtilities.readBytes(in);
		client.handlePacket(entityId, data, data.length, direct);
	}

	/*
	 * out.writeInt(entityID); out.writeBoolean(global); out.writeInt(data.length); for(int i = 0; i < data.length; i++)
	 * { out.writeByte(data[i]); }
	 */

	private void handleVoiceEnd(ByteArrayDataInput in) {
		int entityId = in.readInt();
		client.handleEnd(entityId);
	}

	public void read(byte[] data) {
		ByteArrayDataInput in = ByteStreams.newDataInput(data);
		// long key = in.readLong();
		long key = in.readLong();
		byte id = in.readByte();
		VoiceChat.getLogger().info("Packet is " + id);
		if (client.key == key) {
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
		// }
	}

	@Override
	public void run() {
		while (client.running) {
			if (!packetQueue.isEmpty()) {
				read(packetQueue.poll());
			} else {
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
}