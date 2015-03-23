package net.gliby.voicechat.common.networking;

import net.gliby.voicechat.VoiceChat;

/** Sorts voice chat data and make sure voice samples get delivered in order. **/
public class ThreadDataQueue implements Runnable {

	private DataManager manager;

	public ThreadDataQueue(DataManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		while (manager.running) {
			if (!manager.dataQueue.isEmpty()) {
				ServerDatalet data = (ServerDatalet) manager.dataQueue.poll();
				DataStream stream;
				if ((stream = manager.newDatalet(data)) == null) {
					manager.createStream(data);
				} else manager.giveStream(stream, data);
			} else {
				synchronized (this) {
					try {
						this.wait(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
