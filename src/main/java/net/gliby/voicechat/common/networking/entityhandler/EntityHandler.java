package net.gliby.voicechat.common.networking.entityhandler;

import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.DataManager;
import net.gliby.voicechat.common.networking.DataStream;
import net.gliby.voicechat.common.networking.voiceservers.VoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.voiceservers.VoiceServer;
import net.minecraft.entity.player.EntityPlayerMP;

public class EntityHandler {

	private final DataManager dataManager;
	private final VoiceChatServer voiceChat;

	public EntityHandler(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
		this.dataManager = voiceChat.serverNetwork.dataManager;
	}

	public void connected(EntityPlayerMP speaker) {
		System.out.println("Sending connection to client: " + speaker);
	}

	public void disconnected(int id) {
		final DataStream stream = dataManager.streaming.get(id);
		if (stream != null) dataManager.killStream(stream);
		final VoiceServer voiceServer = voiceChat.getVoiceServer();
		if (voiceServer instanceof VoiceAuthenticatedServer) ((VoiceAuthenticatedServer) voiceServer).closeConnection(id);
	}

	public void whileSpeaking(DataStream stream, EntityPlayerMP speaker, EntityPlayerMP receiver) {
		if (!stream.entities.contains(receiver.getEntityId())) {
			dataManager.giveEntity(receiver, speaker);
			stream.entities.add(receiver.getEntityId());
		}
	}

}
