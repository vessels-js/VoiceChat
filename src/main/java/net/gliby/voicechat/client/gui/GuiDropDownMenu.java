package net.gliby.voicechat.client.gui;

import net.gliby.voicechat.client.textures.IndependentGUITexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class GuiDropDownMenu extends GuiButton {

	String[] array;

	boolean[] mouseOn;

	private final int prevHeight;
	private int amountOfItems = 1;
	public boolean dropDownMenu = false;
	public int selectedInteger;

	public GuiDropDownMenu(int par1, int par2, int par3, int par4, int par5, String par6Str, String[] array) {
		super(par1, par2, par3, par4, par5, par6Str);
		prevHeight = height;
		this.array = array;
		amountOfItems = array.length;
		mouseOn = new boolean[amountOfItems];
	}

	public GuiDropDownMenu(int par1, int par2, int par3, String par4Str, String[] array) {
		super(par1, par2, par3, par4Str);
		prevHeight = height;
		this.array = array;
		amountOfItems = array.length;
		mouseOn = new boolean[amountOfItems];
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int x, int y) {
		if (this.visible) {
			if (dropDownMenu && array.length != 0) {
				height = prevHeight * (amountOfItems + 1);
			} else height = prevHeight;

			final FontRenderer fontrenderer = par1Minecraft.fontRenderer;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
			this.getHoverState(this.field_146123_n);
			this.mouseDragged(par1Minecraft, x, y);
			int l = 14737632;
			drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
			drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
			drawRect(this.xPosition - 1, this.yPosition + this.prevHeight, this.xPosition + this.width + 1, this.yPosition + this.prevHeight + 1, -6250336);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			int u = 256 - 14;
			if (dropDownMenu && array.length != 0) u = 256 - 28;
			else u = 256 - 14;
			if (!this.enabled) l = -6250336;
			this.drawCenteredString(fontrenderer, this.displayString.substring(0, Math.min(displayString.length(), 22)), this.xPosition + this.width / 2, this.yPosition + (this.prevHeight - 8) / 2, l);
			GL11.glPushMatrix();
			if (dropDownMenu && array.length != 0) {
				for (int i = 0; i < amountOfItems; i++) {
					mouseOn[i] = inBounds(x, y, xPosition, yPosition + (this.prevHeight * (i + 1)), width, prevHeight);
					final String s = array[i].substring(0, Math.min(array[i].length(), 26)) + "..";
					this.drawCenteredString(fontrenderer, s, this.xPosition + this.width / 2, this.yPosition + (this.prevHeight * (i + 1)) + 15 / 2, mouseOn[i] ? 16777120 : 14737632);
				}
			}
			GL11.glPopMatrix();
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			IndependentGUITexture.TEXTURES.bindTexture(Minecraft.getMinecraft());
			drawTexturedModalRect(xPosition + width - 15, yPosition + 2, u, 0, 14, 14);
		}
	}

	public int getMouseOverInteger() {
		for (int i = 0; i < mouseOn.length; i++) {
			if (mouseOn[i]) return i;
		}
		return -1;
	}

	public boolean inBounds(int x, int y, int posX, int posY, int width, int height) {
		return this.enabled && this.visible && x >= posX && y >= posY && x < posX + width && y < posY + height;
	}

	public void setArray(String[] array) {
		this.array = array;
		amountOfItems = array.length;
		mouseOn = new boolean[amountOfItems];
	}

	public void setDisplayString(String s) {
		this.displayString = s;
	}

}