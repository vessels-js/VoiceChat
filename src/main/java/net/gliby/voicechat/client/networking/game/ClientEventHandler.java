package net.gliby.voicechat.client.networking.game;

import java.util.Map;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.common.PlayerProxy;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {

	VoiceChatClient voiceChat;

	public ClientEventHandler(VoiceChatClient voiceChatClient) {
		this.voiceChat = voiceChatClient;
	}

	@SubscribeEvent
	public void entityJoinWorld(final EntityJoinWorldEvent event) {
		if (event.world.isRemote) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (event.entity instanceof EntityOtherPlayerMP) {
						EntityOtherPlayerMP player = (EntityOtherPlayerMP) event.entity;
						if (!voiceChat.getSoundManager().playersMuted.contains(player.getEntityId())) {
							for (Map.Entry<Integer, String> entry : voiceChat.getSoundManager().playerMutedData.entrySet()) {
								Integer key = entry.getKey();
								String value = entry.getValue();
								if (value.equals(player.getCommandSenderName())) {
									voiceChat.getSoundManager().playersMuted.remove(key);
									voiceChat.getSoundManager().playerMutedData.remove(key);
									voiceChat.getSoundManager().playersMuted.add(player.getEntityId());
									voiceChat.getSoundManager().playerMutedData.put(player.getEntityId(), player.getCommandSenderName());
									break;
								}
							}
						}
						PlayerProxy proxy = voiceChat.getSoundManager().playerData.get(player.getEntityId());
						if (proxy != null) {
							proxy.setPlayer(player);
							proxy.setName(player.getDisplayName());
							proxy.usesEntity = true;
						}
					}
				}
			});
		}
	}
}
