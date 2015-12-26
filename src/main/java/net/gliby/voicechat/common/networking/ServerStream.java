package net.gliby.voicechat.common.networking;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;

public class ServerStream {

	final int id;
	long lastUpdated;
	int tick;

	/** Player who is currently speaking **/
	public EntityPlayerMP player;

	/**
	 * Used to determine which entities know about "speaker" entity, we have to do this in case the client's that
	 * receives the "speakers" stream doesn't have the entity.
	 **/
	public List<Integer> entities;
	/**
	 * If chat mode is anything other than 0, voice chat will not be 3d.
	 */
	public int chatMode;
	public boolean dirty;

	ServerStream(EntityPlayerMP player, int id, String identifier) {
		this.id = id;
		this.player = player;
		this.entities = new ArrayList<Integer>();
		this.lastUpdated = System.currentTimeMillis();
	}

	/**
	 *
	 * @returns last time stream was fed in milliseconds.
	 */

	public final int getLastTimeUpdated() {
		return (int) (System.currentTimeMillis() - lastUpdated);
	}
}
