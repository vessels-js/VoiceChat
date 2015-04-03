package net.gliby.voicechat.common.networking.voiceservers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import com.google.common.io.ByteStreams;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.MinecraftPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityPositionPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoicePacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoicePacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class MinecraftVoiceServer extends VoiceServer {

	private final VoiceChatServer voiceChat;

	public MinecraftVoiceServer(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
	}

	@Override
	public EnumVoiceNetworkType getType() {
		return EnumVoiceNetworkType.MINECRAFT;
	}

	@Override
	public void handleVoiceData(EntityPlayerMP player, byte[] data, byte divider, int id, boolean end) {
		voiceChat.getServerNetwork().getDataManager().addQueue(player, data, divider, id, end);
	}

	@Override
	public void sendChunkVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples, byte chunkSize) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientVoicePacket(chunkSize, samples, entityID, direct), player);
	}

	@Override
	public void sendEntityPosition(EntityPlayerMP player, int entityID, double x, double y, double z) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientEntityPositionPacket(entityID, x, y, z), player);
	}

	@Override
	public void sendVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientVoicePacket((byte) samples.length, samples, entityID, direct), player);
	}

	@Override
	public void sendVoiceEnd(EntityPlayerMP player, int id) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientVoiceEndPacket(id), player);
	}

	@Override
	public boolean start() {
		VoiceChatServer.getLogger().info("Minecraft Networking is not recommended and is consider very slow, please setup UDP.");
		return true;
	}

	@Override
	public void stop() {
	}
	public static class PacketHandler implements IPacketHandler {
		public static HashMap<String, Class<? extends MinecraftPacket>> packetMap = new HashMap<String, Class<? extends MinecraftPacket>>();
		{
			packetMap.put("GVC-E", MinecraftServerVoiceEndPacket.class);
			packetMap.put("GVC-V", MinecraftServerVoicePacket.class);
		}

		@Override
		public void onPacketData(INetworkManager manager, Packet250CustomPayload payload, Player player) {
			Class packetClass;
			if((packetClass = packetMap.get(payload.channel)) != null) {
				Constructor<?> ctor;
				try {
					ctor = packetClass.getConstructor();
					MinecraftPacket packet = (MinecraftPacket) ctor.newInstance();
					packet.fromBytes(ByteStreams.newDataInput(payload.data));
					packet.onMessage(packet, (EntityPlayerMP)player);
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
