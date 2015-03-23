package net.gliby.voicechat.common.networking;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;

public class PacketManager {

	public static byte[] getVoiceServerAutheticationPacket(VoiceChatServer voiceChat, EnumVoiceNetworkType voiceServerType, String auth, String ip) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeBoolean(voiceChat.getServerSettings().canShowVoicePlates());
			outputStream.writeBoolean(voiceChat.getServerSettings().canShowVoiceIcons());
			outputStream.writeInt(voiceChat.getServerSettings().getMinimumSoundQuality());
			outputStream.writeInt(voiceChat.getServerSettings().getMaximumSoundQuality());
			outputStream.writeInt(voiceChat.getServerSettings().getBufferSize());
			outputStream.writeInt(voiceChat.getServerSettings().getSoundDistance());
			outputStream.writeInt(voiceServerType.ordinal());
			outputStream.writeUTF(auth);
			outputStream.writeInt(voiceChat.getServerSettings().getUDPPort());
			outputStream.writeUTF(ip);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bos.toByteArray();
	}

	public static byte[] getVoiceServerPacket(VoiceChatServer voiceChat, EnumVoiceNetworkType voiceServerType) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeBoolean(voiceChat.getServerSettings().canShowVoicePlates());
			outputStream.writeBoolean(voiceChat.getServerSettings().canShowVoiceIcons());
			outputStream.writeInt(voiceChat.getServerSettings().getMinimumSoundQuality());
			outputStream.writeInt(voiceChat.getServerSettings().getMaximumSoundQuality());
			outputStream.writeInt(voiceChat.getServerSettings().getBufferSize());
			outputStream.writeInt(voiceChat.getServerSettings().getSoundDistance());
			outputStream.writeInt(voiceServerType.ordinal());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bos.toByteArray();
	}
}