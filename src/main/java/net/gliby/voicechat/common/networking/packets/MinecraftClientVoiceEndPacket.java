/**
 *  Copyright (c) 2015, Gliby.
 *  * http://www.gliby.net/
 */
package net.gliby.voicechat.common.networking.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.common.networking.MinecraftPacket;

public class MinecraftClientVoiceEndPacket extends MinecraftPacket implements IMessageHandler<MinecraftClientVoiceEndPacket, IMessage> {

	public MinecraftClientVoiceEndPacket() {}
	
	int entityID;
	
	public MinecraftClientVoiceEndPacket(int entityID) {
		this.entityID = entityID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
	}
	
	@Override
	public IMessage onMessage(MinecraftClientVoiceEndPacket packet, MessageContext ctx) {
		VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient().handleEnd(packet.entityID);
		return null;
	}

}
