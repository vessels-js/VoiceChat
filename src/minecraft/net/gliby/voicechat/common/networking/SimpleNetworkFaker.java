package net.gliby.voicechat.common.networking;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoicePacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class SimpleNetworkFaker {
	
	public void sendTo(MinecraftPacket packet, EntityPlayerMP player) {
		Packet250CustomPayload payload = new Packet250CustomPayload();
		payload.channel = packet.getChannel();
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		packet.toBytes(out);
		payload.data = out.toByteArray();
		payload.length = payload.data.length;
		PacketDispatcher.sendPacketToPlayer(payload, (Player)player);
	}

	public void sendToServer(MinecraftPacket packet) {
		Packet250CustomPayload payload = new Packet250CustomPayload();
		payload.channel = packet.getChannel();
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		packet.toBytes(out);
		payload.data = out.toByteArray();
		payload.length = payload.data.length;
		PacketDispatcher.sendPacketToServer(payload);
	}

}
