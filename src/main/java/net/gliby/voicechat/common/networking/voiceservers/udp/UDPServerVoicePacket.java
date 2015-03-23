package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;

public class UDPServerVoicePacket extends UDPPacket {

	public int entityID;
	public boolean direct;
	public byte[] data;
	public byte divider;

	public UDPServerVoicePacket(byte[] data, int entityId, boolean global) {
		this.data = data;
		this.entityID = entityId;
		this.direct = global;
	}

	@Override
	public byte id() {
		return 1;
	}

	@Override
	public void write(ByteArrayDataOutput in) {
		in.writeInt(entityID);
		in.writeBoolean(direct);
		UDPByteUtilities.writeBytes(data, in);
	}

}
