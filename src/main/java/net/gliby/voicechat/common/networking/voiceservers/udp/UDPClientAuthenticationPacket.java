package net.gliby.voicechat.common.networking.voiceservers.udp;

import java.io.UnsupportedEncodingException;

import com.google.common.io.ByteArrayDataOutput;

public class UDPClientAuthenticationPacket extends UDPPacket {

	String hash;

	public UDPClientAuthenticationPacket(String hash) {
		this.hash = hash;
	}

	@Override
	public byte id() {
		return 0;
	}

	@Override
	public void write(ByteArrayDataOutput out) {
		try {
			UDPByteUtilities.writeBytes(hash.getBytes("UTF-8"), out);
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

}
