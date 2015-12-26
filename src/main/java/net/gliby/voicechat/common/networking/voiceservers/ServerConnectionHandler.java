package net.gliby.voicechat.common.networking.voiceservers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.gui.options.GuiScreenOptionsWizard;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceServerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import sun.util.logging.resources.logging;

public class ServerConnectionHandler {

	VoiceChatServer voiceChat;

	public ServerConnectionHandler(VoiceChatServer vc) {
		this.voiceChat = vc;
		this.loggedIn = new ArrayList<GameProfile>();
	}

	private List<GameProfile> loggedIn;

	@SubscribeEvent
	public void onJoin(PlayerTickEvent event) {
		if (event.phase == Phase.END) {
			if (event.side == Side.SERVER && !loggedIn.contains(event.player.getGameProfile())) {
				loggedIn.add(event.player.getGameProfile());
				onConnected(event.player);
			}
		}
	}

	private void onConnected(final EntityPlayer entity) {
		final EntityPlayerMP player = (EntityPlayerMP) entity;
		if (voiceChat.getVoiceServer() instanceof VoiceAuthenticatedServer) {
			final VoiceAuthenticatedServer voiceServer = (VoiceAuthenticatedServer) voiceChat.getVoiceServer();
			String hash = null;
			while (hash == null) {
				try {
					hash = sha256(RandomStringUtils.random(32));
				} catch (final NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
			voiceServer.waitingAuth.put(hash, player);
			VoiceChat.getDispatcher().sendTo(new MinecraftClientVoiceAuthenticatedServer(voiceChat.getServerSettings().canShowVoicePlates(), voiceChat.getServerSettings().canShowVoiceIcons(), voiceChat.getServerSettings().getMinimumSoundQuality(), voiceChat.getServerSettings().getMaximumSoundQuality(), voiceChat.getServerSettings().getBufferSize(), voiceChat.getServerSettings().getSoundDistance(),
					voiceChat.getVoiceServer().getType().ordinal(), voiceChat.getServerSettings().getUDPPort(), hash, voiceChat.serverSettings.isUsingProxy() ? voiceChat.serverNetwork.getAddress() : ""), player);
		} else VoiceChat.getDispatcher().sendTo(new MinecraftClientVoiceServerPacket(voiceChat.getServerSettings().canShowVoicePlates(), voiceChat.getServerSettings().canShowVoiceIcons(), voiceChat.getServerSettings().getMinimumSoundQuality(), voiceChat.getServerSettings().getMaximumSoundQuality(), voiceChat.getServerSettings().getBufferSize(), voiceChat.getServerSettings().getSoundDistance(),
				voiceChat.getVoiceServer().getType().ordinal()), player);
		voiceChat.serverNetwork.dataManager.entityHandler.connected(player);
	}

	@SubscribeEvent
	public void onDisconnect(final PlayerEvent.PlayerLoggedOutEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			loggedIn.remove(event.player.getGameProfile());
			voiceChat.serverNetwork.dataManager.entityHandler.disconnected(event.player.getEntityId());
		}
	}

	private String sha256(String s) throws NoSuchAlgorithmException {
		byte[] hash = null;
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA-256");
			hash = md.digest(s.getBytes());
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < hash.length; ++i) {
			final String hex = Integer.toHexString(hash[i]);
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