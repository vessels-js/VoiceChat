package net.gliby.voicechat.client.networking.voiceclients;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.gliby.voicechat.common.PlayerProxy;
import net.gliby.voicechat.common.networking.MinecraftPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityDataPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityPositionPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoicePacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceServerPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoicePacket;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

/**
 * If all else fails, use minecraft's own networking system.
 * **/
public class MinecraftVoiceClient extends VoiceClient {

	private final ClientStreamManager soundManager;

	public MinecraftVoiceClient(EnumVoiceNetworkType enumVoiceServer) {
		super(enumVoiceServer);
		VoiceChat.getProxyInstance();
		soundManager = VoiceChatClient.getSoundManager();
	}

	@Override
	public void handleEnd(int id) {
		soundManager.alertEnd(id);
	}

	@Override
	public void handleEntityPosition(int entityID, double x, double y, double z) {
		final PlayerProxy proxy = soundManager.playerData.get(entityID);
		if (proxy != null) {
			proxy.setPosition(x, y, z);
		}
	}

	@Override
	public void handlePacket(int entityID, byte[] data, int chunkSize, boolean direct) {
		soundManager.getSoundPreProcessor().process(entityID, data, chunkSize, direct);
	}

	@Override
	public void sendVoiceData(byte division, byte[] samples, boolean end) {
		if(end) VoiceChat.getDispatcher().sendToServer(new MinecraftServerVoiceEndPacket());
		else VoiceChat.getDispatcher().sendToServer(new MinecraftServerVoicePacket(division, samples));
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	public static class PacketHandler implements IPacketHandler  {

		public static HashMap<String, Class<? extends MinecraftPacket>> packetMap = new HashMap<String, Class<? extends MinecraftPacket>>();
		{
			packetMap.put("GVC-E", MinecraftClientVoiceEndPacket.class);
			packetMap.put("GVC-V", MinecraftClientVoicePacket.class);
			packetMap.put("GVC-ED", MinecraftClientEntityDataPacket.class);
			packetMap.put("GVC-EP", MinecraftClientEntityPositionPacket.class);
			packetMap.put("GVC-AS", MinecraftClientVoiceAuthenticatedServer.class);
			packetMap.put("GVC-S", MinecraftClientVoiceServerPacket.class);
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
					packet.onMessage(packet, null);
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
