package net.gliby.voicechat.common.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.api.VoiceChatAPI;
import net.gliby.voicechat.common.api.events.ServerStreamEvent;
import net.gliby.voicechat.common.networking.entityhandler.EntityHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class ServerStreamManager {

	List<ServerStream> currentStreams;

	ConcurrentLinkedQueue<ServerDatalet> dataQueue;
	public ConcurrentHashMap<Integer, ServerStream> streaming;

	public HashMap<UUID, Integer> chatModeMap;
	private HashMap<Integer, List<Integer>> receivedEntityData;

	private Thread threadUpdate, treadQueue;
	private final VoiceChatServer voiceChat;
	public List<UUID> mutedPlayers;
	public EntityHandler entityHandler;

	volatile boolean running;

	ServerStreamManager(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
		
	}

	public void addQueue(EntityPlayerMP player, byte[] decoded_data, byte divider, int id, boolean end) {
		if (mutedPlayers.contains(player.getPersistentID())) return;
		dataQueue.offer(new ServerDatalet(player, id, decoded_data, divider, end));
		synchronized (treadQueue) {
			treadQueue.notify();
		}
	}

	private void addStreamSafe(ServerStream stream) {
		streaming.put(stream.id, stream);
		currentStreams.add(stream);
		synchronized (threadUpdate) {
			threadUpdate.notify();
		}
	}

	public void createStream(ServerDatalet data) {
		ServerStream stream;
		addStreamSafe(stream = new ServerStream(data.player, data.id, generateSource(data)));
		VoiceChatAPI.instance().bus().post(new ServerStreamEvent.StreamCreated(this, stream, data));
		giveStream(stream, data);
	}
	
	
	/**
	 * Transfers stream data to all players.
	 * @param stream
	 * @param let
	 */
	public void feedStreamToAllPlayers(ServerStream stream, ServerDatalet let) {
		final EntityPlayerMP speaker = let.player;
		final List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		if (let.end) {
			for (int i = 0; i < players.size(); i++) {
				final EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					voiceChat.getVoiceServer().sendVoiceEnd(target, let.id);
				}
			}
		} else {
			for (int i = 0; i < players.size(); i++) {
				final EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					entityHandler.whileSpeaking(stream, speaker, target);
					voiceChat.getVoiceServer().sendChunkVoiceData(target, let.id, false, let.data, let.divider);
				}
			}
		}
	}

	/**
	 * Transfers stream data to all players within the same world as the speaker.
	 * @param stream
	 * @param let
	 */
	public void feedStreamToWorld(ServerStream stream, ServerDatalet let) {
		final EntityPlayerMP speaker = let.player;
		final List<EntityPlayerMP> players = speaker.worldObj.playerEntities;
		if (let.end) {
			for (int i = 0; i < players.size(); i++) {
				final EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					voiceChat.getVoiceServer().sendVoiceEnd(target, let.id);
				}
			}
		} else {
			for (int i = 0; i < players.size(); i++) {
				final EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					entityHandler.whileSpeaking(stream, speaker, target);
					voiceChat.getVoiceServer().sendChunkVoiceData(target, let.id, false, let.data, let.divider);
				}
			}
		}
	}
	
	/**
	 * Transfers stream data to players within distance of speaker.
	 * @param stream
	 * @param let
	 * @param distance
	 */
	public void feedWithinEntityWithRadius(ServerStream stream, ServerDatalet let, int distance) {
		final EntityPlayerMP speaker = let.player;
		final List<EntityPlayerMP> players = speaker.worldObj.playerEntities;
		if (let.end) {
			for (int i = 0; i < players.size(); i++) {
				final EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					final double d4 = speaker.posX - target.posX;
					final double d5 = speaker.posY - target.posY;
					final double d6 = speaker.posZ - target.posZ;
					if (d4 * d4 + d5 * d5 + d6 * d6 < distance * distance) {
						voiceChat.getVoiceServer().sendVoiceEnd(target, let.id);
					}
				}
			}
		} else {
			for (int i = 0; i < players.size(); i++) {
				final EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					final double d4 = speaker.posX - target.posX;
					final double d5 = speaker.posY - target.posY;
					final double d6 = speaker.posZ - target.posZ;
					final double distanceBetween = d4 * d4 + d5 * d5 + d6 * d6;
					if (distanceBetween < distance * distance) {
						entityHandler.whileSpeaking(stream, speaker, target);
						voiceChat.getVoiceServer().sendChunkVoiceData(target, let.id, true, let.data, let.divider);
						if (stream.tick % voiceChat.serverSettings.positionUpdateRate == 0) {
							if (distanceBetween > 64 * 64) voiceChat.getVoiceServer().sendEntityPosition(target, speaker.getEntityId(), speaker.posX, speaker.posY, speaker.posZ);
							stream.tick = 0;
						}
						stream.tick++;
					}
				}
			}
		}
	}

	private String generateSource(ServerDatalet let) {
		return Integer.toString(let.id);
	}

	public ServerStream getStream(int entityId) {
		return streaming.get(entityId);
	}

	public void giveEntity(EntityPlayerMP receiver, EntityPlayerMP speaker) {
		voiceChat.getServerNetwork().sendEntityData(receiver, speaker.getEntityId(), speaker.getCommandSenderName(), speaker.posX, speaker.posY, speaker.posZ);

	}

	public void giveStream(ServerStream stream, ServerDatalet let) {
		VoiceChatAPI.instance().bus().post(new ServerStreamEvent.StreamFeed(this, stream, let));
		stream.lastUpdated = System.currentTimeMillis();
		if (let.end) killStream(stream);
	}

	public void init() {
		running = true;
		VoiceChatAPI.instance().bus().register(new ServerStreamHandler(voiceChat));
		entityHandler = new EntityHandler(voiceChat);
		mutedPlayers = new ArrayList<UUID>();
		dataQueue = new ConcurrentLinkedQueue();
		currentStreams = new ArrayList<ServerStream>();
		streaming = new ConcurrentHashMap<Integer, ServerStream>();
		chatModeMap = new HashMap<UUID, Integer>();
		receivedEntityData = new HashMap<Integer, List<Integer>>();
		treadQueue = new Thread(new ThreadDataQueue(this), "Stream Queue");
		treadQueue.start();
		threadUpdate = new Thread(new ThreadDataUpdateStream(this), "Stream Update");
		threadUpdate.start();
	}

	public void killStream(ServerStream stream) {
		currentStreams.remove(stream);
		streaming.remove(stream.id);
		VoiceChatAPI.instance().bus().post(new ServerStreamEvent.StreamDestroyed(this, stream));
	}

	public ServerStream newDatalet(ServerDatalet let) {
		return streaming.get(let.id);
	}

	public void reset() {
		running = false;
		currentStreams.clear();
		this.chatModeMap.clear();
		this.dataQueue.clear();
		this.mutedPlayers.clear();
		this.receivedEntityData.clear();
		this.streaming.clear();
	}
}
