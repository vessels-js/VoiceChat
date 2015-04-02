package net.gliby.voicechat.client.sound.thread;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.ClientStream;
import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.minecraft.client.Minecraft;

import org.lwjgl.util.vector.Vector3f;

import paulscode.sound.SoundSystemConfig;

public class ThreadUpdateStream implements Runnable {

	private final static int ARBITRARY_TIMEOUT = 400;
	private final Minecraft mc;

	private final VoiceChatClient voiceChat;

	private final ClientStreamManager manager;

	/**
	 * Handles sound streams and sound position/velocity.
	 **/
	public ThreadUpdateStream(ClientStreamManager manager, VoiceChatClient voiceChatClient) {
		this.manager = manager;
		this.mc = Minecraft.getMinecraft();
		this.voiceChat = voiceChatClient;
	}

	@Override
	public void run() {
		while (true) {
			if (!VoiceChatClient.getSoundManager().currentStreams.isEmpty()) {
				for (int i = 0; i < VoiceChatClient.getSoundManager().currentStreams.size(); i++) {
					final ClientStream stream = VoiceChatClient.getSoundManager().currentStreams.get(i);
					final String source = stream.generateSource();
					if (stream.needsEnd || stream.getLastTimeUpdatedMS() > (ARBITRARY_TIMEOUT)) if (!voiceChat.sndSystem.playing(source)) manager.killStream(stream);
					if (stream.dirty) {
						voiceChat.sndSystem.setVolume(source, 1.0f);
						voiceChat.sndSystem.setAttenuation(source, SoundSystemConfig.ATTENUATION_LINEAR);
						voiceChat.sndSystem.setDistOrRoll(source, voiceChat.getSettings().getSoundDistance());
						stream.dirty = false;
					}

					if (stream.direct) {
						final Vector3f vector = stream.player.position();
						voiceChat.sndSystem.setPosition(source, vector.x, vector.y, vector.z);
					} else voiceChat.sndSystem.setPosition(source, (float) mc.thePlayer.posX, (float) mc.thePlayer.posY, (float) mc.thePlayer.posZ);
					stream.player.update(mc.theWorld);
				}
				try {
					synchronized (this) {
						wait(25);
					}
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					synchronized (this) {
						this.wait(2);
					}
				} catch (final InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
