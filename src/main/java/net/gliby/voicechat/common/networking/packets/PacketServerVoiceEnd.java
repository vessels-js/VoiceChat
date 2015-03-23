package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.common.networking.CommonPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketServerVoiceEnd extends PayloadProxyMessage implements IMessageHandler<PacketServerVoiceEnd, IMessage> {

	public PacketServerVoiceEnd() {
	}

	public PacketServerVoiceEnd(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketServerVoiceEnd message, MessageContext ctx) {
		CommonPacketHandler.handleVoiceData(message.payload, ctx.getServerHandler().playerEntity, true);
		return null;
	}

}
