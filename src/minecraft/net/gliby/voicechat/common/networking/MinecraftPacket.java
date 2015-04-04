package net.gliby.voicechat.common.networking;

import net.gliby.voicechat.common.networking.packets.MinecraftServerVoiceEndPacket;
import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

public abstract class MinecraftPacket<Packet extends MinecraftPacket> {

	public MinecraftPacket() {
		super();
	}
	
	public abstract void fromBytes(ByteArrayDataInput in);
	
	public abstract void toBytes(ByteArrayDataOutput out);

	public abstract void onMessage(Packet packet, 	EntityPlayerMP player);

	
	public abstract String getChannel();
	
}
