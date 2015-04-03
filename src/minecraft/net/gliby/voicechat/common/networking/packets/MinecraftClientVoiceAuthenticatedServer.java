/**
 *  Copyright (c) 2015, Gliby.
 *  * http://www.gliby.net/
 */
package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.MinecraftPacket;
import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class MinecraftClientVoiceAuthenticatedServer extends MinecraftPacket<MinecraftClientVoiceAuthenticatedServer> {

	boolean showVoicePlates, showVoiceIcons;
	int minQuality, maxQuality, bufferSize, soundDistance, voiceServerType, udpPort;
	String hash, ip;

	public MinecraftClientVoiceAuthenticatedServer() {}

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
	public void fromBytes(ByteArrayDataInput buf) {
		this.showVoicePlates = buf.readBoolean();
		this.showVoiceIcons = buf.readBoolean();
		this.minQuality = buf.readInt();
		this.maxQuality = buf.readInt();
		this.bufferSize = buf.readInt();
		this.soundDistance = buf.readInt();
		this.voiceServerType = buf.readInt();
		this.udpPort = buf.readInt();
		this.hash = buf.readUTF();
		this.ip = buf.readUTF();
	}

	//TODO support string
	@Override
	public void onMessage(MinecraftClientVoiceAuthenticatedServer packet, EntityPlayerMP player) {
		VoiceChat.getProxyInstance().getClientNetwork().handleVoiceAuthenticatedServer(packet.showVoicePlates, packet.showVoiceIcons, packet.minQuality, packet.maxQuality, packet.bufferSize, packet.soundDistance, packet.voiceServerType, packet.udpPort, packet.hash, packet.ip);
	}

	@Override
	public void toBytes(ByteArrayDataOutput buf) {
		buf.writeBoolean(showVoicePlates);
		buf.writeBoolean(showVoiceIcons);
		buf.writeInt(minQuality);
		buf.writeInt(maxQuality);
		buf.writeInt(bufferSize);
		buf.writeInt(soundDistance);
		buf.writeInt(voiceServerType);
		buf.writeInt(udpPort);
		buf.writeUTF(hash);
		buf.writeUTF(ip);
	}

	@Override
	public String getChannel() {
		return "GVC-AS";
	}

}
