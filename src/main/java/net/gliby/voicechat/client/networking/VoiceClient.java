package net.gliby.voicechat.client.networking;

import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;

public abstract class VoiceClient implements Runnable {
	protected EnumVoiceNetworkType type;

	public VoiceClient(EnumVoiceNetworkType enumVoiceServer) {
		this.type = enumVoiceServer;
	}

	public final EnumVoiceNetworkType getType() {
		return this.type;
	}

	public abstract void handleEnd(int id);

	public abstract void handleEntityPosition(int entityID, double x, double y, double z);

	public abstract void handlePacket(int entityID, byte[] data, int chunkSize, boolean direct);

	@Override
	public final void run() {
		this.start();
	}

	public abstract void sendVoiceData(byte division, byte[] samples, boolean end);

	public abstract void start();

	public abstract void stop();
}
