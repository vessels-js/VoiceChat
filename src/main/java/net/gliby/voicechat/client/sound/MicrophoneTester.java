package net.gliby.voicechat.client.sound;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import net.gliby.voicechat.client.VoiceChatClient;

public class MicrophoneTester implements Runnable {

	private TargetDataLine line;
	private Thread thread;
	public boolean recording;
	private final VoiceChatClient voiceChat;
	public float currentAmplitude;

	public MicrophoneTester(VoiceChatClient voiceChat) {
		super();
		this.voiceChat = voiceChat;
	}

	private byte[] boostVolume(byte[] data) {
		final int USHORT_MASK = (1 << 16) - 1;
		final ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
		final ByteBuffer newBuf = ByteBuffer.allocate(data.length).order(ByteOrder.LITTLE_ENDIAN);
		int sample;
		while (buf.hasRemaining()) {
			sample = buf.getShort() & USHORT_MASK;
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
			VoiceChatClient.getLogger().fatal("No line in found, cannot test input device.");
			return;
		}
		final DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, ClientStreamManager.universalAudioFormat);
		try {
			final TargetDataLine targetLine = line;
			targetLine.open(ClientStreamManager.universalAudioFormat);
			targetLine.start();
			final SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			sourceLine.open(ClientStreamManager.universalAudioFormat);
			sourceLine.start();
			int numBytesRead;
			final byte[] targetData = new byte[targetLine.getBufferSize() / 5];
			while (recording) {
				numBytesRead = targetLine.read(targetData, 0, targetData.length);
				if (numBytesRead == -1) break;
				final byte[] boostedTargetData = boostVolume(targetData);
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
		} catch (final Exception e) {
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