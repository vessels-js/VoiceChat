/**
 *  Copyright (c) 2015, Gliby.
 *  * http://www.gliby.net/
 */
package net.gliby.voicechat.common.networking.packets;

import io.netty.buffer.ByteBuf;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.MinecraftPacket;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class MinecraftClientVoiceAuthenticatedServer extends MinecraftPacket implements IMessageHandler<MinecraftClientVoiceAuthenticatedServer, IMessage> {

	boolean showVoicePlates, showVoiceIcons;
	int minQuality, maxQuality, bufferSize, soundDistance, voiceServerType, udpPort;
	String hash, ip;

	public MinecraftClientVoiceAuthenticatedServer() {
	}

	public MinecraftClientVoiceAuthenticatedServer(boolean canShowVoicePlates, boolean canShowVoiceIcons, int minQuality, int maxQuality, int bufferSize, int soundDistance, int voiceServerType, int udpPort, String hash, String ip) {
		this.showVoicePlates = canShowVoicePlates;
		this.showVoiceIcons = canShowVoiceIcons;
		this.minQuality = minQuality;
		this.maxQuality = maxQuality;
		this.bufferSize = bufferSize;
		this.soundDistance = soundDistance;
		this.voiceServerType = voiceServerType;
		this.udpPort = udpPort;
		this.hash = hash;
		this.ip = ip;
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
		this.udpPort = buf.readInt();
		this.hash = ByteBufUtils.readUTF8String(buf);
		this.ip = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public IMessage onMessage(final MinecraftClientVoiceAuthenticatedServer packet, MessageContext ctx) {
		Minecraft.getMinecraft().func_152344_a(new Runnable() {

			@Override
			public void run() {
				VoiceChat.getProxyInstance().getClientNetwork().handleVoiceAuthenticatedServer(packet.showVoicePlates, packet.showVoiceIcons, packet.minQuality, packet.maxQuality, packet.bufferSize, packet.soundDistance, packet.voiceServerType, packet.udpPort, packet.hash, packet.ip);
			}
		});

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
		buf.writeInt(udpPort);
		ByteBufUtils.writeUTF8String(buf, hash);
		ByteBufUtils.writeUTF8String(buf, ip);
	}

}
