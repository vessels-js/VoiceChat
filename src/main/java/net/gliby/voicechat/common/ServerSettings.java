package net.gliby.voicechat.common;

import java.io.File;
import java.io.UnsupportedEncodingException;

import net.gliby.voicechat.VoiceChat;

// TODO NEXT-UPDATE Overhaul settings, one abstract implementation for client/server.
public class ServerSettings {
	private ServerConfiguration configuration;
	private int soundDist = 64;
	private int udpPort = 0;
	private int bufferSize = 128;
	private int advancedNetworkType = 0;
	public int positionUpdateRate = 40;
	private int defaultChatMode = 0;
	private int minimumQuality = 0;
	private int maximumQuality = 9;
	private boolean canShowVoiceIcons = true, canShowVoicePlates = true, behindProxy;
	private int modPackID = 1;

	public ServerSettings(VoiceChatServer voiceChatServer) {
	}

	public boolean canShowVoiceIcons() {
		return canShowVoiceIcons;
	}

	public final boolean canShowVoicePlates() {
		return canShowVoicePlates;
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

	/**
	 * @return
	 */
	protected int getModPackID() {
		return this.modPackID;
	}

	public final int getSoundDistance() {
		return soundDist;
	}

	public final int getUDPPort() {
		return udpPort;
	}

	public final boolean isUsingProxy() {
		return behindProxy;
	}

	public void preInit(File file) {
		configuration = new ServerConfiguration(this, file);
		new Thread(new Runnable() {

			@Override
			public void run() {
				configuration.init();

			}
		}, "Configuration Process").start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				final ModPackSettings settings = new ModPackSettings();
				try {
					final ModPackSettings.GVCModPackInstructions newDefault = settings.init();
					if (newDefault.ID != getModPackID()) {
						VoiceChat.getLogger().info("Modpack defaults applied, original settings overwritten.");
						setCanShowVoicePlates(newDefault.SHOW_PLATES);
						setCanShowVoiceIcons(newDefault.SHOW_PLAYER_ICONS);
						setModPackID(newDefault.ID);
						configuration.save();
					}
				} catch (final UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			}
		}, "Mod Pack Overwrite Process").start();
	}

	public void setAdvancedNetworkType(int type) {
		this.advancedNetworkType = type;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public final void setCanShowVoiceIcons(boolean canShowVoiceIcons) {
		this.canShowVoiceIcons = canShowVoiceIcons;
	}

	public void setCanShowVoicePlates(boolean canShowVoicePlates) {
		this.canShowVoicePlates = canShowVoicePlates;
	}

	public void setDefaultChatMode(int defaultChatMode) {
		this.defaultChatMode = defaultChatMode;
	}

	public void setModPackID(int id) {
		this.modPackID = id;
	}

	public void setQuality(int x0, int x1) {
		this.minimumQuality = x0;
		this.maximumQuality = x1;
	}

	public void setSoundDistance(int dist) {
		this.soundDist = dist;
	}

	public void setUDPPort(int udp) {
		this.udpPort = udp;
	}

	public void setUsingProxy(boolean val) {
		this.behindProxy = val;
	}
}
