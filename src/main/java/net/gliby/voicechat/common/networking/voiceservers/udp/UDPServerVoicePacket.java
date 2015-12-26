package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;

public class UDPServerVoicePacket extends UDPPacket {

	public int entityID;
	public boolean direct;
	public byte[] data;
	public byte volume;

	public UDPServerVoicePacket(byte[] data, int entityId, boolean global, byte volume) {
		this.data = data;
		this.entityID = entityId;
		this.direct = global;
		this.volume = volume;
	}

	@Override
	public byte id() {
		return 1;
	}

	@Override
	public void write(ByteArrayDataOutput in) {
		in.writeByte(volume);
		in.writeInt(entityID);
		in.writeBoolean(direct);
		UDPByteUtilities.writeBytes(data, in);
	}

}
