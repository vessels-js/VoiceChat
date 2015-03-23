package net.gliby.voicechat.common;

import java.io.File;
//TODO NEXT-UPDATE Overhaul settings, one abstract implementation for client/server.
public class ServerSettings {
	private ServerConfiguration configuration;
	private VoiceChatServer voiceChat;
	private int soundDist = 64;
	private int udpPort = 0;
	private int bufferSize = 128;
	private int advancedNetworkType = 1;
	public int positionUpdateRate = 40;
	private int defaultChatMode = 0;
	private int minimumQuality = 0;
	private int maximumQuality = 9;
	private boolean canShowVoiceIcons = true, canShowVoicePlates = true;
	
	public ServerSettings(VoiceChatServer voiceChatServer) {
		this.voiceChat = voiceChatServer;
	}

	public final int getAdvancedNetworkType() {
		return advancedNetworkType;
	}

	public final int getBufferSize() {
		return bufferSize;
	}

	public final int getDefaultChatMode() {
		return defaultChatMode;
	}

	public final int getMaximumSoundQuality() {
		return maximumQuality;
	}

	public final int getMinimumSoundQuality() {
		return minimumQuality;
	}

	public final int getSoundDistance() {
		return soundDist;
	}

	public final int getUDPPort() {
		return udpPort;
	}

	public final boolean isUsingProxy() {
		return false;
	}

	public void preInit(File file) {
		configuration = new ServerConfiguration(this, file);
		configuration.init();
	}
	
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setAdvancedNetworkType(int type) {
		this.advancedNetworkType = type;
	}

	public void setDefaultChatMode(int defaultChatMode) {
		this.defaultChatMode = defaultChatMode;
	}

	public void setSoundDistance(int dist) {
		this.soundDist = dist;
	}

	public void setUDPPort(int udp) {
		this.udpPort = udp;
	}

	public void setQuality(int x0, int x1) {
		this.minimumQuality = x0;
		this.maximumQuality  = x1;
	}
	
	public boolean canShowVoiceIcons() {
		return canShowVoiceIcons;
	}

	public final void setCanShowVoiceIcons(boolean canShowVoiceIcons) {
		this.canShowVoiceIcons = canShowVoiceIcons;
	}

	public final boolean canShowVoicePlates() {
		return canShowVoicePlates;
	}

	public void setCanShowVoicePlates(boolean canShowVoicePlates) {
		this.canShowVoicePlates = canShowVoicePlates;
	}
}
