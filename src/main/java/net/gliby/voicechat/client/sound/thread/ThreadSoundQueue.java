package net.gliby.voicechat.client.sound.thread;

import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.gliby.voicechat.client.sound.Datalet;

public class ThreadSoundQueue implements Runnable {

	private final ClientStreamManager sndManager;
	private final Object notifier = new Object();

	public ThreadSoundQueue(ClientStreamManager sndManager) {
		this.sndManager = sndManager;
	}

	@Override
	public void run() {
		while (true) {
			if (!sndManager.queue.isEmpty()) {
				final Datalet data = sndManager.queue.poll();
				if (data != null) {
					final boolean end = data.data == null;
					if (sndManager.newDatalet(data) && !end) {
						sndManager.createStream(data);
					} else {
						if (end) sndManager.giveEnd(data.id);
						else sndManager.giveStream(data);
					}
				}
			} else {
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}