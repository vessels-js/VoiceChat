package net.gliby.voicechat.client.sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import net.gliby.voicechat.client.VoiceChatClient;

public class MicrophoneTester implements Runnable {

	public TargetDataLine line;
	public Thread thread;
	public boolean recording;
	private VoiceChatClient voiceChat;
	public float currentAmplitude;

	public MicrophoneTester(VoiceChatClient voiceChat) {
		super();
		this.voiceChat = voiceChat;
	}

	private byte[] boostVolume(byte[] data) {
		int USHORT_MASK = (1 << 16) - 1;
		;
		final ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		final ByteBuffer newBuf = ByteBuffer.allocate(data.length).order(ByteOrder.LITTLE_ENDIAN);
		int sample;
		while (buf.hasRemaining()) {
			sample = (int) buf.getShort() & USHORT_MASK;
			sample *= 1 + (int) (voiceChat.getSettings().getInputBoost() * 5);
			newBuf.putShort((short) (sample & USHORT_MASK));
		}
		return newBuf.array();
	}

	public Thread getThread() {
		return thread;
	}

	@Override
	public void run() {
		voiceChat.setRecorderActive(false);
		voiceChat.recorder.stop();
		line = voiceChat.getSettings().getInputDevice().getLine();
		if (line == null) {
			voiceChat.getLogger().fatal("No line in found, cannot test input device.");
			return;
		}
		DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, SoundManager.universalAudioFormat);
		try {
			TargetDataLine targetLine = line;
			targetLine.open(SoundManager.universalAudioFormat);
			targetLine.start();
			SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(SoundManager.universalAudioFormat);
			sourceLine.start();
			int numBytesRead;
			byte[] targetData = new byte[targetLine.getBufferSize() / 5];
			while (recording) {
				numBytesRead = targetLine.read(targetData, 0, targetData.length);
				if (numBytesRead == -1) break;
				byte[] boostedTargetData = boostVolume(targetData);
				sourceLine.write(boostedTargetData, 0, numBytesRead);
				double sum = 0;
				for (int i = 0; i < numBytesRead; i++) {
					sum += boostedTargetData[i] * boostedTargetData[i];
				}
				if (numBytesRead > 0) {
					currentAmplitude = (int) Math.sqrt(sum / numBytesRead);
				}
			}
			sourceLine.flush();
			sourceLine.close();
			line.flush();
			line.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		thread = new Thread(this, "Input Device Tester");
		recording = true;
		thread.start();
	}

	public void stop() {
		recording = false;
		thread = null;
	}

	public void toggle() {
		if (recording) start();
		else stop();
	}
}