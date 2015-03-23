package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.client.networking.ClientPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClientVoiceServerAuth extends PayloadProxyMessage implements IMessageHandler<PacketClientVoiceServerAuth, IMessage> {

	public PacketClientVoiceServerAuth() {
	}

	public PacketClientVoiceServerAuth(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketClientVoiceServerAuth message, MessageContext ctx) {
		ClientPacketHandler.handleVoiceServerAuthentication(message.payload);
		return null;
	}

}
