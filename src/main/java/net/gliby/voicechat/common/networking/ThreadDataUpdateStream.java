package net.gliby.voicechat.common.networking;

public class ThreadDataUpdateStream implements Runnable {

	private final static int ARBITRARY_TIMEOUT = 350;
	private final ServerStreamManager dataManager;

	public ThreadDataUpdateStream(ServerStreamManager dataManager) {
		this.dataManager = dataManager;
	}

	@Override
	public void run() {
		while (dataManager.running) {
			if (!dataManager.currentStreams.isEmpty()) {
				for (int i = 0; i < dataManager.currentStreams.size(); i++) {
					final ServerStream stream = dataManager.currentStreams.get(i);
					final int duration = stream.getLastTimeUpdated();
					if (duration > ARBITRARY_TIMEOUT) {
						if (duration > stream.player.ping * 2) {
							dataManager.killStream(stream);
						}
					}
				}
			}
			try {
				synchronized (this) {
					this.wait(12);
				}
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
