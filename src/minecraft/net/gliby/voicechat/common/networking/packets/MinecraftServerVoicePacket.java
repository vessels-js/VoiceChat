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

public class MinecraftServerVoicePacket extends MinecraftPacket<MinecraftServerVoicePacket> {

	private byte[] data;
	private byte divider;

	public MinecraftServerVoicePacket() {}

	public MinecraftServerVoicePacket(byte divider, byte[] data) {
		this.divider = divider;
		this.data = data;
	}

	@Override
	public void fromBytes(ByteArrayDataInput buf) {
		this.divider = buf.readByte();
		this.data = new byte[buf.readInt()];
		for (int i = 0; i < data.length; i++) {
			data[i] = buf.readByte();
		}
	}

	public void onMessage(MinecraftServerVoicePacket packet, EntityPlayerMP player) {
		VoiceChat.getServerInstance().getVoiceServer().handleVoiceData(player, packet.data, packet.divider, player.entityId, false);
	}

	@Override
	public void toBytes(ByteArrayDataOutput buf) {
		buf.writeByte(divider);
		buf.writeInt(data.length);
		for(int i = 0; i < data.length; i++) {
			buf.writeByte(data[i]);
		}
	}

	@Override
	public String getChannel() {
		return "GVC-V";
	}

}
