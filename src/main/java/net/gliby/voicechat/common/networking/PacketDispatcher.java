package net.gliby.voicechat.common.networking;

import net.gliby.voicechat.VoiceChat;
import net.minecraft.entity.player.EntityPlayerMP;

/** Wrapper for PacketDispatcher from 1.6, because network back port is needed! **/
public class PacketDispatcher {

	public static void sendPacketToPlayer(PayloadProxyMessage message, EntityPlayerMP player) {
		VoiceChat.DISPATCH.sendTo(message, player);
	}

	public static void sendPacketToServer(PayloadProxyMessage message) {
		VoiceChat.DISPATCH.sendToServer(message);
	}

}
