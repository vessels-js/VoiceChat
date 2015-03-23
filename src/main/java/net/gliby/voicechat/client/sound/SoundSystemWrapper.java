package net.gliby.voicechat.client.sound;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.opengl.Display;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * Starting from 1.7+ Minecraft's hidden paulscode SoundSystem(actual sound
 * engine) behind a bunch of sound handlers, SoundSystemWrapper job is to expose
 * those hidden functions.
 **/
public class SoundSystemWrapper {

	private SoundManager soundManager;

	public SoundSystem sndSystem;
	public SoundSystemWrapper(SoundHandler soundHandler) {
		this.soundManager = ReflectionHelper.getPrivateValue(SoundHandler.class, soundHandler, 5);
		this.sndSystem = ReflectionHelper.getPrivateValue(SoundManager.class, soundManager, 4);
	}

	public void feedRawAudioData(String identifier, byte[] bs) {
		fix();
		sndSystem.feedRawAudioData(identifier, bs);
	}

	private void fix() {
		while (sndSystem.randomNumberGenerator == null) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			refresh();
			VoiceChatClient.getSoundManager().reload();
		}
	}

	private void fixThreaded() {
		if(sndSystem.randomNumberGenerator == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					fix();
				}
			}).start();
		}
	}

	public void flush(String identifier) {
		fix();
		sndSystem.flush(identifier);
	}

	@SubscribeEvent
	public void loadEvent(SoundLoadEvent event) {
		fixThreaded();
	}

	public boolean playing(String string) {
		fix();
		return sndSystem.playing(string);
	}

	public void rawDataStream(AudioFormat format, boolean priority, String identifier, float x, float y, float z, int attModel, float distOrRoll) {
		fix();
		sndSystem.rawDataStream(format, priority, identifier, x, y, z, attModel, distOrRoll);
	}

	public void refresh() {
		this.sndSystem = ReflectionHelper.getPrivateValue(SoundManager.class, soundManager, 4);
	}

	public void setAttenuation(String generateSource, int att) {
		fix();
		sndSystem.setAttenuation(generateSource, att);
	}

	public void setDistOrRoll(String generateSource, float soundDistance) {
		fix();
		sndSystem.setDistOrRoll(generateSource, soundDistance);
	}

	public void setPitch(String identifier, float f) {
		fix();
		sndSystem.setPitch(identifier, f);
	}

	public void setPosition(String string, float x, float y, float z) {
		fix();
		sndSystem.setPosition(string, x, y, z);
	}

	public void setVelocity(String string, float motX, float motY, float motZ) {
		fix();
		sndSystem.setVelocity(string, motX, motY, motZ);
	}

	public void setVolume(String identifier, float worldVolume) {
		fix();
		sndSystem.setVolume(identifier, worldVolume);
	}
}