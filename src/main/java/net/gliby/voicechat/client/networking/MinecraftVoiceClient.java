package net.gliby.voicechat.client.networking;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.sound.SoundManager;
import net.gliby.voicechat.common.PlayerProxy;
import net.gliby.voicechat.common.networking.PacketDispatcher;
import net.gliby.voicechat.common.networking.packets.PacketServerVoiceEnd;
import net.gliby.voicechat.common.networking.packets.PacketServerVoiceSample;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;
/** If all else fails, use minecraft's own networking system. Optimization to come, until then slow ByteArrayOutputStream it is! **/
public class MinecraftVoiceClient extends VoiceClient {

	private SoundManager soundManager;

	public MinecraftVoiceClient(EnumVoiceNetworkType enumVoiceServer) {
		super(enumVoiceServer);
		soundManager = VoiceChat.getProxyInstance().getSoundManager();
	}

	@Override
	public void handleEnd(int id) {
		soundManager.alertEnd(id);
	}

	@Override
	public void handleEntityPosition(int entityID, double x, double y, double z) {
		PlayerProxy proxy = soundManager.playerData.get(entityID);
		if (proxy != null) {
			proxy.setPosition(x, y, z);
		}
	}

	@Override
	public void handlePacket(int entityID, byte[] data, int chunkSize, boolean direct) {
		soundManager.getSoundPreProcessor().process(entityID, data, chunkSize, direct);
	}

	@Override
	public void sendVoiceData(byte division, byte[] samples, boolean end) {
		if (end) {
			PacketDispatcher.sendPacketToServer(new PacketServerVoiceEnd(new byte[0]));
			return;
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
		DataOutputStream outputStream = new DataOutputStream(bos);
		if (samples != null) {
			try {
				if (!end) {
					outputStream.writeByte(division);
					outputStream.writeInt(samples.length);
					for (int i = 0; i < samples.length; i++) {
						outputStream.writeByte(samples[i]);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		PacketDispatcher.sendPacketToServer(new PacketServerVoiceSample(bos.toByteArray()));
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

}
