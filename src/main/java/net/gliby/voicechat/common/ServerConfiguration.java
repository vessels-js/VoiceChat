package net.gliby.voicechat.common;

import java.io.File;
import java.io.IOException;

import net.gliby.gman.JINIFile;
import net.gliby.voicechat.VoiceChat;

public class ServerConfiguration {
	private static final String MODPACK_ID = "ModPackID", BEHIND_PROXY = "ServerBehindProxy", SHOW_VOICEPLATES = "ShowVoicePlates", SHOW_PLAYERICONS = "ShowPlayerIcons", MINIMUM_QUALITY = "MinimumQuality", MAXIMUM_QUALITY = "MaximumQuality", SOUND_DISTANCE = "SoundDistance", DEFAULT_CHAT_MODE = "DefaultChatMode", UDP_PORT = "UDPPort", NETWORK_TYPE = "NetworkType", BUFFER_SIZE = "BufferSize";
	private final File location;
	private final ServerSettings settings;
	private JINIFile init;

	public ServerConfiguration(ServerSettings settings, File file) {
		this.settings = settings;
		this.location = file;
	}

	public void init() {
		if (!load()) {
			VoiceChat.getLogger().info("No Configuration file found on server, will create one with default settings.");
			if (save()) VoiceChat.getLogger().info("Created Configuration file with default settings on server.");
		}
	}

	private boolean load() {
		if (location.exists()) {
			try {
				this.init = new JINIFile(location);
				settings.setSoundDistance(init.ReadFloat("Game", SOUND_DISTANCE, Float.valueOf(64)).intValue());
				settings.setDefaultChatMode(init.ReadInteger("Game", DEFAULT_CHAT_MODE, 0));
				settings.setCanShowVoiceIcons(init.ReadBool("Game", SHOW_PLAYERICONS, true));
				settings.setCanShowVoicePlates(init.ReadBool("Game", SHOW_VOICEPLATES, true));
				settings.setAdvancedNetworkType(init.ReadInteger("Network", NETWORK_TYPE, 1));
				settings.setUDPPort(init.ReadInteger("Network", UDP_PORT, Integer.valueOf(settings.getUDPPort()).intValue()));
				settings.setQuality(init.ReadInteger("Network", MINIMUM_QUALITY, Integer.valueOf(settings.getMinimumSoundQuality()).intValue()), init.ReadInteger("Network", MAXIMUM_QUALITY, Integer.valueOf(settings.getMaximumSoundQuality()).intValue()));
				settings.setBufferSize(init.ReadInteger("Network", BUFFER_SIZE, Integer.valueOf(settings.getBufferSize()).intValue()));
				settings.setUsingProxy(init.ReadBool("Network", BEHIND_PROXY, false));
				settings.setModPackID(init.ReadInteger("Miscellaneous", MODPACK_ID, 1));
				return true;
			} catch (final Exception e) {
				VoiceChat.getLogger().fatal("Couldn't read configuration file, fix it or delete it. Default settings being used.");
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean save() {
		if (init == null || !location.exists()) try {
			this.init = new JINIFile(location);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		this.init.WriteFloat("Game", SOUND_DISTANCE, settings.getSoundDistance());
		this.init.WriteComment("Game", "Sound Distance is proximity in which players can hear you! @Whiskey.");
		this.init.WriteInteger("Game", DEFAULT_CHAT_MODE, settings.getDefaultChatMode());
		this.init.WriteComment("Game", DEFAULT_CHAT_MODE + ": 0 - distance based, 1 - world based, 2 - global.");
		this.init.WriteBool("Game", SHOW_PLAYERICONS, settings.canShowVoiceIcons());
		this.init.WriteBool("Game", SHOW_VOICEPLATES, settings.canShowVoicePlates());
		this.init.WriteComment("Game", SHOW_PLAYERICONS + ", if false - players won't see icons when someone talks; " + SHOW_VOICEPLATES + ", if false - players won't see player names(voice plates) on their screens.");
		this.init.WriteInteger("Network", NETWORK_TYPE, settings.getAdvancedNetworkType());
		this.init.WriteComment("Network", NETWORK_TYPE + ", 0 - Minecraft Network, 1 - UDP Network. UDP networking improves performance and network speeds extensively, it is highly recommended.");
		this.init.WriteInteger("Network", UDP_PORT, settings.getUDPPort());
		this.init.WriteComment("Network", "If " + UDP_PORT + " is set to 0, minecraft's own port will be used, this cannot be the same as query port! Change the network type to 0, if you can't port forward to a UDP custom port.");
		this.init.WriteInteger("Network", MINIMUM_QUALITY, settings.getMinimumSoundQuality());
		this.init.WriteInteger("Network", MAXIMUM_QUALITY, settings.getMaximumSoundQuality());
		this.init.WriteComment("Network", "Sound Quality level, starting from 0 to 9. If you want to reduce bandwidth, make the maximum quality smaller. If you'd like to make sound quality great, set the minimum quality to a high value.");
		this.init.WriteInteger("Network", BUFFER_SIZE, settings.getBufferSize());
		this.init.WriteComment("Network", BUFFER_SIZE + " - recommended buffer size is 128, max 500, going any higher will cause issues. Buffer Size determines voice data amount in a single packet, big buffers equal in bigger latency. If you are experiencing stuttering with players, or having network lag - set this to a higher value. ");
		this.init.WriteBool("Network", BEHIND_PROXY, settings.isUsingProxy());
		this.init.WriteComment("Network", BEHIND_PROXY + ": if server is behind a proxy, like bungeecord, enable this.");
		this.init.WriteInteger("Miscellaneous", MODPACK_ID, settings.getModPackID());
		return init.UpdateFile();
	}

}
