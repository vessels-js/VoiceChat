package net.gliby.voicechat.common.api;

import cpw.mods.fml.common.eventhandler.EventBus;

public class VoiceChatAPI {

	private static VoiceChatAPI instance = new VoiceChatAPI();
	
	private EventBus eventBus = new EventBus();
	
	public static VoiceChatAPI instance() {
		return instance;
	}
	
	public EventBus bus() {
		return eventBus;
	}
} 
