package net.gliby.voicechat.client;

import ovr.paulscode.sound.libraries.LibraryLWJGLOpenAL;
import net.gliby.voicechat.VoiceChat;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class UpdatedSoundManager {

	/**
	 * Replaces Minecraft's current audio library with modified LWJGLOpenAL library, fixes issues with streaming audio.
	 *
	 * @param event
	 **/
	public void init(FMLPreInitializationEvent event) {
		for (final ModContainer mod : Loader.instance().getModList()) {
			if (mod.getModId().equals("soundfilters")) {
				VoiceChat.getLogger().info("Found Sound Filters mod, won't replace OpenAL library.");
				return;
			}
		}
		VoiceChat.getLogger().info("Trying to replace Library");
		try {
			SoundSystemConfig.removeLibrary(paulscode.sound.libraries.LibraryLWJGLOpenAL.class);
			SoundSystemConfig.addLibrary(ovr.paulscode.sound.libraries.LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
		} catch (final Exception e) {
			VoiceChat.getLogger().info("Failed to replaced sound libraries, you won't be hearing any voice chat.");
			e.printStackTrace();
		}
		VoiceChat.getLogger().info("Successfully replaced sound libraries.");
	}

}
