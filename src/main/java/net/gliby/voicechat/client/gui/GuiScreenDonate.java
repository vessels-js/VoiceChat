package net.gliby.voicechat.client.gui;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.imageio.ImageIO;

import net.gliby.gman.ModInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ModMetadata;

public class GuiScreenDonate extends GuiScreen {

	private GuiScreen parent;
	private ModInfo info;
	private ModMetadata modMetadata;

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
		glTranslatef(width / 2 - (cachedLogoDimensions.width / 2), height / 2 - (cachedLogoDimensions.height + 60), 0);
		renderModLogo(info.modId, modMetadata, true);
		glPopMatrix();
		String s = I18n.format("menu.gman.supportGliby.description");
		fontRendererObj.drawSplitString(s, width / 2 - 150, height / 2 - 50, 300, -1);
		String s1 = I18n.format("menu.gman.supportGliby.contact");
		fontRendererObj.drawSplitString(s1, width / 2 - 150, height / 2 + 35, 300, -1);
		super.drawScreen(x, y, tick);
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, (this.width / 2) - 50, height - 34, 100, 20, I18n.format("gui.back")));
		buttonList.add(new GuiButton(1, (this.width / 2) - 100, height / 2, I18n.format("menu.gman.donate")));
	}

	private void openURL(String par1URI) {
		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
			oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { new URI(par1URI) });
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private ResourceLocation cachedLogo;
	private Dimension cachedLogoDimensions;

	private void renderModLogo(String modId, ModMetadata modMetadata, boolean draw) {
		String logoFile = modMetadata.logoFile;
		if (!logoFile.isEmpty()) {
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			TextureManager tm = mc.getTextureManager();
			IResourcePack pack = FMLClientHandler.instance().getResourcePackFor(modId);
			try {
				if (cachedLogo == null) {
					BufferedImage logo = null;
					if (pack != null) {
						logo = pack.getPackImage();
					} else {
						InputStream logoResource = getClass().getResourceAsStream(logoFile);
						if (logoResource != null) {
							logo = ImageIO.read(logoResource);
						}
					}
					if (logo != null) {
						cachedLogo = tm.getDynamicTextureLocation("modlogo", new DynamicTexture(logo));
						cachedLogoDimensions = new Dimension(logo.getWidth(), logo.getHeight());
					}
				}
				if (cachedLogo != null && draw) {
					this.mc.renderEngine.bindTexture(cachedLogo);
					double scaleX = cachedLogoDimensions.width / 200.0;
					double scaleY = cachedLogoDimensions.height / 65.0;
					double scale = 1.0;
					if (scaleX > 1 || scaleY > 1) {
						scale = 1.0 / Math.max(scaleX, scaleY);
					}
					cachedLogoDimensions.width *= scale;
					cachedLogoDimensions.height *= scale;
					Tessellator tess = Tessellator.instance;
					tess.startDrawingQuads();
					tess.addVertexWithUV(0, cachedLogoDimensions.height, zLevel, 0, 1);
					tess.addVertexWithUV(0 + cachedLogoDimensions.width, 0 + cachedLogoDimensions.height, zLevel, 1, 1);
					tess.addVertexWithUV(0 + cachedLogoDimensions.width, 0, zLevel, 1, 0);
					tess.addVertexWithUV(0, 0, zLevel, 0, 0);
					tess.draw();
				}
			} catch (IOException e) {}
		}
	}
}
