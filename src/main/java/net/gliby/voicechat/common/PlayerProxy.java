package net.gliby.voicechat.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.util.vector.Vector3f;

public class PlayerProxy {

	private EntityPlayer player;
	private double x, y, z;
	private String entityName;
	public boolean usesEntity;

	/**
	 * Used for entities that are not on the client, it's common that players that are < 64 away will be EntityVectors.
	 * Don't set the entity! All player proxies are stored in playerData map.
	 **/
	public PlayerProxy(EntityPlayer player, int entityID, String name, double x, double y, double z) {
		this.player = player;
		this.entityName = name;
		this.x = x;
		this.y = y;
		this.z = z;
		usesEntity = player != null;
	}

	public String entityName() {
		return entityName != null ? entityName : player.getDisplayName();
	}

	public Entity getPlayer() {
		return player;
	}

	public Vector3f position() {
		return player != null ? (usesEntity ? new Vector3f((float) player.posX, (float) player.posY, (float) player.posZ) : new Vector3f((float) x, (float) y, (float) z)) : new Vector3f((float) x, (float) y, (float) z);
	}

	public void setName(String name) {
		this.entityName = name;
	}

	public void setPlayer(EntityPlayer entity) {
		this.player = entity;
		usesEntity = true;
	}

	public void setPosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "PlayerProxy[" + entityName + ": " + x + ", " + y + "," + z + "]";
	}

	public void update(WorldClient world) {
		if (world != null) {
			player = world.getPlayerEntityByName(entityName);
			usesEntity = player != null;
		}
	}
}
