package net.gliby.voicechat.common.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.entityhandler.EntityHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
//TODO Finish by 14th, create API with functioning examples.
public class DataManager {

	List<DataStream> currentStreams;

	ConcurrentLinkedQueue<ServerDatalet> dataQueue;
	public ConcurrentHashMap<Integer, DataStream> streaming;

	public HashMap<UUID, Integer> chatModeMap;
	private HashMap<Integer, List<Integer>> receivedEntityData;

	private EntityHandler handler;
	private Thread threadUpdate, treadQueue;
	private VoiceChatServer voiceChat;
	public List<UUID> mutedPlayers;
	public EntityHandler entityHandler;

	volatile boolean running;

	DataManager(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
	}

	public void addQueue(EntityPlayerMP player, byte[] decoded_data, byte divider, int id, boolean end) {
		if (mutedPlayers.contains(player.getPersistentID())) return;
		dataQueue.offer(new ServerDatalet(player, id, decoded_data, divider, end));
		synchronized (treadQueue) {
			treadQueue.notify();
		}
	}

	private void addStreamSafe(DataStream stream) {
		streaming.put(stream.id, stream);
		currentStreams.add(stream);
		synchronized (threadUpdate) {
			threadUpdate.notify();
		}
	}

	public void createStream(ServerDatalet data) {
		int chatMode = voiceChat.getServerSettings().getDefaultChatMode();
		if (chatModeMap.containsKey(data.player.getPersistentID())) chatMode = chatModeMap.get(data.player.getPersistentID());
		DataStream stream;
		addStreamSafe(stream = new DataStream(data.player, data.id, generateSource(data), chatMode));
		giveStream(stream, data);
	}

	private String generateSource(ServerDatalet let) {
		return Integer.toString(let.id);
	}

	public void giveEntity(EntityPlayerMP receiver, EntityPlayerMP speaker) {
		voiceChat.getServerNetwork().sendEntityData(receiver, speaker.getEntityId(), speaker.getCommandSenderName(), speaker.posX, speaker.posY, speaker.posZ);

	}

	public void giveStream(DataStream stream, ServerDatalet let) {
		if(stream.dirty) {
			if (chatModeMap.containsKey(stream.player.getPersistentID()))
				stream.chatMode = chatModeMap.get(stream.player.getPersistentID());
		}
		
		if(stream.chatMode == 0)
			sendVoiceToWorldWithinDistance(stream, let, voiceChat.getServerSettings().getSoundDistance());
		if(stream.chatMode == 1)
			sendVoiceToWorld(stream, let);
		if(stream.chatMode == 2)
			sendToServer(stream, let);
		stream.lastUpdated = System.currentTimeMillis();
		if (let.end) killStream(stream);
	}

	public DataStream getStream(int entityId) {
		return streaming.get(entityId);
	}

	public void init() {
		running = true;
		entityHandler = new EntityHandler(voiceChat);
		mutedPlayers = new ArrayList<UUID>();
		dataQueue = new ConcurrentLinkedQueue();
		currentStreams = new ArrayList<DataStream>();
		streaming = new ConcurrentHashMap<Integer, DataStream>();
		chatModeMap = new HashMap<UUID, Integer>();
		receivedEntityData = new HashMap<Integer, List<Integer>>();
		treadQueue = new Thread(new ThreadDataQueue(this), "Stream Queue");
		treadQueue.start();
		threadUpdate = new Thread(new ThreadDataUpdateStream(this), "Stream Update");
		threadUpdate.start();
	}

	public void killStream(DataStream stream) {
		currentStreams.remove(stream);
		streaming.remove(stream.id);
	}

	public DataStream newDatalet(ServerDatalet let) {
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
	
	private void sendToServer(DataStream stream, ServerDatalet let) {
		EntityPlayerMP speaker = let.player;
		List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		if (let.end) {
			for (int i = 0; i < players.size(); i++) {
				EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					voiceChat.getVoiceServer().sendVoiceEnd(target, let.id);
				}
			}
		} else {
			for (int i = 0; i < players.size(); i++) {
				EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					entityHandler.whileSpeaking(stream, speaker, target);
					voiceChat.getVoiceServer().sendChunkVoiceData(target, let.id, false, let.data, let.divider);
				}
			}
		}
	}

	private void sendVoiceToWorld(DataStream stream, ServerDatalet let) {
		EntityPlayerMP speaker = let.player;
		List<EntityPlayerMP> players = speaker.worldObj.playerEntities;
		if (let.end) {
			for (int i = 0; i < players.size(); i++) {
				EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					voiceChat.getVoiceServer().sendVoiceEnd(target, let.id);
				}
			}
		} else {
			for (int i = 0; i < players.size(); i++) {
				EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					entityHandler.whileSpeaking(stream, speaker, target);
					voiceChat.getVoiceServer().sendChunkVoiceData(target, let.id, false, let.data, let.divider);
				}
			}
		}
	}

	public void sendVoiceToWorldWithinDistance(DataStream stream, ServerDatalet let, int distance) {
		EntityPlayerMP speaker = let.player;
		List<EntityPlayerMP> players = speaker.worldObj.playerEntities;
		if (let.end) {
			for (int i = 0; i < players.size(); i++) {
				EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					double d4 = speaker.posX - target.posX;
					double d5 = speaker.posY - target.posY;
					double d6 = speaker.posZ - target.posZ;
					if (d4 * d4 + d5 * d5 + d6 * d6 < distance * distance) {
						voiceChat.getVoiceServer().sendVoiceEnd(target, let.id);
					}
				}
			}
		} else {
			for (int i = 0; i < players.size(); i++) {
				EntityPlayerMP target = players.get(i);
				if (target.getEntityId() != speaker.getEntityId()) {
					double d4 = speaker.posX - target.posX;
					double d5 = speaker.posY - target.posY;
					double d6 = speaker.posZ - target.posZ;
					double distanceBetween = d4 * d4 + d5 * d5 + d6 * d6;
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
}
