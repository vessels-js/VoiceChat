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

public class MinecraftClientVoiceServerPacket extends MinecraftPacket implements IMessageHandler<MinecraftClientVoiceServerPacket, IMessage> {

	boolean showVoicePlates, showVoiceIcons;
	int minQuality, maxQuality, bufferSize, soundDistance, voiceServerType;

	public MinecraftClientVoiceServerPacket() {}

	public MinecraftClientVoiceServerPacket(boolean canShowVoicePlates, boolean canShowVoiceIcons, int minQuality, int maxQuality, int bufferSize, int soundDistance, int voiceServerType) {
		this.showVoicePlates = canShowVoicePlates;
		this.showVoiceIcons = canShowVoiceIcons;
		this.minQuality = minQuality;
		this.maxQuality = maxQuality;
		this.bufferSize = bufferSize;
		this.soundDistance = soundDistance;
		this.voiceServerType = voiceServerType;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.showVoicePlates = buf.readBoolean();
		this.showVoiceIcons = buf.readBoolean();
		this.minQuality = buf.readInt();
		this.maxQuality = buf.readInt();
		this.bufferSize = buf.readInt();
		this.soundDistance = buf.readInt();
		this.voiceServerType = buf.readInt();
	}

	@Override
	public IMessage onMessage(MinecraftClientVoiceServerPacket packet, MessageContext ctx) {
		VoiceChat.getProxyInstance().getClientNetwork().handleVoiceServer(packet.showVoicePlates, packet.showVoiceIcons, packet.minQuality, packet.maxQuality, packet.bufferSize, packet.soundDistance, packet.voiceServerType);
		return null;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(showVoicePlates);
		buf.writeBoolean(showVoiceIcons);
		buf.writeInt(minQuality);
		buf.writeInt(maxQuality);
		buf.writeInt(bufferSize);
		buf.writeInt(soundDistance);
		buf.writeInt(voiceServerType);
	}

}
