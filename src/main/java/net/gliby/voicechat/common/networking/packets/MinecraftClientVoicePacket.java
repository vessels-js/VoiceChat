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

public class MinecraftClientVoicePacket extends MinecraftPacket implements IMessageHandler<MinecraftClientVoicePacket, IMessage> {
	byte divider;
	byte[] samples;
	int entityID;
	boolean direct;
	byte volume;

	public MinecraftClientVoicePacket() {
	}

	public MinecraftClientVoicePacket(byte divider, byte[] samples, int entityID, boolean direct, byte volume) {
		this.divider = divider;
		this.samples = samples;
		this.entityID = entityID;
		this.direct = direct;
		this.volume = volume;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.volume = buf.readByte();
		this.divider = buf.readByte();
		this.entityID = buf.readInt();
		this.direct = buf.readBoolean();
		this.samples = new byte[buf.readableBytes()];
		buf.readBytes(samples);
	}

	@Override
	public IMessage onMessage(MinecraftClientVoicePacket packet, MessageContext ctx) {
		if (VoiceChat.getProxyInstance().getClientNetwork().isConnected()) VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient().handlePacket(packet.entityID, packet.samples, packet.divider, packet.direct, packet.volume);
		return null;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeByte(volume);
		buf.writeByte(divider);
		buf.writeInt(entityID);
		buf.writeBoolean(direct);
		buf.writeBytes(samples);
	}

}
