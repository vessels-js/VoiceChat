package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;

public class UDPServerVoiceEndPacket extends UDPPacket {

	int entityID;

	public UDPServerVoiceEndPacket(int entityID) {
		this.entityID = entityID;
	}

	@Override
	public byte id() {
		return 2;
	}

	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeInt(entityID);
	}
}
