package net.gliby.voicechat.common.networking;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

public class DataStream {

	final int id;
	long lastUpdated;
	int tick;

	/** Player who is currently speaking **/
	public EntityPlayerMP player;

	/**
	 * Used to determine which entities know about "speaker" entity, we have to do this in case the client's that
	 * receive the "speakers" stream don't have the entity.
	 **/
	public List<Integer> entities;
	public int chatMode;
	public boolean dirty;

	DataStream(EntityPlayerMP player, int id, String identifier, int chatMode) {
		this.id = id;
		this.player = player;
		this.entities = new ArrayList<Integer>();
		this.lastUpdated = System.currentTimeMillis();
		this.chatMode = chatMode;
	}

	public final int getLastTimeUpdatedMS() {
		return (int) (System.currentTimeMillis() - lastUpdated);
	}
}
