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

public class MinecraftClientVoiceEndPacket extends MinecraftPacket<MinecraftClientVoiceEndPacket> {

	int entityID;

	public MinecraftClientVoiceEndPacket() {}

	public MinecraftClientVoiceEndPacket(int entityID) {
		this.entityID = entityID;
	}

	@Override
	public void fromBytes(ByteArrayDataInput buf) {
		this.entityID = buf.readInt();
	}

	@Override
	public void onMessage(MinecraftClientVoiceEndPacket packet, EntityPlayerMP player) {
		if(VoiceChat.getProxyInstance().getClientNetwork().isConnected())
			VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient().handleEnd(packet.entityID);
	}

	@Override
	public void toBytes(ByteArrayDataOutput buf) {
		buf.writeInt(entityID);
	}

	@Override
	public String getChannel() {
		return "GVC-E";
	}

}
