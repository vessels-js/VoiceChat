package net.gliby.voicechat.common.networking;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.gliby.voicechat.VoiceChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommonPacketHandler {

	public static void handleVoiceData(byte[] payload, EntityPlayerMP player, boolean end) {
		byte[] data = null;
		byte divider = 0;
		if (!end) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(payload));
			try {
				divider = dis.readByte();
				int size = dis.readInt();
				data = new byte[size];
				for (int i = 0; i < data.length; i++) {
					data[i] = dis.readByte();
				}
				if (data.length > VoiceChat.getServerInstance().getServerSettings().getBufferSize()) throw new Exception("Security: Received to much data! LIMIT " + VoiceChat.getServerInstance().getServerSettings().getBufferSize() + ", current: " + data.length);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		VoiceChat.getServerInstance().getVoiceServer().handleVoiceData(player, data, divider, ((Entity) player).getEntityId(), end);
	}
}
