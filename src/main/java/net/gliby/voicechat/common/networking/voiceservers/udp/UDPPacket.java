package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;

/** Type is who sends. **/
public abstract class UDPPacket {

	public abstract byte id();

	public abstract void write(ByteArrayDataOutput out);

}
