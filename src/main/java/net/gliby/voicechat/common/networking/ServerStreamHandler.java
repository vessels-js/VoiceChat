package net.gliby.voicechat.common.networking;

import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.api.events.ServerStreamEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
//Does the same as default, but in API.
public class ServerStreamHandler {

	VoiceChatServer voiceChat;
	public ServerStreamHandler(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
	}

	@SubscribeEvent
	public void createdStream(ServerStreamEvent.StreamCreated event) {
		//Get default chat mode from settings.
		int chatMode = voiceChat.getServerSettings().getDefaultChatMode();
		//Check if players have special chat modes, if yes overwrite chatMode
		if (event.streamManager.chatModeMap.containsKey(event.voiceLet.player.getPersistentID()))
			chatMode = event.streamManager.chatModeMap.get(event.voiceLet.player.getPersistentID());
		//Set chat mode.
		event.stream.chatMode = chatMode;
	}

	@SubscribeEvent
	public void feedStream(ServerStreamEvent.StreamFeed event) {
		//If something is out of order, stream is marked as dirty and updated.
		if (event.stream.dirty) {
			if (event.streamManager.chatModeMap.containsKey(event.stream.player.getPersistentID())) event.stream.chatMode = event.streamManager.chatModeMap.get(event.stream.player.getPersistentID());
		}
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
