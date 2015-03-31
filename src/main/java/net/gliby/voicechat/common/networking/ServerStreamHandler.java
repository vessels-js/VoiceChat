package net.gliby.voicechat.common.networking;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.api.events.ServerStreamEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ServerStreamHandler {

	VoiceChatServer voiceChat;
	public ServerStreamHandler(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
		System.out.println("Registered!");
	}

	@SubscribeEvent
	public void createdStream(ServerStreamEvent.StreamCreated event) {
		int chatMode = voiceChat.getServerSettings().getDefaultChatMode();
		if (event.streamManager.chatModeMap.containsKey(event.voiceLet.player.getPersistentID())) 
			chatMode = event.streamManager.chatModeMap.get(event.voiceLet.player.getPersistentID());
		event.stream.chatMode = chatMode;
	}

	@SubscribeEvent
	public void feedStream(ServerStreamEvent.StreamFeed event) {
		if (event.stream.dirty)
			if (event.streamManager.chatModeMap.containsKey(event.stream.player.getPersistentID())) event.stream.chatMode = event.streamManager.chatModeMap.get(event.stream.player.getPersistentID());

		switch(event.stream.chatMode) {
		case 0 :
			event.streamManager.feedWithinEntityWithRadius(event.stream, event.voiceLet, voiceChat.getServerSettings().getSoundDistance());
			break;
		case 1 :
			event.streamManager.feedStreamToWorld(event.stream, event.voiceLet);
			break;
		case 2 :
			event.streamManager.feedStreamToAllPlayers(event.stream, event.voiceLet);
			break;
		}
	}

	@SubscribeEvent
	public void killStream(ServerStreamEvent.StreamDestroyed event) {

	}
}
