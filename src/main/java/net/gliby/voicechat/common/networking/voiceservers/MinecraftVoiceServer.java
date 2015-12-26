package net.gliby.voicechat.common.networking.voiceservers;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityPositionPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoicePacket;
import net.minecraft.entity.player.EntityPlayerMP;

public class MinecraftVoiceServer extends VoiceServer {

	private final VoiceChatServer voiceChat;

	public MinecraftVoiceServer(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
	}

	@Override
	public EnumVoiceNetworkType getType() {
		return EnumVoiceNetworkType.MINECRAFT;
	}

	@Override
	public void handleVoiceData(EntityPlayerMP player, byte[] data, byte divider, int id, boolean end) {
		voiceChat.getServerNetwork().getDataManager().addQueue(player, data, divider, id, end);
	}

	@Override
	public void sendChunkVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples, byte chunkSize, byte volume) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientVoicePacket(chunkSize, samples, entityID, direct, volume), player);
	}

	@Override
	public void sendEntityPosition(EntityPlayerMP player, int entityID, double x, double y, double z) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientEntityPositionPacket(entityID, x, y, z), player);
	}

	@Override
	public void sendVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples, byte volume) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientVoicePacket((byte) samples.length, samples, entityID, direct, volume), player);
	}

	@Override
	public void sendVoiceEnd(EntityPlayerMP player, int id) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientVoiceEndPacket(id), player);
	}

	@Override
	public boolean start() {
		VoiceChatServer.getLogger().warn("Minecraft Networking is not recommended and is consider very slow, please setup UDP.");
		return true;
	}

	@Override
	public void stop() {
	}
}
