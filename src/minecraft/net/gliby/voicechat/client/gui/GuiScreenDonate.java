package net.gliby.voicechat.client.gui;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.awt.Dimension;
import java.net.URI;

import net.gliby.gman.ModInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StringTranslate;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.TextureFXManager;
import cpw.mods.fml.common.ModMetadata;

public class GuiScreenDonate extends GuiScreen {

	private final GuiScreen parent;
	private final ModInfo info;
	private final ModMetadata modMetadata;


	private Dimension cachedLogoDimensions;

	public GuiScreenDonate(ModInfo info, ModMetadata modMetadata, GuiScreen parent) {
		this.parent = parent;
		this.info = info;
		this.modMetadata = modMetadata;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			mc.displayGuiScreen(parent);
			break;
		case 1:
			openURL(info.donateURL);
			break;
		}
	}

	@Override
	public void drawScreen(int x, int y, float tick) {
		renderModLogo(info.modId, modMetadata, false);
		drawBackground(0);
		glPushMatrix();
		glTranslatef(width / 2 - (cachedLogoDimensions.width / 2), height / 2 - (cachedLogoDimensions.height + 95), 0);
		renderModLogo(info.modId, modMetadata, true);
		glPopMatrix();
		final String s = StringTranslate.getInstance().translateKey("menu.gman.supportGliby.description");
		fontRenderer.drawSplitString(s, width / 2 - 150, height / 2 - 50, 300, -1);
		final String s1 = StringTranslate.getInstance().translateKey("menu.gman.supportGliby.contact");
		fontRenderer.drawSplitString(s1, width / 2 - 150, height / 2 + 35, 300, -1);
		super.drawScreen(x, y, tick);
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, (this.width / 2) - 50, height - 34, 100, 20, StringTranslate.getInstance().translateKey("gui.back")));
		buttonList.add(new GuiButton(1, (this.width / 2) - 100, height / 2, StringTranslate.getInstance().translateKey("menu.gman.donate")));
	}
	private void openURL(String par1URI) {
		try {
			final Class oclass = Class.forName("java.awt.Desktop");
			final Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
			oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { new URI(par1URI) });
		} catch (final Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private void renderModLogo(String modId, ModMetadata modMetadata, boolean draw) {
		final String logoFile = modMetadata.logoFile;
		if (!logoFile.isEmpty())
		{
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.mc.renderEngine.bindTexture(logoFile);
			cachedLogoDimensions = TextureFXManager.instance().getTextureDimensions(logoFile);
			double scaleX = cachedLogoDimensions.width / 200.0;
			double scaleY = cachedLogoDimensions.height / 65.0;
			double scale = 1.0;
			if (scaleX > 1 || scaleY > 1)
			{
				scale = 1.0 / Math.max(scaleX, scaleY);
			}
			cachedLogoDimensions.width *= scale;
			cachedLogoDimensions.height *= scale;
			int top = 32;
			int offset =0 ;
			Tessellator tess = Tessellator.instance;
			tess.startDrawingQuads();
			tess.addVertexWithUV(offset,             top + cachedLogoDimensions.height, zLevel, 0, 1);
			tess.addVertexWithUV(offset + cachedLogoDimensions.width, top + cachedLogoDimensions.height, zLevel, 1, 1);
			tess.addVertexWithUV(offset + cachedLogoDimensions.width, top,              zLevel, 1, 0);
			tess.addVertexWithUV(offset,             top,              zLevel, 0, 0);
			tess.draw();
		}
	}
}
