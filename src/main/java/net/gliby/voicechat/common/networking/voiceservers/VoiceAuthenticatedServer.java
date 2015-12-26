package net.gliby.voicechat.common.networking.voiceservers;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;

public abstract class VoiceAuthenticatedServer extends VoiceServer {
	public Map<String, EntityPlayerMP> waitingAuth = new HashMap<String, EntityPlayerMP>();

	public abstract void closeConnection(int id);
}
