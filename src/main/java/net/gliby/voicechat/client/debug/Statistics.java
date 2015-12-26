package net.gliby.voicechat.client.debug;

import net.gliby.voicechat.client.sound.MovingAverage;

public class Statistics {

	MovingAverage decodedAverage = new MovingAverage(8);
	MovingAverage encodedAverage = new MovingAverage(8);
	int encodedSum;
	int decodedSum;

	public void addDecodedSamples(int size) {
		decodedSum += size;
		decodedAverage.add(size);
	}

	public void addEncodedSamples(int size) {
		encodedSum += size;
		encodedAverage.add(size);
	}

	public int getDecodedAverageDataReceived() {
		return decodedAverage.getAverage().intValue();
	}

	public int getDecodedDataReceived() {
		return decodedSum;
	}

	public int getEncodedAverageDataReceived() {
		return encodedAverage.getAverage().intValue();
	}

	public int getEncodedDataReceived() {
		return encodedSum;
	}
}
