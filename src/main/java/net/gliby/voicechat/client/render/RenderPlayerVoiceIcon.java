package net.gliby.voicechat.client.render;

import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glTranslatef;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.gui.GuiInGameHandlerVoiceChat;
import net.gliby.voicechat.client.sound.ClientStream;
import net.gliby.voicechat.client.textures.IndependentGUITexture;
import net.gliby.voicechat.common.MathUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderPlayerVoiceIcon extends Gui {

	private final VoiceChatClient voiceChat;
	private final Minecraft mc;

	public RenderPlayerVoiceIcon(VoiceChatClient voiceChat, Minecraft mc) {
		this.voiceChat = voiceChat;
		this.mc = mc;
	}

	private void enableEntityLighting(Entity entity, float partialTicks) {
		int i1 = entity.getBrightnessForRender(partialTicks);
		if (entity.isBurning()) {
			i1 = 15728880;
		}
		final int j = i1 % 65536;
		final int k = i1 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

    public void disableEntityLighting() {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
	
	@SubscribeEvent
	public void render(RenderWorldLastEvent event) {
		if (!VoiceChatClient.getSoundManager().currentStreams.isEmpty() && voiceChat.getSettings().isVoiceIconAllowed()) {
			glDisable(GL11.GL_DEPTH_TEST);
			glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			translateWorld(mc, event.partialTicks);
			for (int i = 0; i < MathUtility.clamp(VoiceChatClient.getSoundManager().currentStreams.size(), 0, voiceChat.getSettings().getMaximumRenderableVoiceIcons()); i++) {
				final ClientStream stream = VoiceChatClient.getSoundManager().currentStreams.get(i);
				if (stream.player.getPlayer() != null && stream.player.usesEntity) {
					final EntityLivingBase entity = (EntityLivingBase) stream.player.getPlayer();
					if (!entity.isInvisible() && !mc.gameSettings.hideGUI) {
						glPushMatrix();
						enableEntityLighting(entity, event.partialTicks);
						glNormal3f(0.0F, 1.0F, 0.0F);
						glDepthMask(false);
						translateEntity(entity, event.partialTicks);
						glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
						glTranslatef(-0.25f, entity.height + 0.7f, 0);
						glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
						glScalef(0.015f, 0.015f, 1.0f);
						IndependentGUITexture.TEXTURES.bindTexture(mc);
//						glEnable(GL11.GL_TEXTURE_2D);
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
						disableEntityLighting();
						glPopMatrix();
					}
				}
			}
			glDisable(GL11.GL_BLEND);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
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
		glTranslated(entity.prevPosX + (entity.posX - entity.prevPosX) *  tick, entity.prevPosY + (entity.posY - entity.prevPosY) * tick, entity.prevPosZ + (entity.posZ - entity.prevPosZ) * tick);
	}

	public void translateWorld(Minecraft mc, float tick) {
		glTranslated(-(mc.thePlayer.prevPosX + (mc.thePlayer.posX - mc.thePlayer.prevPosX) * tick), -(mc.thePlayer.prevPosY + (mc.thePlayer.posY - mc.thePlayer.prevPosY) * tick), -(mc.thePlayer.prevPosZ + (mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * tick));
	}
}
