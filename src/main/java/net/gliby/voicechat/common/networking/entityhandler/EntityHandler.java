package net.gliby.voicechat.common.networking.entityhandler;

import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.ServerStream;
import net.gliby.voicechat.common.networking.ServerStreamManager;
import net.gliby.voicechat.common.networking.voiceservers.VoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.voiceservers.VoiceServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class EntityHandler {

	private final ServerStreamManager dataManager;
	private final VoiceChatServer voiceChat;

	public EntityHandler(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
		this.dataManager = voiceChat.serverNetwork.dataManager;
	}

	public void connected(EntityPlayerMP speaker) {
	}

	public void disconnected(int id) {
		final ServerStream stream = dataManager.streaming.get(id);
		if (stream != null) dataManager.killStream(stream);
		final VoiceServer voiceServer = voiceChat.getVoiceServer();
		if (voiceServer instanceof VoiceAuthenticatedServer) ((VoiceAuthenticatedServer) voiceServer).closeConnection(id);
	}

	public void whileSpeaking(ServerStream stream, EntityPlayerMP speaker, EntityPlayerMP receiver) {
		if (!stream.entities.contains(receiver.getEntityId())) {
			dataManager.giveEntity(receiver, speaker);
			stream.entities.add(receiver.getEntityId());
		}
	}

}
