package net.gliby.voicechat.client.gui.options;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.gui.GuiBoostSlider;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenVoiceChatOptionsAdvanced extends GuiScreen {

	private final VoiceChatClient voiceChat;

	private GuiButton encodingMode, enhancedDecoding, serverConnection, volumeControlButton;
	private GuiBoostSlider qualitySlider;
	private final GuiScreen parent;

	public GuiScreenVoiceChatOptionsAdvanced(VoiceChatClient voiceChat, GuiScreen parent) {
		this.voiceChat = voiceChat;
		this.parent = parent;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			voiceChat.getSettings().getConfiguration().save();
			mc.displayGuiScreen(parent);
			break;
		case 1:
			resetAdvancedOptions();
			break;
		case 5:
			int mode = voiceChat.getSettings().getEncodingMode();
			if (mode < 2) mode++;
			else mode = 0;
			voiceChat.getSettings().setEncodingMode(mode);
			encodingMode.displayString = I18n.format("menu.encodingMode") + ": " + voiceChat.getSettings().getEncodingModeString();
			break;
		case 6:
			voiceChat.getSettings().setPerceptualEnchantment(!voiceChat.getSettings().isPerceptualEnchantmentAllowed());
			enhancedDecoding.displayString = I18n.format("menu.enhancedDecoding") + ": " + (voiceChat.getSettings().isPerceptualEnchantmentAllowed() ? I18n.format("options.on") : I18n.format("options.off"));
			break;
		case 7:
			voiceChat.getSettings().setSnooperAllowed(false);
			serverConnection.displayString = I18n.format("menu.allowSnooper") + ": " + (voiceChat.getSettings().isSnooperAllowed() ? I18n.format("options.on") : I18n.format("options.off"));
			break;
		case 8:
			voiceChat.getSettings().setVolumeControl(!voiceChat.getSettings().isVolumeControlled());
			volumeControlButton.displayString = I18n.format("menu.volumeControl") + ": " + (voiceChat.getSettings().isVolumeControlled() ? I18n.format("options.on") : I18n.format("options.off"));
			voiceChat.getSoundManager().volumeControlStop();
			break;
		}
	}

	@Override
	public void drawScreen(int x, int y, float time) {
		drawDefaultBackground();
		glPushMatrix();
		glTranslatef(width / 2 - (fontRendererObj.getStringWidth("Gliby's Voice Chat Options") / 2) * 1.5f, 0, 0);
		glScalef(1.5f, 1.5f, 0);
		drawString(mc.fontRenderer, "Gliby's Voice Chat Options", 0, 6, -1);
		glPopMatrix();
		glPushMatrix();
		glTranslatef(width / 2 - (fontRendererObj.getStringWidth(I18n.format("menu.advancedOptions")) / 2), 12, 0);
		drawString(mc.fontRenderer, I18n.format("menu.advancedOptions"), 0, 12, -1);
		glPopMatrix();
		if ((int) (voiceChat.getSettings().getEncodingQuality() * 10) <= 2) {
			drawCenteredString(mc.fontRenderer, I18n.format("menu.encodingMessage"), width / 2, height - 50, -255);
		}
		super.drawScreen(x, y, time);
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, (this.width / 2) - 75, height - 34, 150, 20, I18n.format("gui.back")));
		buttonList.add(new GuiButton(1, (this.width / 2) + 77, height - 34, 75, 20, I18n.format("controls.reset")));
		qualitySlider = new GuiBoostSlider(4, this.width / 2 + 2, 74, "", I18n.format("menu.encodingQuality") + ": " + (voiceChat.getSettings().getEncodingQuality() == 0 ? "0" : String.valueOf((int) (voiceChat.getSettings().getEncodingQuality() * 10))), 0);
		qualitySlider.sliderValue = voiceChat.getSettings().getEncodingQuality();
		encodingMode = new GuiButton(5, (this.width / 2) - 152, 98, 150, 20, I18n.format("menu.encodingMode") + ": " + voiceChat.getSettings().getEncodingModeString());
		buttonList.add(enhancedDecoding = new GuiButton(6, (this.width / 2) - 152, 50, 150, 20, I18n.format("menu.enhancedDecoding") + ": " + (voiceChat.getSettings().isPerceptualEnchantmentAllowed() ? I18n.format("options.on") : I18n.format("options.off"))));
		buttonList.add(serverConnection = new GuiButton(7, (this.width / 2) + 2, 50, 150, 20, I18n.format("menu.allowSnooper") + ": " + (voiceChat.getSettings().isSnooperAllowed() ? I18n.format("options.on") : I18n.format("options.off"))));
		buttonList.add(volumeControlButton = new GuiButton(8, (this.width / 2) - 152, 74, 150, 20, I18n.format("menu.volumeControl") + ": " + (voiceChat.getSettings().isVolumeControlled() ? I18n.format("options.on") : I18n.format("options.off"))));
		buttonList.add(qualitySlider);
		buttonList.add(encodingMode);
		serverConnection.enabled = false;
		encodingMode.enabled = false;
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		voiceChat.getSettings().getConfiguration().save();
	}

	// TO BE REPLACED - when settings get overhauled.
	public void resetAdvancedOptions() {
		qualitySlider.sliderValue = 0.6f;
		voiceChat.getSettings().setEncodingQuality(qualitySlider.sliderValue);
		qualitySlider.idValue = I18n.format("menu.encodingQuality") + ": " + (voiceChat.getSettings().getEncodingQuality() == 0 ? "0" : String.valueOf((int) (voiceChat.getSettings().getEncodingQuality() * 10)));
		qualitySlider.displayString = qualitySlider.idValue;
		voiceChat.getSettings().setEncodingMode(1);
		encodingMode.displayString = I18n.format("menu.encodingMode") + ": " + voiceChat.getSettings().getEncodingModeString();
		voiceChat.getSettings().setPerceptualEnchantment(true);
		enhancedDecoding.displayString = I18n.format("menu.enhancedDecoding") + ": " + (voiceChat.getSettings().isPerceptualEnchantmentAllowed() ? I18n.format("options.on") : I18n.format("options.off"));
		voiceChat.getSettings().setSnooperAllowed(false);
		serverConnection.displayString = I18n.format("menu.allowSnooper") + ": " + (voiceChat.getSettings().isSnooperAllowed() ? I18n.format("options.on") : I18n.format("options.off"));
		voiceChat.getSettings().setVolumeControl(true);
		volumeControlButton.displayString = I18n.format("menu.volumeControl") + ": " + (voiceChat.getSettings().isVolumeControlled() ? I18n.format("options.on") : I18n.format("options.off"));
	}

	@Override
	public void updateScreen() {
		voiceChat.getSettings().setEncodingQuality(qualitySlider.sliderValue);
		qualitySlider.setDisplayString(I18n.format("menu.encodingQuality") + ": " + (voiceChat.getSettings().getEncodingQuality() == 0 ? "0" : String.valueOf((int) (voiceChat.getSettings().getEncodingQuality() * 10))));
	}
}
