package net.gliby.voicechat.client.gui;

public class UIPosition {

	public int type;

	public EnumUIPlacement info;
	public float scale, x, y;

	public UIPosition(EnumUIPlacement info, float x, float y, int type, float scale) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.info = info;
		this.scale = scale;
	}
}
