package net.gliby.voicechat.common.networking.voiceservers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.PacketDispatcher;
import net.gliby.voicechat.common.networking.PacketManager;
import net.gliby.voicechat.common.networking.packets.PacketClientVoiceServer;
import net.gliby.voicechat.common.networking.packets.PacketClientVoiceServerAuth;
import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.commons.lang3.RandomStringUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class ServerConnectionHandler {

	VoiceChatServer voiceChat;

	public ServerConnectionHandler(VoiceChatServer vc) {
		this.voiceChat = vc;
	}

	@SubscribeEvent
	public void onConnected(final PlayerEvent.PlayerLoggedInEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
			executor.schedule(new Runnable() {
				@Override
				public void run() {
					EntityPlayerMP player = (EntityPlayerMP) event.player;
					if (voiceChat.getVoiceServer() instanceof VoiceAuthenticatedServer) {
						VoiceAuthenticatedServer voiceServer = (VoiceAuthenticatedServer) voiceChat.getVoiceServer();
						String hash = null;
						while (hash == null) {
							try {
								hash = sha256(RandomStringUtils.random(32));
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							}
						}
						EntityPlayerMP entity = (EntityPlayerMP) player;
						voiceServer.waitingAuth.put(hash, player);
						PacketDispatcher.sendPacketToPlayer(new PacketClientVoiceServerAuth(PacketManager.getVoiceServerAutheticationPacket(voiceChat, voiceServer.getType(), hash, voiceChat.serverSettings.isUsingProxy() ? voiceChat.serverNetwork.getAddress() : "")), player);
					} else PacketDispatcher.sendPacketToPlayer(new PacketClientVoiceServer(PacketManager.getVoiceServerPacket(voiceChat, voiceChat.getVoiceServer().getType())), player);
					voiceChat.serverNetwork.dataManager.entityHandler.connected(player);
				}
			}, 500, TimeUnit.MILLISECONDS);
		}
	}

	@SubscribeEvent
	public void onDisconnect(final PlayerEvent.PlayerLoggedOutEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			// if(!event.player.isClientWorld()) {
			EntityPlayerMP player = (EntityPlayerMP) event.player;
			voiceChat.serverNetwork.dataManager.entityHandler.disconnected(player);
			// }
		}
	}

	private String sha256(String s) throws NoSuchAlgorithmException {
		byte[] hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			hash = md.digest(s.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hash.length; ++i) {
			String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				sb.append(0);
				sb.append(hex.charAt(hex.length() - 1));
			} else {
				sb.append(hex.substring(hex.length() - 2));
			}
		}
		return sb.toString();
	}
}