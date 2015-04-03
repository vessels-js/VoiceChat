package net.gliby.voicechat.client.textures;

import org.lwjgl.opengl.GL11;

import net.gliby.voicechat.VoiceChat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
//TODO Fix textures.
public class IndependentGUITexture {

	public static final IndependentGUITexture TEXTURES = new IndependentGUITexture("gvctextures");
	public static final IndependentGUITexture GUI_WIZARD = new IndependentGUITexture("wizard_gui");
	private static final String steve = "/mob/char.png";

	public static void bindClientPlayer(Minecraft mc) {
		loadDownloadableImageTexture(mc.thePlayer.skinUrl, mc.thePlayer.getTexture());
	}

	public static void bindDefaultPlayer(Minecraft mc) {
		mc.renderEngine.bindTexture(steve);
	}

	public static void bindPlayer(Minecraft mc, Entity entity) {
		loadDownloadableImageTexture(entity.skinUrl, entity.getTexture());
	}

	private static boolean loadDownloadableImageTexture(String par1Str, String par2Str)
	{
		RenderEngine renderengine = RenderManager.instance.renderEngine;
		if(renderengine != null) {
			int i = renderengine.getTextureForDownloadableImage(par1Str, par2Str);
			if (i >= 0) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, i);
				renderengine.resetBoundTexture();
				return true;
			} else return false;
		}
		return false;
	}

	private Object resource;

	public IndependentGUITexture(String texture) {
		resource = "/gvc/textures/gui/" + texture + ".png";
	}

	public void bindTexture(Minecraft mc) {
		mc.renderEngine.bindTexture((String)this.getTexture());
	}

	public Object getTexture() {
		return this.resource;
	}
}
