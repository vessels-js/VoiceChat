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

public class MinecraftClientVoiceEndPacket extends MinecraftPacket implements IMessageHandler<MinecraftClientVoiceEndPacket, IMessage> {

	int entityID;

	public MinecraftClientVoiceEndPacket() {}

	public MinecraftClientVoiceEndPacket(int entityID) {
		this.entityID = entityID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityID = buf.readInt();
	}

	@Override
	public IMessage onMessage(MinecraftClientVoiceEndPacket packet, MessageContext ctx) {
		if(VoiceChat.getProxyInstance().getClientNetwork().isConnected())
			VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient().handleEnd(packet.entityID);
		return null;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
	}

}
