package net.gliby.voicechat.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public abstract class GuiPlaceableInterface {

	public int screenWidth, screenHeight;
	public UIPosition positionUI;
	float x, y;

	int positionType = 0, width, height;
	float scale = 1.0f;
	EnumUIPlacement info;

	public GuiPlaceableInterface(UIPosition position, int width, int height) {
		this.positionUI = position;
		this.info = position.info;
		this.x = position.type == 0 ? position.x * width : position.x;
		this.y = position.type == 0 ? position.y * height : position.y;
		this.positionType = position.type;
		this.screenWidth = width;
		this.screenHeight = height;
		this.scale = position.scale;
	}

	public abstract void draw(Minecraft mc, GuiScreen gui, int x, int y, float tick);

	public void update(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.screenWidth = width;
		this.screenHeight = height;
	}
}
