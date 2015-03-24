package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.client.networking.ClientPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClientChunkVoiceSample extends PayloadProxyMessage implements IMessageHandler<PacketClientChunkVoiceSample, IMessage> {

	public PacketClientChunkVoiceSample() {
	}

	public PacketClientChunkVoiceSample(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketClientChunkVoiceSample message, MessageContext ctx) {
		ClientPacketHandler.handleChunkVoiceData(message.payload);
		return null;
	}

}