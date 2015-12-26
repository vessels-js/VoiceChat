package net.gliby.voicechat.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.Settings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiUIPlacement extends GuiScreen {

	public static void drawRectLines(int par0, int par1, int par2, int par3, int par4) {
		int j1;
		if (par0 < par2) {
			j1 = par0;
			par0 = par2;
			par2 = j1;
		}
		if (par1 < par3) {
			j1 = par1;
			par1 = par3;
			par3 = j1;
		}
		final float f = (par4 >> 24 & 255) / 255.0F;
		final float f1 = (par4 >> 16 & 255) / 255.0F;
		final float f2 = (par4 >> 8 & 255) / 255.0F;
		final float f3 = (par4 & 255) / 255.0F;
		final Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(f1, f2, f3, f);
		tessellator.startDrawing(2);
		tessellator.addVertex(par0, par3, 0.0D);
		tessellator.addVertex(par2, par3, 0.0D);
		tessellator.addVertex(par2, par1, 0.0D);
		tessellator.addVertex(par0, par1, 0.0D);
		tessellator.draw();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}

	private final List<GuiPlaceableInterface> placeables = new ArrayList<GuiPlaceableInterface>();
	private final GuiScreen parent;

	private int offsetX, offsetY;

	public String[] positionTypes = new String[2];
	private GuiButton positionTypeButton, resetButton;
	private GuiBoostSlider scaleSlider;

	private GuiPlaceableInterface selectedUIPlaceable, lastSelected;

	public GuiUIPlacement(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			if (lastSelected != null) {
				if (lastSelected.positionType >= 1) lastSelected.positionType = 0;
				else lastSelected.positionType++;
			}
		}

		if (button.id == 1) {
			if (lastSelected != null) {
				if (lastSelected.info.positionType == 0) {
					lastSelected.x = lastSelected.info.x * width;
					lastSelected.y = lastSelected.info.y * height;
				} else {
					lastSelected.x = lastSelected.info.x;
					lastSelected.y = lastSelected.info.y;
				}
			}
		}
	}

	public void drawRectWH(int x, int y, int width, int height, int color) {
		drawRect(x, y, width + x, height + y, color);
	}

	@Override
	public void drawScreen(int x, int y, float tick) {
		drawDefaultBackground();
		drawCenteredString(fontRendererObj, I18n.format("menu.pressESCtoReturn"), (width / 2), 2, -1);
		if (selectedUIPlaceable != null) {
			selectedUIPlaceable.x = x - offsetX;
			selectedUIPlaceable.y = y - offsetY;
			if (!Mouse.isButtonDown(0)) {
				selectedUIPlaceable = null;
			}
		}

		if (lastSelected != null) {
			scaleSlider.setDisplayString(I18n.format("menu.scale") + ": " + (int) (lastSelected.scale * 100) + "%");
			scaleSlider.sliderValue = lastSelected.scale;
			final boolean rightSide = inBounds(lastSelected.x + lastSelected.width + 151, lastSelected.y + 42, width, 0, width, height * 2);
			final boolean topSide = inBounds(lastSelected.x + lastSelected.width - 75, lastSelected.y, -width, -height, width * 2, height);
			final boolean bottomSide = inBounds(lastSelected.x + lastSelected.width, lastSelected.y + 66, 0, height, width * 2, height);
			positionTypeButton.xPosition = (int) (lastSelected.x + (rightSide ? -100 : lastSelected.width + 2));
			positionTypeButton.yPosition = (int) (lastSelected.y - (bottomSide ? (lastSelected.y + 66) - height : (topSide ? lastSelected.y - 0 : 0)));
			scaleSlider.xPosition = (int) (lastSelected.x + (rightSide ? -154 : lastSelected.width + 2));
			scaleSlider.yPosition = (int) (lastSelected.y + 22 - (bottomSide ? (lastSelected.y + 66) - height : (topSide ? lastSelected.y - 0 : 0)));
			resetButton.xPosition = (int) (lastSelected.x + (rightSide ? -100 : lastSelected.width + 2));
			resetButton.yPosition = (int) (lastSelected.y + 44 - (bottomSide ? (lastSelected.y + 66) - height : (topSide ? lastSelected.y - 0 : 0)));
			positionTypeButton.displayString = I18n.format("menu.position") + ": " + positionTypes[lastSelected.positionType];
			positionTypeButton.drawButton(mc, x, y);
			resetButton.drawButton(mc, x, y);
			scaleSlider.drawButton(mc, x, y);
			lastSelected.scale = scaleSlider.sliderValue;
		}

		for (int i = 0; i < placeables.size(); i++) {
			final GuiPlaceableInterface placeable = placeables.get(i);
			GL11.glPushMatrix();
			GL11.glTranslatef(placeable.x, placeable.y, 0);
			placeable.draw(mc, this, x, y, tick);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glTranslatef(placeable.x, placeable.y, 0);
			GL11.glLineWidth(4.0f);
			drawRectLines(0, 0, placeable.width, placeable.height, selectedUIPlaceable == placeable ? 0xff00FF00 : -1);
			GL11.glLineWidth(1.0f);
			GL11.glPopMatrix();
		}
	}

	public boolean inBounds(float mouseX, float mouseY, float x, float y, float width, float height) {
		return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
	}

	@Override
	public void initGui() {
		positionTypes[0] = I18n.format("menu.positionAutomatic");
		positionTypes[1] = I18n.format("menu.positionAbsolute");
		if (scaleSlider == null) {
			placeables.add(new GuiUIPlacementSpeak(VoiceChat.getProxyInstance().getSettings().getUIPositionSpeak(), width, height));
			placeables.add(new GuiUIPlacementVoicePlate(VoiceChat.getProxyInstance().getSettings().getUIPositionPlate(), width, height));
		}
		buttonList.add(positionTypeButton = new GuiButton(0, 2, 2, 96, 20, "Position: Automatic"));
		buttonList.add(resetButton = new GuiButton(1, 2, 2, 96, 20, I18n.format("menu.resetLocation")));
		buttonList.add(scaleSlider = new GuiBoostSlider(2, 2, 2, "", "Scale: 100%", 0));
		for (int i = 0; i < placeables.size(); i++) {
			final GuiPlaceableInterface placeableInterface = placeables.get(i);
			if (placeableInterface.positionType == 0) resize(placeableInterface);
		}
	}

	@Override
	public void keyTyped(char par1, int par2) {
		if (lastSelected != null) {
			if (par2 == Keyboard.KEY_UP) lastSelected.y--;
			if (par2 == Keyboard.KEY_DOWN) lastSelected.y++;
			if (par2 == Keyboard.KEY_RIGHT) lastSelected.x++;
			if (par2 == Keyboard.KEY_LEFT) lastSelected.x--;
		}
		if (par2 == 1) {
			this.mc.displayGuiScreen(parent);
		}
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (b == 0) {
			if (selectedUIPlaceable == null) {
				for (int i = 0; i < placeables.size(); i++) {
					final GuiPlaceableInterface placeable = placeables.get(i);
					if (inBounds(x, y, placeable.x, placeable.y, placeable.width, placeable.height)) {
						this.offsetX = (int) Math.abs(x - placeable.x);
						this.offsetY = (int) Math.abs(y - placeable.y);
						this.selectedUIPlaceable = placeable;
						this.lastSelected = selectedUIPlaceable;
					}
				}
			} else selectedUIPlaceable = null;
		}
		super.mouseClicked(x, y, b);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		save();
	}

	private void resize(GuiPlaceableInterface placeable) {
		placeable.update((int) (width * ((placeable.x * 1.0F) / placeable.screenWidth)), (int) (height * ((placeable.y * 1.0F) / placeable.screenHeight)), width, height);
	}

	public void save() {
		final Settings settings = VoiceChat.getProxyInstance().getSettings();
		for (int i = 0; i < placeables.size(); i++) {
			final GuiPlaceableInterface placeable = placeables.get(i);
			if (placeable.positionType == 0) {
				placeable.positionUI.x = ((placeable.x * 1.0F) / placeable.screenWidth);
				placeable.positionUI.y = ((placeable.y * 1.0F) / placeable.screenHeight);
			} else {
				placeable.positionUI.x = placeable.x;
				placeable.positionUI.y = placeable.y;
			}
			placeable.positionUI.type = placeable.positionType;
			placeable.positionUI.scale = placeable.scale;
		}
		settings.getConfiguration().save();
	}

	@Override
	public void updateScreen() {
	}
}
