package net.gliby.voicechat.client.sound;

import java.util.Comparator;

import net.gliby.voicechat.common.PlayerProxy;

public class PlayableStream {
	public static class PlayableStreamComparator implements Comparator<PlayableStream> {
		@Override
		public int compare(PlayableStream a, PlayableStream b) {
			int f = a.id > b.id ? +1 : a.id < b.id ? -1 : 0;
			return f;
		}
	}

	public final int id;
	public int special;
	public boolean needsEnd, direct;

	public long lastUpdated;
	// private MovingAverage jitterAverage = new MovingAverage(3);
	public JitterBuffer buffer;
	public PlayerProxy player;

	public boolean dirty;

	/**
	 ** Used for managing voice data, ++organization
	 **/
	public PlayableStream(PlayerProxy proxy, int id, boolean direct) {
		this.id = id;
		this.direct = direct;
		this.lastUpdated = System.currentTimeMillis();
		this.player = proxy;
		buffer = new JitterBuffer(SoundManager.universalAudioFormat, 0);
	}

	public PlayableStream(PlayerProxy proxy, int id, boolean direct, int special) {
		this.id = id;
		this.direct = direct;
		this.lastUpdated = System.currentTimeMillis();
		this.buffer = new JitterBuffer(SoundManager.universalAudioFormat, 0);
		this.special = special;
	}

	public String generateSource() {
		return "" + this.id;
	}

	public int getJitterRate() {
		return getLastTimeUpdatedMS();
	}

	public int getLastTimeUpdatedMS() {
		return (int) (System.currentTimeMillis() - lastUpdated);
	}

	public void update(Datalet data, int l) {
		if (this.direct != data.direct) dirty = true;
		this.direct = data.direct;
		// jitterAverage.add(l);
	}
}
