package net.gliby.voicechat.client.sound;

import java.util.Comparator;

import net.gliby.voicechat.common.PlayerProxy;

public class ClientStream {
	public static class PlayableStreamComparator implements Comparator<ClientStream> {
		@Override
		public int compare(ClientStream a, ClientStream b) {
			final int f = a.id > b.id ? +1 : a.id < b.id ? -1 : 0;
			return f;
		}
	}

	// 0 - 100, -1 being we don't want to set volume.
	public int volume;
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
	public ClientStream(PlayerProxy proxy, int id, boolean direct) {
		this.id = id;
		this.direct = direct;
		this.lastUpdated = System.currentTimeMillis();
		this.player = proxy;
		buffer = new JitterBuffer(ClientStreamManager.universalAudioFormat, 0);
	}

	public ClientStream(PlayerProxy proxy, int id, boolean direct, int special) {
		this.id = id;
		this.direct = direct;
		this.lastUpdated = System.currentTimeMillis();
		this.buffer = new JitterBuffer(ClientStreamManager.universalAudioFormat, 0);
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
		this.volume = data.volume;
		// jitterAverage.add(l);
	}
}
