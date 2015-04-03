/**
 *  Copyright (c) 2015, Gliby.
 *  * http://www.gliby.net/
 */
package net.gliby.voicechat.common.networking.packets;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.MinecraftPacket;
import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public class MinecraftServerVoiceEndPacket extends MinecraftPacket<MinecraftServerVoiceEndPacket> {

	public MinecraftServerVoiceEndPacket() {}

	@Override
	public void fromBytes(ByteArrayDataInput buf) {}

	@Override
	public void onMessage(MinecraftServerVoiceEndPacket packet, EntityPlayerMP player) {
		VoiceChat.getServerInstance().getVoiceServer().handleVoiceData(player, null, (byte) 0, player.entityId, true);
	}

	@Override
	public void toBytes(ByteArrayDataOutput buf) {}

	@Override
	public String getChannel() {
		return "GVC-E";
	}

}
