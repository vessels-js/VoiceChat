package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.client.networking.ClientPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClientVoiceEnd extends PayloadProxyMessage implements IMessageHandler<PacketClientVoiceEnd, IMessage> {

	public PacketClientVoiceEnd() {
	}

	public PacketClientVoiceEnd(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketClientVoiceEnd message, MessageContext ctx) {
		ClientPacketHandler.handleVoiceEnd(message.payload);
		return null;
	}

}
