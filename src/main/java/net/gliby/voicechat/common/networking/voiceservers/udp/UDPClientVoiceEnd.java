package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;

public class UDPClientVoiceEnd extends UDPPacket {

	@Override
	public byte id() {
		return 2;
	}

	@Override
	public void write(ByteArrayDataOutput out) {
	}

}
