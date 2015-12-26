package net.gliby.voicechat.client.gui;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.util.Random;

import net.gliby.voicechat.client.textures.IndependentGUITexture;
import net.gliby.voicechat.common.MathUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiUIPlacementVoicePlate extends GuiPlaceableInterface {

	private static final ResourceLocation field_110826_a = new ResourceLocation("textures/entity/steve.png");
	String[] players;

	public GuiUIPlacementVoicePlate(UIPosition position, int width, int height) {
		super(position, width, height);
		this.width = 70;
		this.height = 55;
		players = new String[] { "krisis78", "theGliby", "3kliksphilip" };
		shuffleArray(players);
	}

	@Override
	public void draw(Minecraft mc, GuiScreen gui, int x, int y, float tick) {
		for (int i = 0; i < players.length; i++) {
			final String stream = players[i];
			final int length = mc.fontRenderer.getStringWidth(stream);
			float scale = 0.75f * this.scale;
			glPushMatrix();
			glTranslatef(positionUI.x + positionUI.info.offsetX, positionUI.y + positionUI.info.offsetY + ((i * 23) * scale), 0);
			glScalef(scale, scale, 0.0f);
			glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0f);
			glTranslatef(0, 0, 0);
			IndependentGUITexture.TEXTURES.bindTexture(mc);
			gui.drawTexturedModalRect(0, 0, 56, 0, 109, 22);
			glPushMatrix();
			scale = MathUtility.clamp(50.5F / length, 0, 1.25f);
			glTranslatef(25 + (scale / 2), 11.0f - (((mc.fontRenderer.FONT_HEIGHT - 1) * scale) / 2), 0);
			glScalef(scale, scale, 0);
			gui.drawString(mc.fontRenderer, stream, 0, 0, -1);
			glPopMatrix();
			glPushMatrix();
			glTranslatef(3f, 3, 0);
			glScalef(0.64f * 0.75f, 0.32f * 0.75f, 0.0f);
			mc.getTextureManager().bindTexture(field_110826_a);
			gui.drawTexturedModalRect(0, 0, 32, 64, 32, 64);
			glPopMatrix();
			glDisable(GL11.GL_BLEND);
			glPopMatrix();

		}
	}

	void shuffleArray(String[] ar) {
		final Random rnd = new Random();
		for (int i = ar.length - 1; i > 0; i--) {
			final int index = rnd.nextInt(i + 1);
			final String a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

}
