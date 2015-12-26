package net.gliby.voicechat.client.sound;

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.debug.Statistics;
import net.minecraft.client.Minecraft;

import org.xiph.speex.SpeexDecoder;

public class SoundPreProcessor {

	public static List<byte[]> divideArray(byte[] source, int chunksize) {
		final List<byte[]> result = new ArrayList<byte[]>();
		int start = 0;
		while (start < source.length) {
			final int end = Math.min(source.length, start + chunksize);
			result.add(Arrays.copyOfRange(source, start, end));
			start += chunksize;
		}

		return result;
	}

	VoiceChatClient voiceChat;
	Statistics stats;

	SpeexDecoder decoder;

	byte[] buffer;

	public SoundPreProcessor(VoiceChatClient voiceChat, Minecraft mc) {
		this.voiceChat = voiceChat;
		this.stats = VoiceChatClient.getStatistics();
	}

	public boolean process(int id, byte[] encodedSamples, int chunkSize, boolean direct, byte volume) {
		if (chunkSize > encodedSamples.length) {
			VoiceChatClient.getLogger().fatal("Sound Pre-Processor has been given incorrect data from network, sample pieces cannot be bigger than whole sample. ");
			return false;
		}

		if (decoder == null) {
			decoder = new SpeexDecoder();
			decoder.init(0, (int) ClientStreamManager.getUniversalAudioFormat().getSampleRate(), ClientStreamManager.getUniversalAudioFormat().getChannels(), voiceChat.getSettings().isPerceptualEnchantmentAllowed());
		}

		byte[] decodedData = null;
		if (encodedSamples.length <= chunkSize) {
			try {
				decoder.processData(encodedSamples, 0, encodedSamples.length);
			} catch (final StreamCorruptedException e) {
				e.printStackTrace();
				return false;
			}
			decodedData = new byte[decoder.getProcessedDataByteSize()];
			decoder.getProcessedData(decodedData, 0);
		} else {
			final List samplesList = divideArray(encodedSamples, chunkSize);
			buffer = new byte[0];
			for (int i = 0; i < samplesList.size(); i++) {
				final byte[] sample = (byte[]) samplesList.get(i);
				final SpeexDecoder tempDecoder = new SpeexDecoder();
				tempDecoder.init(0, (int) ClientStreamManager.getUniversalAudioFormat().getSampleRate(), ClientStreamManager.getUniversalAudioFormat().getChannels(), voiceChat.getSettings().isPerceptualEnchantmentAllowed());
				try {
					decoder.processData(sample, 0, sample.length);
				} catch (final StreamCorruptedException e) {
					e.printStackTrace();
					return false;
				}
				final byte[] sampleBuffer = new byte[decoder.getProcessedDataByteSize()];
				decoder.getProcessedData(sampleBuffer, 0);
				write(sampleBuffer);
			}
			decodedData = buffer;
		}
		if (decodedData != null) {
			VoiceChatClient.getSoundManager().addQueue(decodedData, direct, id, volume);
			if (stats != null) {
				stats.addEncodedSamples(encodedSamples.length);
				stats.addDecodedSamples(decodedData.length);
			}
			buffer = new byte[0];
			return true;
		}
		return false;
	}

	private void write(byte[] write) {
		final byte[] result = new byte[buffer.length + write.length];
		System.arraycopy(buffer, 0, result, 0, buffer.length);
		System.arraycopy(write, 0, result, buffer.length, write.length);
		buffer = result;
	}

}
