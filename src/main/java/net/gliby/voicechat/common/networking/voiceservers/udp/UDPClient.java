package net.gliby.voicechat.common.networking.voiceservers.udp;

import java.math.BigInteger;
import java.net.InetSocketAddress;

import net.minecraft.entity.player.EntityPlayerMP;

public class UDPClient {

	public EntityPlayerMP player;
	public InetSocketAddress socketAddress;

	public String hash;

	public int key;

	public UDPClient(EntityPlayerMP player, InetSocketAddress socketAddress, String hash) {
		this.player = player;
		this.socketAddress = socketAddress;
		this.hash = hash;
		this.key = (int) new BigInteger(hash.replaceAll("[^0-9.]", "")).longValue();
	}

	@Override
	public String toString() {
		return "Client[" + socketAddress + ": "  + key + ", " + player + "]";
	}

}
