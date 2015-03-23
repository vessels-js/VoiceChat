package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.client.networking.ClientPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClientVoiceServer extends PayloadProxyMessage implements IMessageHandler<PacketClientVoiceServer, IMessage> {

	public PacketClientVoiceServer() {
	}

	public PacketClientVoiceServer(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketClientVoiceServer message, MessageContext ctx) {
		ClientPacketHandler.handleVoiceServer(message.payload);
		return null;
	}

}
