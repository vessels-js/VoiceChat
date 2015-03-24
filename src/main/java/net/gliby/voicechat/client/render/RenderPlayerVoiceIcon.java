package net.gliby.voicechat.client.render;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.PlayableStream;
import net.gliby.voicechat.client.textures.IndependentGUITexture;
import net.gliby.voicechat.common.MathUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderPlayerVoiceIcon extends Gui {

	private VoiceChatClient voiceChat;
	private Minecraft mc;

	public RenderPlayerVoiceIcon(VoiceChatClient voiceChat, Minecraft mc) {
		this.voiceChat = voiceChat;
		this.mc = mc;
	}

	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (!voiceChat.getSoundManager().currentStreams.isEmpty() && voiceChat.getSettings().isVoiceIconAllowed()) {
			glDisable(GL11.GL_LIGHTING);
			glDisable(GL11.GL_DEPTH_TEST);
			glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			translateWorld(mc, event.partialTicks);
			for (int i = 0; i < MathUtility.clamp(voiceChat.getSoundManager().currentStreams.size(), 0, voiceChat.getSettings().getMaximumRenderableVoiceIcons()); i++) {
				PlayableStream stream = voiceChat.getSoundManager().currentStreams.get(i);
				if (stream.player.getPlayer() != null && stream.player.usesEntity) {
					EntityLivingBase entity = (EntityLivingBase) stream.player.getPlayer();
					if (!entity.isInvisible() && !mc.gameSettings.hideGUI) {
						applyLighting(entity, event.partialTicks);
						glPushMatrix();
						glNormal3f(0.0F, 1.0F, 0.0F);
						glDepthMask(false);
						translateEntity(entity, event.partialTicks);
						glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
						glTranslatef(-0.25f, entity.height + 0.7f, 0);
						glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
						glScalef(0.015f, 0.015f, 1.0f);
						IndependentGUITexture.TEXTURES.bindTexture(mc);
						glEnable(GL11.GL_TEXTURE_2D);
						glColor4f(1.0F, 1.0F, 1.0F, 0.25F);
						if (!entity.isSneaking()) renderIcon();
						glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						glEnable(GL11.GL_DEPTH_TEST);
						glDepthMask(true);
						renderIcon();
						IndependentGUITexture.bindPlayer(mc, entity);
						glTranslatef(20, 30, 0);
						glScalef(-1, -1, -1);
						glScalef(0.64f * 0.75f, 0.32f * 0.75f, 0.0f);
						drawTexturedModalRect(0, 0, 32, 64, 32, 64);
						glPopMatrix();
					}
				}
			}
			glEnable(GL11.GL_LIGHTING);
			glDisable(GL11.GL_BLEND);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}

	private void applyLighting(Entity entity, float partialTicks) {
		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		float f1 = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
		int i1 = entity.getBrightnessForRender(partialTicks);
		if (entity.isBurning()) {
			i1 = 15728880;
		}
		int j = i1 % 65536;
		int k = i1 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	private void renderIcon() {
		drawTexturedModalRect(0, 0, 0, 0, 54, 46);
		switch ((int) ((Minecraft.getSystemTime() % 1000L) / 350.0F)) {
			case 0:
				drawTexturedModalRect(12, -3, 0, 47, 22, 49);
				break;
			case 1:
				drawTexturedModalRect(23 + 8, -3, 23, 47, 14, 49);
				break;
			case 2:
				drawTexturedModalRect(38 + 2, -3, 38, 47, 16, 49);
				break;
		}
	}

	public void translateEntity(Entity entity, float tick) {
		glTranslated(entity.prevPosX + (entity.posX - entity.prevPosX) * (float) tick, entity.prevPosY + (entity.posY - (float) entity.prevPosY) * tick, (float) entity.prevPosZ + (entity.posZ - (float) entity.prevPosZ) * tick);
	}

	public void translateWorld(Minecraft mc, float tick) {
		glTranslated(-(mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * tick), -(mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * tick), -(mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * tick));
	}
}
