package net.gliby.voicechat.client.networking.voiceclients;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.gliby.voicechat.common.PlayerProxy;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoicePacket;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;

/**
 * If all else fails, use minecraft's own networking system.
 **/
public class MinecraftVoiceClient extends VoiceClient {

	private final ClientStreamManager soundManager;

	public MinecraftVoiceClient(EnumVoiceNetworkType enumVoiceServer) {
		super(enumVoiceServer);
		VoiceChat.getProxyInstance();
		soundManager = VoiceChatClient.getSoundManager();
	}

	@Override
	public void handleEnd(int id) {
		soundManager.alertEnd(id);
	}

	@Override
	public void handleEntityPosition(int entityID, double x, double y, double z) {
		final PlayerProxy proxy = soundManager.playerData.get(entityID);
		if (proxy != null) {
			proxy.setPosition(x, y, z);
		}
	}

	@Override
	public void handlePacket(int entityID, byte[] data, int chunkSize, boolean direct, byte volume) {
		soundManager.getSoundPreProcessor().process(entityID, data, chunkSize, direct, volume);
	}

	@Override
	public void sendVoiceData(byte division, byte[] samples, boolean end) {
		if (end) VoiceChat.getDispatcher().sendToServer(new MinecraftServerVoiceEndPacket());
		else VoiceChat.getDispatcher().sendToServer(new MinecraftServerVoicePacket(division, samples));
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

}
