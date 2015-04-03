package net.gliby.voicechat.client.networking.game;

import java.util.Map;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.gliby.voicechat.common.PlayerProxy;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

public class ClientEventHandler {

	VoiceChatClient voiceChat;

	public ClientEventHandler(VoiceChatClient voiceChatClient) {
		this.voiceChat = voiceChatClient;
	}

	@ForgeSubscribe
	public void entityJoinWorld(final EntityJoinWorldEvent event) {
		if (event.world.isRemote) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (event.entity instanceof EntityOtherPlayerMP) {
						final EntityOtherPlayerMP player = (EntityOtherPlayerMP) event.entity;
						if (!VoiceChatClient.getSoundManager().playersMuted.contains(player.entityId)) {
							VoiceChatClient.getSoundManager();
							for (final Map.Entry<Integer, String> entry : ClientStreamManager.playerMutedData.entrySet()) {
								final Integer key = entry.getKey();
								final String value = entry.getValue();
								if (value.equals(player.getCommandSenderName())) {
									VoiceChatClient.getSoundManager().playersMuted.remove(key);
									VoiceChatClient.getSoundManager();
									ClientStreamManager.playerMutedData.remove(key);
									VoiceChatClient.getSoundManager().playersMuted.add(player.entityId);
									VoiceChatClient.getSoundManager();
									ClientStreamManager.playerMutedData.put(player.entityId, player.getCommandSenderName());
									break;
								}
							}
						}

						final PlayerProxy proxy = VoiceChatClient.getSoundManager().playerData.get(player.entityId);
						if (proxy != null) {
							proxy.setPlayer(player);
							proxy.setName(player.getCommandSenderName());
							proxy.usesEntity = true;
						}
					}
				}
			}, "Entity Join Process").start();
		}
	}
}
