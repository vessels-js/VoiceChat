package net.gliby.voicechat.client.keybindings;

import net.gliby.voicechat.client.VoiceChatClient;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Type;
import cpw.mods.fml.relauncher.Side;

public class KeyTickHandler {
	VoiceChatClient voiceChat;

	public KeyTickHandler(VoiceChatClient voiceChat) {
		this.voiceChat = voiceChat;
	}

	@SubscribeEvent
	public void tick(TickEvent event) {
		if (event.type == Type.PLAYER) {
			if (event.side == Side.CLIENT) {
				voiceChat.keyManager.keyEvent(null);
			}
		}
	}
}
