package net.gliby.voicechat.common.networking;

import net.minecraft.entity.player.EntityPlayerMP;

public class ServerDatalet {
	public final EntityPlayerMP player;
	public final int id;
	public final byte[] data;
	public boolean end;
	public byte divider;
	public byte volume;

	public ServerDatalet(final EntityPlayerMP player, final int id, final byte[] data, byte divider, boolean end, byte volume) {
		this.player = player;
		this.id = id;
		this.data = data;
		this.end = end;
		this.divider = divider;
		this.volume = volume;
	}
}