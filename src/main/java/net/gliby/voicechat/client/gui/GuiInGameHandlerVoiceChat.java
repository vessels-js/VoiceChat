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
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.debug.Statistics;
import net.gliby.voicechat.client.gui.options.GuiScreenOptionsWizard;
import net.gliby.voicechat.client.sound.ClientStream;
import net.gliby.voicechat.client.textures.IndependentGUITexture;
import net.gliby.voicechat.common.MathUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class GuiInGameHandlerVoiceChat extends Gui {

	private long lastFrame;
	private long lastFPS;
	private float fade = 0;

	private final VoiceChatClient voiceChat;
	private ScaledResolution res;
	private Vector2f position;
	private final Minecraft mc;

	private UIPosition positionUI;

	public GuiInGameHandlerVoiceChat(VoiceChatClient voiceChat) {
		this.voiceChat = voiceChat;
		mc = Minecraft.getMinecraft();
	}

	public void calcDelta() {
		if (getTime() - lastFPS > 1000) {
			lastFPS += 1000;
		}
	}

	public int getDelta() {
		final long time = getTime();
		final int delta = (int) (time - lastFrame);
		lastFrame = time;
		return delta;
	}

	private Vector2f getPosition(int width, int height, UIPosition uiPositionSpeak) {
		return uiPositionSpeak.type == 0 ? new Vector2f(uiPositionSpeak.x * width, uiPositionSpeak.y * height) : new Vector2f(uiPositionSpeak.x, uiPositionSpeak.y);
	}

	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}


	@SubscribeEvent
	public void render(RenderGameOverlayEvent.Text text) {
		if (text.type == ElementType.DEBUG) {
			if (VoiceChat.getProxyInstance().getSettings().isDebug()) {
				final Statistics stats = voiceChat.getStatistics();
				if (stats != null) {
					final int settings = ValueFormat.COMMAS | ValueFormat.PRECISION(2) | ValueFormat.BILLIONS;
					final String encodedAvg = ValueFormat.format(stats.getEncodedAverageDataReceived(), settings);
					final String decodedAvg = ValueFormat.format(stats.getDecodedAverageDataReceived(), settings);
					final String encodedData = ValueFormat.format(stats.getEncodedDataReceived(), settings);
					final String decodedData = ValueFormat.format(stats.getDecodedDataReceived(), settings);
					text.right.add("Voice Chat Debug Info");
					text.right.add("VC Data [ENC AVG]: " + encodedAvg + "");
					text.right.add("VC Data [DEC AVG]: " + decodedAvg + "");
					text.right.add("VC Data [ENC REC]: " + encodedData + "");
					text.right.add("VC Data [DEC REC]: " + decodedData + "");
				}
			}
		}
	}

	@SubscribeEvent
	public void renderInGameGui(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.HOTBAR) {
			if (res == null) {
				getDelta();
				lastFPS = getTime();
			}
			res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

			final int width = res.getScaledWidth();
			final int height = res.getScaledHeight();
			final int delta = getDelta();
			calcDelta();
			if (!VoiceChat.getProxyInstance().isRecorderActive()) if (fade > 0) fade -= 0.01f * delta;
			else fade = 0;
			else if (fade < 1.0f && VoiceChat.getProxyInstance().isRecorderActive()) fade += 0.01f * delta;
			else fade = 1.0f;

			if (fade != 0) {
				positionUI = voiceChat.getSettings().getUIPositionSpeak();
				position = getPosition(width, height, positionUI);
				if (positionUI.scale != 0) {
					glPushMatrix();
					glEnable(GL11.GL_BLEND);
					glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					glColor4f(1.0F, 1.0F, 1.0F, fade * voiceChat.getSettings().getUIOpacity());
					IndependentGUITexture.TEXTURES.bindTexture(mc);
					glTranslatef(position.x + positionUI.info.offsetX, position.y + positionUI.info.offsetY, 0);
					glScalef(positionUI.scale, positionUI.scale, 1.0f);
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
					mc.getTextureManager().bindTexture(mc.thePlayer.getLocationSkin());
					glScalef(0.6f, 0.3f, 0.0f);
					glTranslatef(0, 47, 0);
					drawTexturedModalRect(0, 0, 32, 64, 32, 64);
					glDisable(GL11.GL_BLEND);
					glPopMatrix();
				}
			}

			if (!VoiceChatClient.getSoundManager().currentStreams.isEmpty() && voiceChat.getSettings().isVoicePlateAllowed()) {
				float scale = 0;
				positionUI = voiceChat.getSettings().getUIPositionPlate();
				position = getPosition(width, height, positionUI);
				glEnable(GL11.GL_BLEND);
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				for (int i = 0; i < VoiceChatClient.getSoundManager().currentStreams.size(); i++) {
					final ClientStream stream = VoiceChatClient.getSoundManager().currentStreams.get(i);
					if (stream != null) {
						final String s = stream.player.entityName();
						final boolean playerExists = stream.player.getPlayer() != null;
						final int length = mc.fontRenderer.getStringWidth(s);
						scale = 0.75f * positionUI.scale;
						glPushMatrix();
						glTranslatef(position.x + positionUI.info.offsetX, position.y + positionUI.info.offsetY + ((i * 23) * scale), 0);
						glScalef(scale, scale, 0.0f);
						glColor4f(1.0F, 1.0F, 1.0F, voiceChat.getSettings().getUIOpacity());
						glTranslatef(0, 0, 0);
						IndependentGUITexture.TEXTURES.bindTexture(mc);
						drawTexturedModalRect(0, 0, 56, stream.special * 22, 109, 22);
						glPushMatrix();
						scale = MathUtility.clamp(50.5F / length, 0, 1.25f);
						glTranslatef(25 + (scale / 2), 11.0f - (((mc.fontRenderer.FONT_HEIGHT - 1) * scale) / 2), 0);
						glScalef(scale, scale, 0);
						drawString(mc.fontRenderer, s, 0, 0, -1);
						glPopMatrix();
						glPushMatrix();
						glTranslatef(3f, 3, 0);
						glScalef(0.64f * 0.75f, 0.32f * 0.75f, 0.0f);
						if (playerExists) IndependentGUITexture.bindPlayer(mc, stream.player.getPlayer());
						else IndependentGUITexture.bindDefaultPlayer(mc);
						glColor4f(1.0F, 1.0F, 1.0F, voiceChat.getSettings().getUIOpacity());
						drawTexturedModalRect(0, 0, 32, 64, 32, 64);
						drawTexturedModalRect(0, 0, 160, 64, 32, 64);
						glPopMatrix();
						glPopMatrix();
					}
				}
				glDisable(GL11.GL_BLEND);
			}

			if (VoiceChatClient.getSoundManager().currentStreams.isEmpty()) {
				VoiceChatClient.getSoundManager().volumeControlStop();
			} else {
				if (voiceChat.getSettings().isVolumeControlled()) {
					VoiceChatClient.getSoundManager().volumeControlStart();
				}
			}
		}
	}

}
