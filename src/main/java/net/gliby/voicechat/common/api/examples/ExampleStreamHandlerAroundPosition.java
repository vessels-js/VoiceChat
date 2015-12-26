/**
 * Copyright (c) 2015, Gliby. * http://www.gliby.net/
 */
package net.gliby.voicechat.common.api.examples;

import java.util.List;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.api.events.ServerStreamEvent;
import net.gliby.voicechat.common.networking.ServerDatalet;
import net.gliby.voicechat.common.networking.ServerStream;
import net.gliby.voicechat.common.networking.ServerStreamManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ExampleStreamHandlerAroundPosition {

	@SubscribeEvent
	public void feedStream(ServerStreamEvent.StreamFeed event) {
		event.stream.player.mcServer.getConfigurationManager();
		feedStreamPositionWithRadius(event.streamManager, event.stream, event.voiceLet, event.stream.player.worldObj, 0, 128, 0, VoiceChat.getServerInstance().getServerSettings().getSoundDistance());
	}

	/**
	 *
	 * @param streamManager
	 * @param stream
	 * @param voiceData
	 * @param world World to stream to, you can use Entities .worldObj
	 * @param x
	 * @param y
	 * @param z
	 * @param distance Distance should be the same as the distance in voice chat settings, otherwise it will stream globally.
	 */
	public void feedStreamPositionWithRadius(ServerStreamManager streamManager, ServerStream stream, ServerDatalet voiceData, World world, double x, double y, double z, int distance) {
		final EntityPlayerMP speaker = stream.player;
		final List<EntityPlayerMP> players = world.playerEntities;
		for (int i = 0; i < players.size(); i++) {
			final EntityPlayerMP target = players.get(i);
			if(target.getEntityId() != speaker.getEntityId()) {
				final double d4 = x - target.posX;
				final double d5 = y - target.posY;
				final double d6 = z - target.posZ;
				if (d4 * d4 + d5 * d5 + d6 * d6 < distance * distance) {
					streamManager.feedStreamToPlayer(stream, voiceData, target, distance == VoiceChat.getServerInstance().getServerSettings().getSoundDistance());
				}
			}
		}
	}

}
