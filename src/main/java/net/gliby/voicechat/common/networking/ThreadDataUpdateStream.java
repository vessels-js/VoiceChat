package net.gliby.voicechat.common.networking;

public class ThreadDataUpdateStream implements Runnable {

	private final static int ARBITRARY_TIMEOUT = 250;
	private DataManager dataManager;

	public ThreadDataUpdateStream(DataManager dataManager) {
		this.dataManager = dataManager;
	}

	@Override
	public void run() {
		while (dataManager.running) {
			if (!dataManager.currentStreams.isEmpty()) {
				for (int i = 0; i < dataManager.currentStreams.size(); i++) {
					DataStream stream = dataManager.currentStreams.get(i);
					int duration = stream.getLastTimeUpdatedMS();
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
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
