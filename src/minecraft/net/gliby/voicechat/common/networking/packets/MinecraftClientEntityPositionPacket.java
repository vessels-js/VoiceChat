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

public class MinecraftClientEntityPositionPacket extends MinecraftPacket<MinecraftClientEntityPositionPacket> {

	private int entityID;
	private double x, y, z;

	public MinecraftClientEntityPositionPacket() {}

	public MinecraftClientEntityPositionPacket(int entityID, double x, double y, double z) {
		this.entityID = entityID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteArrayDataInput buf) {
		this.entityID = buf.readInt();
		this.x = buf.readDouble();
		this.y = buf.readDouble();
		this.z = buf.readDouble();
	}

	@Override
	public void onMessage(MinecraftClientEntityPositionPacket packet, EntityPlayerMP player) {
		if(VoiceChat.getProxyInstance().getClientNetwork().isConnected())
			VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient().handleEntityPosition(packet.entityID, packet.x, packet.y, packet.z);
	}

	@Override
	public void toBytes(ByteArrayDataOutput buf) {
		buf.writeInt(entityID);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	@Override
	public String getChannel() {
		return "GVC-EP";
	}
}
