package net.gliby.voicechat.common.api;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.api.examples.ExampleStreamHandlerOnlyOP;
import net.gliby.voicechat.common.networking.ServerStreamHandler;
import cpw.mods.fml.common.eventhandler.EventBus;


public class VoiceChatAPI {
	private ServerStreamHandler handler;

	private static VoiceChatAPI instance;

	private EventBus eventBus;

	public void init() {
		this.instance = this;
		this.eventBus = null;
		this.eventBus = new EventBus();
		bus().register(handler = new ServerStreamHandler(VoiceChat.getServerInstance()));
		setCustomStreamHandler(new ExampleStreamHandlerOnlyOP());
	}

	public static VoiceChatAPI instance() {
		return instance;
	}

	/**
	 * Only call from init.
	 * @return
	 */
	public EventBus bus() {
		return eventBus;
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
