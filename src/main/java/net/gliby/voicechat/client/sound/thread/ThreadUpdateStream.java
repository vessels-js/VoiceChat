package net.gliby.voicechat.client.sound.thread;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.PlayableStream;
import net.gliby.voicechat.client.sound.SoundManager;
import net.minecraft.client.Minecraft;

import org.lwjgl.util.vector.Vector3f;

import paulscode.sound.SoundSystemConfig;

public class ThreadUpdateStream implements Runnable {

	private final static int ARBITRARY_TIMEOUT = 325;
	private Minecraft mc;

	private VoiceChatClient voiceChat;

	private SoundManager manager;

	/**
	 * Handles sound streams and sound position/velocity.
	 **/
	public ThreadUpdateStream(SoundManager manager, VoiceChatClient voiceChatClient) {
		this.manager = manager;
		this.mc = Minecraft.getMinecraft();
		this.voiceChat = voiceChatClient;
	}

	@Override
	public void run() {
		while (true) {
			if (!voiceChat.getSoundManager().currentStreams.isEmpty()) {
				for (int i = 0; i < voiceChat.getSoundManager().currentStreams.size(); i++) {
					PlayableStream stream = voiceChat.getSoundManager().currentStreams.get(i);
					String source = stream.generateSource();
					if (stream.needsEnd || stream.getLastTimeUpdatedMS() > (ARBITRARY_TIMEOUT)) if (!voiceChat.sndSystem.playing(source)) manager.killStream(stream);
					if (stream.dirty) {
						voiceChat.sndSystem.setVolume(source, 1.0f);
						voiceChat.sndSystem.setAttenuation(source, SoundSystemConfig.ATTENUATION_LINEAR);
						voiceChat.sndSystem.setDistOrRoll(source, voiceChat.getSettings().getSoundDistance());
						stream.dirty = false;
					}
					
					if (stream.direct) {
						Vector3f vector = stream.player.position();
						voiceChat.sndSystem.setPosition(source, vector.x, vector.y, vector.z);
					} else voiceChat.sndSystem.setPosition(source, (float) mc.thePlayer.posX, (float) mc.thePlayer.posY, (float) mc.thePlayer.posZ);
					stream.player.update(mc.theWorld);
				}
				try {
					synchronized (this) {
						wait(34);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					synchronized (this) {
						this.wait(2);
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
