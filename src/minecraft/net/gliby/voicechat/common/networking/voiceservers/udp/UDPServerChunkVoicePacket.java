package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;

public class UDPServerChunkVoicePacket extends UDPPacket {

	byte[] data;
	boolean direct;
	byte chunkSize;
	int entityId;

	public UDPServerChunkVoicePacket(byte[] samples, int entityID, boolean direct, byte chunkSize) {
		this.data = samples;
		this.entityId = entityID;
		this.direct = direct;
		this.chunkSize = chunkSize;
	}

	@Override
	public byte id() {
		return 5;
	}

	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeInt(entityId);
		out.writeByte(chunkSize);
		out.writeBoolean(direct);
		UDPByteUtilities.writeBytes(data, out);
	}

}
