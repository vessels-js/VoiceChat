package net.gliby.voicechat.common.api;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.ServerStreamHandler;
import cpw.mods.fml.common.eventhandler.EventBus;


public class VoiceChatAPI {
	private static VoiceChatAPI instance;

	public static VoiceChatAPI instance() {
		return instance;
	}

	private ServerStreamHandler handler;

	private EventBus eventBus;

	/**
	 * Only call from init.
	 * @return
	 */
	public EventBus bus() {
		return eventBus;
	}

	public void init() {
		VoiceChatAPI.instance = this;
		this.eventBus = new EventBus();
		bus().register(handler = new ServerStreamHandler(VoiceChat.getServerInstance()));
	}

	/**
	 * Only needed if you want to overwrite ServerStreamHandler. There's a bug in EventBus, so to counter that bug the eventbus is reset.
	 * This should be set in pre-init.
	 */
	public void setCustomStreamHandler(Object eventHandler) {
		eventBus = null;
		eventBus = new EventBus();
		eventBus.register(eventHandler);
	}
}
