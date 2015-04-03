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

public class MinecraftClientEntityDataPacket extends MinecraftPacket<MinecraftClientEntityDataPacket> {

	private int entityID;
	private String username;
	private double x, y, z;

	public MinecraftClientEntityDataPacket() {}

	public MinecraftClientEntityDataPacket(int entityID, String username, double x, double y, double z) {
		this.entityID = entityID;
		this.username = username;
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
		this.username = buf.readUTF();
	}

	@Override
	public void onMessage(MinecraftClientEntityDataPacket packet, EntityPlayerMP player) {
		if(VoiceChat.getProxyInstance().getClientNetwork().isConnected())
			VoiceChat.getProxyInstance().getClientNetwork().handleEntityData(packet.entityID, packet.username, packet.x, packet.y, packet.z);
	}

	@Override
	public void toBytes(ByteArrayDataOutput buf) {
		buf.writeInt(entityID);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeUTF(username);
	}

	@Override
	public String getChannel() {
		return "GVC-ED";
	}
}
