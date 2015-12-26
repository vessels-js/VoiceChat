/**
 *  Copyright (c) 2015, Gliby.
 *  * http://www.gliby.net/
 */
package net.gliby.voicechat.common.networking.packets;

import io.netty.buffer.ByteBuf;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.MinecraftPacket;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MinecraftClientEntityPositionPacket extends MinecraftPacket implements IMessageHandler<MinecraftClientEntityPositionPacket, IMessage> {

	private int entityID;
	private double x, y, z;

	public MinecraftClientEntityPositionPacket() {}

	public MinecraftClientEntityPositionPacket(int entityID, double x, double y, double z) {
		this.entityID = entityID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityID = buf.readInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
	}

	@Override
	public IMessage onMessage(MinecraftClientEntityPositionPacket packet, MessageContext ctx) {
		if(VoiceChat.getProxyInstance().getClientNetwork().isConnected())
			VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient().handleEntityPosition(packet.entityID, packet.x, packet.y, packet.z);
		return null;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}
}
