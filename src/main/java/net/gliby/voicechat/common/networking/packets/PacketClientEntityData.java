package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.client.networking.ClientPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClientEntityData extends PayloadProxyMessage implements IMessageHandler<PacketClientEntityData, IMessage> {

	public PacketClientEntityData() {
	}

	public PacketClientEntityData(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketClientEntityData message, MessageContext ctx) {
		ClientPacketHandler.handleEntityData(message.payload);
		return null;
	}

}