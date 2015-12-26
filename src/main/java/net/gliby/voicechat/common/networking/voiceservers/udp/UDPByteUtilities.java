package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class UDPByteUtilities {

	public static byte[] readBytes(ByteArrayDataInput in) {
		final byte[] data = new byte[in.readInt()];
		for (int i = 0; i < data.length; i++)
			data[i] = in.readByte();
		return data;
	}

	public static void writeBytes(byte[] data, ByteArrayDataOutput out) {
		out.writeInt(data.length);
		for (int i = 0; i < data.length; i++)
			out.writeByte(data[i]);
	}
}
