package net.gliby.voicechat.common.networking.voiceservers;

import net.gliby.voicechat.VoiceChat;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class VoiceServer implements Runnable {

	public abstract EnumVoiceNetworkType getType();

	public abstract void handleVoiceData(EntityPlayerMP player, byte[] data, byte divider, int id, boolean end);

	@Override
	public final void run() {
		VoiceChat.getLogger().info(this.start() ? "Started [" + getType().name + "] Server." : "Failed to start [" + getType().name + "] Server.");
	}

	public abstract void sendChunkVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples, byte chunkSize, byte volume);

	public abstract void sendEntityPosition(EntityPlayerMP player, int entityID, double x, double y, double z);

	public abstract void sendVoiceData(EntityPlayerMP player, int entityID, boolean global, byte[] samples, byte volume);

	public abstract void sendVoiceEnd(EntityPlayerMP player, int entityID);

	public abstract boolean start();

	public abstract void stop();
}
