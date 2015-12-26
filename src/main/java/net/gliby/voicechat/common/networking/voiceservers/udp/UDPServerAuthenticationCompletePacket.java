package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;

public class UDPServerAuthenticationCompletePacket extends UDPPacket {

	@Override
	public byte id() {
		return 0;
	}

	@Override
	public void write(ByteArrayDataOutput out) {
	}

}
