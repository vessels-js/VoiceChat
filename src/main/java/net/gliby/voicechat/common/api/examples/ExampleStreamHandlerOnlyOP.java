/**
 * Copyright (c) 2015, Gliby. * http://www.gliby.net/
 */
package net.gliby.voicechat.common.api.examples;

import java.util.List;

import net.gliby.voicechat.common.api.VoiceChatAPI;
import net.gliby.voicechat.common.api.events.ServerStreamEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
/**
 *  What does this example do? Causes OP's to only hear/talk other OP'S.
 */
public class ExampleStreamHandlerOnlyOP {

	public ExampleStreamHandlerOnlyOP() {
		//Tells API to use this stream handler, because we don't want to use the original one. Otherwise audio will be sent twice.
		//You don't have to do this if you want to modify or monitor events.
		VoiceChatAPI.instance().setCustomStreamHandler(this);
	}

	@SubscribeEvent
	public void createStream(ServerStreamEvent.StreamCreated event) {
		if(!isOP(event.stream.player))
			event.stream.player.addChatMessage(new ChatComponentText("Only OP's are allowed to talk!"));
	}

	@SubscribeEvent
	public void feedStream(ServerStreamEvent.StreamFeed event) {
		final List<EntityPlayerMP> players = event.stream.player.mcServer.getConfigurationManager().playerEntityList;
		final EntityPlayerMP speaker = event.stream.player;
		if(isOP(speaker)) {
			for(int i = 0; i < players.size(); i++) {
				final EntityPlayerMP player = players.get(i);
				if(isOP(player) && player.getEntityId() != speaker.getEntityId()) {
					event.streamManager.feedStreamToPlayer(event.stream, event.voiceLet, player, false);
				}
			}
		}
	}

	public boolean isOP(EntityPlayerMP player) {
		return player.mcServer.getConfigurationManager().func_152603_m().func_152683_b(player.getGameProfile()) != null;
	}

}
