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

	private VoiceChatServer voiceChat;

	private String externalAddress;

	public final DataManager dataManager;

	public ServerNetwork(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
		dataManager = new DataManager(voiceChat);
	}

	public String getAddress() {
		return externalAddress;
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public String[] getPlayerIPs() {
		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		String[] ips = new String[players.size()];
		for (int i = 0; i < players.size(); i++) {
			EntityPlayerMP p = (EntityPlayerMP) players.get(i);
			ips[i] = p.getPlayerIP();
		}
		return ips;
	}

	public EntityPlayerMP[] getPlayers() {
		List<EntityPlayerMP> pl = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		EntityPlayerMP[] players = pl.toArray(new EntityPlayerMP[pl.size()]);
		return players;
	}

	public void init() {
		if (voiceChat.getServerSettings().isUsingProxy()) externalAddress = retrieveExternalAddress();
		dataManager.init();
	}

	private String retrieveExternalAddress() {
		BufferedReader in = null;
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void sendEntityData(EntityPlayerMP player, int entityID, String username, double x, double y, double z) {
		VoiceChat.getDispatcher().sendTo(new MinecraftClientEntityDataPacket(entityID, username, x, y, z), player);
	}

	public void stop() {
		dataManager.reset();
	}
}
