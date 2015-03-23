package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.client.networking.ClientPacketHandler;
import net.gliby.voicechat.common.networking.PayloadProxyMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClientEntityPosition extends PayloadProxyMessage implements IMessageHandler<PacketClientEntityPosition, IMessage> {

	public PacketClientEntityPosition() {
	}

	public PacketClientEntityPosition(byte[] payload) {
		this.payload = payload;
		this.payloadLength = payload.length;
	}

	@Override
	public IMessage onMessage(PacketClientEntityPosition message, MessageContext ctx) {
		ClientPacketHandler.handleEntityPosition(message.payload);
		return null;
	}

}
