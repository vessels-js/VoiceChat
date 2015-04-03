package net.gliby.voicechat.common.networking.voiceservers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.RandomStringUtils;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceServerPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;

public class ServerConnectionHandler implements IPlayerTracker {

	VoiceChatServer voiceChat;

	public ServerConnectionHandler(VoiceChatServer vc) {
		this.voiceChat = vc;
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

	@Override
	public void onPlayerLogin(final EntityPlayer p) {
		final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		executor.schedule(new Runnable() {
			@Override
			public void run() {
				final EntityPlayerMP player = (EntityPlayerMP) p;
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
					System.out.println("Sending packet!!!!");
					voiceServer.waitingAuth.put(hash, player);
					VoiceChat.getDispatcher().sendTo(new MinecraftClientVoiceAuthenticatedServer(voiceChat.getServerSettings().canShowVoicePlates(), voiceChat.getServerSettings().canShowVoiceIcons(), voiceChat.getServerSettings().getMinimumSoundQuality(), voiceChat.getServerSettings().getMaximumSoundQuality(), voiceChat.getServerSettings().getBufferSize(), voiceChat.getServerSettings().getSoundDistance(), voiceChat.getVoiceServer().getType().ordinal(), voiceChat.getServerSettings().getUDPPort(), hash,  voiceChat.serverSettings.isUsingProxy() ? voiceChat.serverNetwork.getAddress() : "" ), player);
				} else VoiceChat.getDispatcher().sendTo(new MinecraftClientVoiceServerPacket(voiceChat.getServerSettings().canShowVoicePlates(), voiceChat.getServerSettings().canShowVoiceIcons(), voiceChat.getServerSettings().getMinimumSoundQuality(), voiceChat.getServerSettings().getMaximumSoundQuality(), voiceChat.getServerSettings().getBufferSize(), voiceChat.getServerSettings().getSoundDistance(), voiceChat.getVoiceServer().getType().ordinal()), player);
				voiceChat.serverNetwork.dataManager.entityHandler.connected(player);
			}
		}, 500, TimeUnit.MILLISECONDS);
		//500 millisecond delay otherwise it causes some funky network issues with login.
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			voiceChat.serverNetwork.dataManager.entityHandler.disconnected(player.entityId);
		}

	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
	}
}