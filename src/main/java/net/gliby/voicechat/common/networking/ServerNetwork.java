package net.gliby.voicechat.common.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityDataPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class ServerNetwork {

	private final VoiceChatServer voiceChat;

	private String externalAddress;

	public final ServerStreamManager dataManager;

	public ServerNetwork(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
		dataManager = new ServerStreamManager(voiceChat);
	}

	public String getAddress() {
		return externalAddress;
	}

	public ServerStreamManager getDataManager() {
		return dataManager;
	}

	public String[] getPlayerIPs() {
		final List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		final String[] ips = new String[players.size()];
		for (int i = 0; i < players.size(); i++) {
			final EntityPlayerMP p = players.get(i);
			ips[i] = p.getPlayerIP();
		}
		return ips;
	}

	public EntityPlayerMP[] getPlayers() {
		final List<EntityPlayerMP> pl = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		final EntityPlayerMP[] players = pl.toArray(new EntityPlayerMP[pl.size()]);
		return players;
	}

	public void init() {
		if (voiceChat.getServerSettings().isUsingProxy()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					externalAddress = retrieveExternalAddress();
				}

			}, "Extrernal Address Retriver Process").start();
		}
		dataManager.init();
	}

	private String retrieveExternalAddress() {
		VoiceChat.getLogger().info("Retrieving server address.");
		BufferedReader in = null;
		try {
			final URL whatismyip = new URL("http://checkip.amazonaws.com");
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			return in.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return "0.0.0.0";
	}

	public void sendEntityData(EntityPlayerMP player, int entityID, String username, double x, double y, double z) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientEntityDataPacket(entityID, username, x, y, z), player);
	}

	public void stop() {
		dataManager.reset();
	}
}
