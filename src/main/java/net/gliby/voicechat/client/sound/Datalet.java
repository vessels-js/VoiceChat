package net.gliby.voicechat.client.sound;

public class Datalet {

	public final int id;
	public final byte[] data;
	public final boolean direct;

	Datalet(final boolean direct, final int id, final byte[] data) {
		this.direct = direct;
		this.id = id;
		this.data = data;
	}

}