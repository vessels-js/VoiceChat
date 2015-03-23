package net.gliby.voicechat.client.sound.thread;

import net.gliby.voicechat.client.sound.Datalet;
import net.gliby.voicechat.client.sound.SoundManager;

public class ThreadSoundQueue implements Runnable {

	private SoundManager sndManager;
	private Object notifier = new Object();

	public ThreadSoundQueue(SoundManager sndManager) {
		this.sndManager = sndManager;
	}

	@Override
	public void run() {
		while (true) {
			if (!sndManager.queue.isEmpty()) {
				Datalet data = (Datalet) sndManager.queue.poll();
				if (data != null) {
					boolean end = data.data == null;
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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}