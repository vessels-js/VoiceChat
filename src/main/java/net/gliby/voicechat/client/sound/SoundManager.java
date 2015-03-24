package net.gliby.voicechat.client.sound;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFormat;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.thread.ThreadSoundQueue;
import net.gliby.voicechat.client.sound.thread.ThreadUpdateStream;
import net.gliby.voicechat.common.PlayerProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.util.vector.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystemConfig;

public class SoundManager {

	/** Common audio format **/
	public static AudioFormat universalAudioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000.0F, 16, 1, 2, 16000.0F, false);
	/** Details for muted players **/
	public static Map<Integer, String> playerMutedData = new HashMap<Integer, String>();

	public static AudioFormat getUniversalAudioFormat() {
		return universalAudioFormat;
	}

	public List<PlayableStream> currentStreams = new ArrayList<PlayableStream>();

	public List<Integer> playersMuted = new ArrayList<Integer>();

	/** Used to queue voice data, ensures order of data and easier management. **/
	public ConcurrentLinkedQueue<Datalet> queue = new ConcurrentLinkedQueue<Datalet>();

	/** Currently streaming players, for API use currentStreams. **/
	public ConcurrentHashMap<Integer, PlayableStream> streaming = new ConcurrentHashMap<Integer, PlayableStream>();
	/** Decodes SPEEX sound data **/
	public final SoundPreProcessor soundPreProcessor;

	public ConcurrentHashMap<Integer, PlayerProxy> playerData = new ConcurrentHashMap<Integer, PlayerProxy>();
	private Thread threadUpdate;

	private ThreadSoundQueue threadQueue;
	private Minecraft mc;

	private VoiceChatClient voiceChat;

	public SoundManager(Minecraft mc, VoiceChatClient voiceChatClient) {
		this.mc = mc;
		this.voiceChat = voiceChatClient;
		soundPreProcessor = new SoundPreProcessor(voiceChatClient, mc);
	}

	public void addQueue(byte[] decoded_data, boolean global, int id) {
		if (!playersMuted.contains(id)) {
			queue.offer(new Datalet(global, id, decoded_data));
			synchronized (threadQueue) {
				threadQueue.notify();
			}
		}
	}

	private void addStreamSafe(PlayableStream stream) {
		streaming.put(stream.id, stream);
		synchronized (threadUpdate) {
			threadUpdate.notify();
		}
		String entityName = stream.player.entityName();
		for (int i = 0; i < voiceChat.getTestPlayers().length; i++) {
			String name = voiceChat.getTestPlayers()[i];
			if (stream.player.equals(name)) stream.special = 2;
		}
		if (voiceChat.specialPlayers.containsKey(entityName)) stream.special = voiceChat.specialPlayers.get(entityName);

		if (!containsStream(stream.id)) {
			List<PlayableStream> streams = new ArrayList<PlayableStream>(this.currentStreams);
			streams.add(stream);
			Collections.sort(streams, new PlayableStream.PlayableStreamComparator());
			this.currentStreams.removeAll(this.currentStreams);
			this.currentStreams.addAll(streams);
		}
	}

	public void alertEnd(int id) {
		if (!playersMuted.contains(id)) {
			queue.offer(new Datalet(false, id, null));
			synchronized (threadQueue) {
				threadQueue.notify();
			}
		}
	}

	public boolean containsStream(int id) {
		PlayableStream currentStream = streaming.get(id);
		for (int i = 0; i < this.currentStreams.size(); i++) {
			PlayableStream stream = this.currentStreams.get(i);
			String currentName = currentStream.player.entityName();
			String otherName = stream.player.entityName();
			if (stream.player.entityName() != null && currentStream.player.entityName() != null) if (currentName.equals(otherName)) return true;
			if (stream.id == id) return true;
			else continue;
		}
		return false;
	}

	public void createStream(Datalet data) {
		String identifier = generateSource(data.id);
		PlayerProxy player = getPlayerData(data.id);
		if (data.direct) {
			Vector3f position = player.position();
			voiceChat.sndSystem.rawDataStream(universalAudioFormat, true, identifier, position.x, position.y, position.z, SoundSystemConfig.ATTENUATION_LINEAR, voiceChat.getSettings().getSoundDistance());
		} else voiceChat.sndSystem.rawDataStream(universalAudioFormat, true, identifier, (float) mc.thePlayer.posX, (float) mc.thePlayer.posY, (float) mc.thePlayer.posZ, SoundSystemConfig.ATTENUATION_LINEAR, voiceChat.getSettings().getSoundDistance());
		voiceChat.sndSystem.setPitch(identifier, 1.0f);
		voiceChat.sndSystem.setVolume(identifier, voiceChat.getSettings().getWorldVolume());
		addStreamSafe(new PlayableStream(player, data.id, data.direct));
		giveStream(data);
	}

	private String generateSource(int let) {
		return "" + let;
	}

	private PlayerProxy getPlayerData(int entityId) {
		PlayerProxy proxy = playerData.get(entityId);
		EntityPlayer entity = (EntityPlayer) mc.theWorld.getEntityByID(entityId);
		if (proxy == null) {
			if (entity != null) proxy = new PlayerProxy(entity, entity.getEntityId(), entity.getCommandSenderName(), entity.posX, entity.posY, entity.posZ);
			else {
				VoiceChat.getLogger().error("Major error, no entity found for player.");
				proxy = new PlayerProxy(null, entityId, "" + entityId, 0, 0, 0);
			}
			playerData.put(entityId, proxy);
		} else {
			if (entity != null) {
				proxy.setPlayer(entity);
				proxy.setName(entity.getCommandSenderName());
			}
		}
		return proxy;
	}

	public SoundPreProcessor getSoundPreProcessor() {
		return soundPreProcessor;
	}

	public void giveEnd(int id) {
		PlayableStream stream = streaming.get(id);
		if (stream != null) stream.needsEnd = true;
	}

	public void giveStream(Datalet data) {
		PlayableStream stream = streaming.get(data.id);
		if (stream != null) {
			String identifier = generateSource(data.id);
			stream.update(data, (int) (System.currentTimeMillis() - stream.lastUpdated));
			stream.buffer.push(data.data);
			stream.buffer.updateJitter(stream.getJitterRate());
			if (stream.buffer.isReady() || stream.needsEnd) {
				voiceChat.sndSystem.flush(identifier);
				voiceChat.sndSystem.feedRawAudioData(identifier, stream.buffer.get());
				stream.buffer.clearBuffer(stream.getJitterRate());
			}
			stream.lastUpdated = System.currentTimeMillis();
		}
	}

	public void init() {
		Thread thread = new Thread(threadQueue = new ThreadSoundQueue(this), "Client Stream Queue");
		thread.start();
		threadUpdate = new Thread(new ThreadUpdateStream(this, voiceChat), "Client Stream Updater");
		threadUpdate.start();
	}

	public void killStream(PlayableStream stream) {
		if (stream != null) {
			List<PlayableStream> streams = new ArrayList<PlayableStream>(this.currentStreams);
			streams.remove(stream);
			Collections.sort(streams, new PlayableStream.PlayableStreamComparator());
			this.currentStreams.removeAll(this.currentStreams);
			this.currentStreams.addAll(streams);
			this.currentStreams.remove(stream);
			Collections.sort(this.currentStreams, new PlayableStream.PlayableStreamComparator());
			streaming.remove(stream.id);
		}
	}

	public boolean newDatalet(Datalet let) {
		return !streaming.containsKey(let.id);
	}

	public void reset() {
		volumeControlStop();
		queue.clear();
		this.streaming.clear();
		this.currentStreams.clear();
		playerData.clear();
	}

	private boolean volumeControlActive;
	private float volumeValue = 0.15F;
	private float WEATHER, MUSIC, RECORDS, BLOCKS, MOBS, ANIMALS, PLAYERS, AMBIENT;

	public void volumeControlStart() {
		if (!(mc.currentScreen instanceof GuiScreenOptionsSounds) && !volumeControlActive) {
			WEATHER = mc.gameSettings.getSoundLevel(SoundCategory.WEATHER);
			MUSIC = mc.gameSettings.getSoundLevel(SoundCategory.MUSIC);
			RECORDS = mc.gameSettings.getSoundLevel(SoundCategory.RECORDS);
			BLOCKS = mc.gameSettings.getSoundLevel(SoundCategory.BLOCKS);
			MOBS = mc.gameSettings.getSoundLevel(SoundCategory.MOBS);
			ANIMALS = mc.gameSettings.getSoundLevel(SoundCategory.PLAYERS);
			mc.gameSettings.setSoundLevel(SoundCategory.WEATHER, volumeValue);
			mc.gameSettings.setSoundLevel(SoundCategory.MUSIC, volumeValue);
			mc.gameSettings.setSoundLevel(SoundCategory.RECORDS, volumeValue);
			mc.gameSettings.setSoundLevel(SoundCategory.BLOCKS, volumeValue);
			mc.gameSettings.setSoundLevel(SoundCategory.MOBS, volumeValue);
			mc.gameSettings.setSoundLevel(SoundCategory.ANIMALS, volumeValue);
			volumeControlActive = true;
		}
	}

	public void volumeControlStop() {
		if (volumeControlActive) {
			System.out.println("Volume Control Closed!");
			mc.gameSettings.setSoundLevel(SoundCategory.WEATHER, WEATHER);
			mc.gameSettings.setSoundLevel(SoundCategory.MUSIC, MUSIC);
			mc.gameSettings.setSoundLevel(SoundCategory.RECORDS, RECORDS);
			mc.gameSettings.setSoundLevel(SoundCategory.BLOCKS, BLOCKS);
			mc.gameSettings.setSoundLevel(SoundCategory.MOBS, MOBS);
			mc.gameSettings.setSoundLevel(SoundCategory.ANIMALS, ANIMALS);
			mc.gameSettings.setSoundLevel(SoundCategory.PLAYERS, PLAYERS);
			mc.gameSettings.setSoundLevel(SoundCategory.AMBIENT, AMBIENT);
			volumeControlActive = false;
		}
	}

	public void reload() {
		if (!this.currentStreams.isEmpty()) {
			voiceChat.getLogger().info("Reloading SoundManager, removing all active streams.");
			for (int i = 0; i < this.currentStreams.size(); i++) {
				PlayableStream stream = this.currentStreams.get(i);
				killStream(stream);
			}
		}
	}
}