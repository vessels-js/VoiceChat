package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.common.networking.CommonPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketServerVoiceSample extends PayloadProxyMessage implements IMessageHandler<PacketServerVoiceSample, IMessage> {

	public PacketServerVoiceSample() {
	}

	public PacketServerVoiceSample(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketServerVoiceSample message, MessageContext ctx) {
		CommonPacketHandler.handleVoiceData(message.payload, ctx.getServerHandler().playerEntity, false);
		return null;
	}

}
