package net.gliby.voicechat.client.gui.options;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.gui.GuiBoostSlider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StringTranslate;

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
			encodingMode.displayString = StringTranslate.getInstance().translateKey("menu.encodingMode") + ": " + voiceChat.getSettings().getEncodingModeString();
			break;
		case 6:
			voiceChat.getSettings().setPerceptualEnchantment(!voiceChat.getSettings().isPerceptualEnchantmentAllowed());
			enhancedDecoding.displayString = StringTranslate.getInstance().translateKey("menu.enhancedDecoding") + ": " + (voiceChat.getSettings().isPerceptualEnchantmentAllowed() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"));
			break;
		case 7:
			voiceChat.getSettings().setSnooperAllowed(false);
			serverConnection.displayString = StringTranslate.getInstance().translateKey("menu.allowSnooper") + ": " + (voiceChat.getSettings().isSnooperAllowed() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"));
			break;
		case 8:
			//TODO resets volume
			voiceChat.getSettings().setVolumeControl(!voiceChat.getSettings().isVolumeControlled());
			volumeControlButton.displayString = StringTranslate.getInstance().translateKey("menu.volumeControl") + ": " + (voiceChat.getSettings().isVolumeControlled() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"));
//			if (!voiceChat.getSettings().isVolumeControlled()) mc.getSoundHandler().setSoundLevel(SoundCategory.MASTER, mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
			break;
		}
	}

	@Override
	public void drawScreen(int x, int y, float time) {
		drawDefaultBackground();
		glPushMatrix();
		glTranslatef(width / 2 - (fontRenderer.getStringWidth("Gliby's Voice Chat Options") / 2) * 1.5f, 0, 0);
		glScalef(1.5f, 1.5f, 0);
		drawString(mc.fontRenderer, "Gliby's Voice Chat Options", 0, 6, -1);
		glPopMatrix();
		glPushMatrix();
		glTranslatef(width / 2 - (fontRenderer.getStringWidth(StringTranslate.getInstance().translateKey("menu.advancedOptions")) / 2), 12, 0);
		drawString(mc.fontRenderer, StringTranslate.getInstance().translateKey("menu.advancedOptions"), 0, 12, -1);
		glPopMatrix();
		if ((int) (voiceChat.getSettings().getEncodingQuality() * 10) <= 2) {
			drawCenteredString(mc.fontRenderer, StringTranslate.getInstance().translateKey("menu.encodingMessage"), width / 2, height - 50, -255);
		}
		super.drawScreen(x, y, time);
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, (this.width / 2) - 75, height - 34, 150, 20, StringTranslate.getInstance().translateKey("gui.back")));
		buttonList.add(new GuiButton(1, (this.width / 2) + 77, height - 34, 75, 20, StringTranslate.getInstance().translateKey("menu.resetAll")));
		qualitySlider = new GuiBoostSlider(4, this.width / 2 + 2, 74, "", StringTranslate.getInstance().translateKey("menu.encodingQuality") + ": " + (voiceChat.getSettings().getEncodingQuality() == 0 ? "0" : String.valueOf((int) (voiceChat.getSettings().getEncodingQuality() * 10))), 0);
		qualitySlider.sliderValue = voiceChat.getSettings().getEncodingQuality();
		encodingMode = new GuiButton(5, (this.width / 2) - 152, 98, 150, 20, StringTranslate.getInstance().translateKey("menu.encodingMode") + ": " + voiceChat.getSettings().getEncodingModeString());
		buttonList.add(enhancedDecoding = new GuiButton(6, (this.width / 2) - 152, 50, 150, 20, StringTranslate.getInstance().translateKey("menu.enhancedDecoding") + ": " + (voiceChat.getSettings().isPerceptualEnchantmentAllowed() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"))));
		buttonList.add(serverConnection = new GuiButton(7, (this.width / 2) + 2, 50, 150, 20, StringTranslate.getInstance().translateKey("menu.allowSnooper") + ": " + (voiceChat.getSettings().isSnooperAllowed() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"))));
		buttonList.add(volumeControlButton = new GuiButton(8, (this.width / 2) - 152, 74, 150, 20, StringTranslate.getInstance().translateKey("menu.volumeControl") + ": " + (voiceChat.getSettings().isVolumeControlled() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"))));
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
		qualitySlider.idValue = StringTranslate.getInstance().translateKey("menu.encodingQuality") + ": " + (voiceChat.getSettings().getEncodingQuality() == 0 ? "0" : String.valueOf((int) (voiceChat.getSettings().getEncodingQuality() * 10)));
		qualitySlider.displayString = qualitySlider.idValue;
		voiceChat.getSettings().setEncodingMode(1);
		encodingMode.displayString = StringTranslate.getInstance().translateKey("menu.encodingMode") + ": " + voiceChat.getSettings().getEncodingModeString();
		voiceChat.getSettings().setPerceptualEnchantment(true);
		enhancedDecoding.displayString = StringTranslate.getInstance().translateKey("menu.enhancedDecoding") + ": " + (voiceChat.getSettings().isPerceptualEnchantmentAllowed() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"));
		voiceChat.getSettings().setSnooperAllowed(false);
		serverConnection.displayString = StringTranslate.getInstance().translateKey("menu.allowSnooper") + ": " + (voiceChat.getSettings().isSnooperAllowed() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"));
		voiceChat.getSettings().setVolumeControl(true);
		volumeControlButton.displayString = StringTranslate.getInstance().translateKey("menu.volumeControl") + ": " + (voiceChat.getSettings().isVolumeControlled() ? StringTranslate.getInstance().translateKey("options.on") : StringTranslate.getInstance().translateKey("options.off"));
	}

	@Override
	public void updateScreen() {
		voiceChat.getSettings().setEncodingQuality(qualitySlider.sliderValue);
		qualitySlider.setDisplayString(StringTranslate.getInstance().translateKey("menu.encodingQuality") + ": " + (voiceChat.getSettings().getEncodingQuality() == 0 ? "0" : String.valueOf((int) (voiceChat.getSettings().getEncodingQuality() * 10))));
	}
}
