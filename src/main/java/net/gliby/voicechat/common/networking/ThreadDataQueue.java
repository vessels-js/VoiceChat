package net.gliby.voicechat.common.networking;


/** Sorts voice chat data and make sure voice samples get delivered in order. **/
public class ThreadDataQueue implements Runnable {

	private final ServerStreamManager manager;

	public ThreadDataQueue(ServerStreamManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		while (manager.running) {
			if (!manager.dataQueue.isEmpty()) {
				final ServerDatalet data = manager.dataQueue.poll();
				ServerStream stream;
				if ((stream = manager.newDatalet(data)) == null) {
					manager.createStream(data);
				} else manager.giveStream(stream, data);
			} else {
				synchronized (this) {
					try {
						this.wait(1);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
