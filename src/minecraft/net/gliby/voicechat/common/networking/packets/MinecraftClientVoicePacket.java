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

public class MinecraftClientVoicePacket extends MinecraftPacket<MinecraftClientVoicePacket> {
	byte divider;
	byte[] samples;
	int entityID;
	boolean direct;

	public MinecraftClientVoicePacket() {
	}

	public MinecraftClientVoicePacket(byte divider, byte[] samples, int entityID, boolean direct) {
		this.divider = divider;
		this.samples = samples;
		this.entityID = entityID;
		this.direct = direct;
	}

	@Override
	public void fromBytes(ByteArrayDataInput buf) {
		this.divider = buf.readByte();
		this.entityID = buf.readInt();
		this.direct = buf.readBoolean();
		this.samples = new byte[buf.readInt()];
		for(int i = 0; i < samples.length; i++) {
			this.samples[i] = buf.readByte();
		}
	}

	@Override
	public void onMessage(MinecraftClientVoicePacket packet, EntityPlayerMP player) {
		if(VoiceChat.getProxyInstance().getClientNetwork().isConnected())
			VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient().handlePacket(packet.entityID, packet.samples, packet.divider, packet.direct);
	}

	@Override
	public void toBytes(ByteArrayDataOutput buf) {
		buf.writeByte(divider);
		buf.writeInt(entityID);
		buf.writeBoolean(direct);
		buf.writeInt(samples.length);
		for(int i =0; i < samples.length; i++) {
			buf.writeByte(samples[i]);
		}
	}

	@Override
	public String getChannel() {
		return "GVC-V";
	}

}
