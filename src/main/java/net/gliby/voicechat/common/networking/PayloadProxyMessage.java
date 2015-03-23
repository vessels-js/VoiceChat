package net.gliby.voicechat.common.networking;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PayloadProxyMessage implements IMessage {

	public byte[] payload;
	public int payloadLength;

	public PayloadProxyMessage() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		payloadLength = buf.readInt();
		payload = buf.readBytes(payloadLength).array();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(payload.length);
		buf.writeBytes(payload);
	}
}
