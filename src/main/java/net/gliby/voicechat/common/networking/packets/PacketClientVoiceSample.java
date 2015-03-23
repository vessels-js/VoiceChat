package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.client.networking.ClientPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClientVoiceSample extends PayloadProxyMessage implements IMessageHandler<PacketClientVoiceSample, IMessage> {

	public PacketClientVoiceSample() {
	}

	public PacketClientVoiceSample(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketClientVoiceSample message, MessageContext ctx) {
		ClientPacketHandler.handleVoiceData(message.payload);
		return null;
	}

}
