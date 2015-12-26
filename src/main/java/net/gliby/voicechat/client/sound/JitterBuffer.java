package net.gliby.voicechat.client.sound;

import javax.sound.sampled.AudioFormat;

class JitterBuffer {

	private byte[] buffer;
	private final AudioFormat format;
	private int threshold;

	JitterBuffer(AudioFormat format, int jitter) {
		this.format = format;
		updateJitter(jitter);
	}

	void clearBuffer(int jitterSize) {
		buffer = new byte[0];
		updateJitter(jitterSize);
	}

	byte[] get() {
		return buffer;
	}

	private int getSizeInBytes(AudioFormat fmt, int size) {
		final int s = (int) (fmt.getSampleRate() / 1000);
		final int sampleSize = (int) ((fmt.getSampleSizeInBits() / 8) * 0.49f);
		return sampleSize != 0 ? s * size / sampleSize : 0;
	}

	public boolean isReady() {
		return buffer.length > threshold;
	}

	void push(byte[] data) {
		write(data);
	}

	void updateJitter(int size) {
		this.threshold = getSizeInBytes(format, size);
		if (buffer == null) buffer = threshold != 0 ? new byte[3 * this.threshold] : new byte[320];
	}

	private void write(byte[] write) {
		final byte[] result = new byte[buffer.length + write.length];
		System.arraycopy(buffer, 0, result, 0, buffer.length);
		System.arraycopy(write, 0, result, buffer.length, write.length);
		buffer = result;
	}
}