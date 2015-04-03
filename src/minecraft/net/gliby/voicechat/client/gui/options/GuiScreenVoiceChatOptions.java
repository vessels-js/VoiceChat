package net.gliby.voicechat.client.gui.options;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.device.Device;
import net.gliby.voicechat.client.gui.GuiBoostSlider;
import net.gliby.voicechat.client.gui.GuiCustomButton;
import net.gliby.voicechat.client.gui.GuiDropDownMenu;
import net.gliby.voicechat.client.gui.GuiScreenDonate;
import net.gliby.voicechat.client.gui.GuiScreenLocalMute;
import net.gliby.voicechat.client.sound.MicrophoneTester;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringTranslate;

import org.lwjgl.opengl.GL11;

public class GuiScreenVoiceChatOptions extends GuiScreen {

	private final VoiceChatClient voiceChat;
	private final MicrophoneTester tester;
	private GuiCustomButton advancedOptions, mutePlayer;

	private GuiBoostSlider boostSlider, voiceVolume;
	private GuiDropDownMenu dropDown;
	private GuiButton UIPosition, microphoneMode;
	private List<String> warningMessages;
	private String updateMessage;

	public GuiScreenVoiceChatOptions(VoiceChatClient voiceChat) {
		this.voiceChat = voiceChat;
		tester = new MicrophoneTester(voiceChat);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 899:
			if (!dropDown.dropDownMenu) mc.displayGuiScreen(new GuiScreenVoiceChatOptionsAdvanced(voiceChat, this));
			break;
		case 898:
			if (!dropDown.dropDownMenu) mc.displayGuiScreen(new GuiScreenOptionsWizard(voiceChat, this));
			break;
		case 0:
			if (button instanceof GuiDropDownMenu && !voiceChat.getSettings().getDeviceHandler().isEmpty()) ((GuiDropDownMenu) button).dropDownMenu = !((GuiDropDownMenu) button).dropDownMenu;
			break;
		case 1:
			voiceChat.getSettings().getConfiguration().save();
			mc.displayGuiScreen(new GuiScreenDonate(voiceChat.getModInfo(), VoiceChatClient.getModMetadata(), this));
			break;
		case 2:
			if (!tester.recording) tester.start();
			else tester.stop();
			button.displayString = tester.recording ? StringTranslate.getInstance().translateKey("menu.microphoneStopTest") : StringTranslate.getInstance().translateKey("menu.microphoneTest");
			break;
		case 3:
			voiceChat.getSettings().getConfiguration().save();
			mc.displayGuiScreen(null);
			break;
		case 4:
			mc.displayGuiScreen(new GuiScreenOptionsUI(voiceChat, this));
			break;
		case 897:
			if (!dropDown.dropDownMenu) mc.displayGuiScreen(new GuiScreenLocalMute(this, voiceChat));

			break;
		case 5:
			if (!dropDown.dropDownMenu) {
				microphoneMode.drawButton = true;
				microphoneMode.enabled = true;
				voiceChat.getSettings().setSpeakMode(voiceChat.getSettings().getSpeakMode() == 0 ? 1 : 0);
				microphoneMode.displayString = StringTranslate.getInstance().translateKey("menu.speakMode") + ": " + (voiceChat.getSettings().getSpeakMode() == 0 ? StringTranslate.getInstance().translateKey("menu.speakModePushToTalk") : StringTranslate.getInstance().translateKey("menu.speakModeToggleToTalk"));
			} else if (voiceChat.getSettings().getDeviceHandler().isEmpty()) {
				microphoneMode.drawButton = false;
				microphoneMode.enabled = false;
			}
			break;
		}
	}

	@Override
	public void drawScreen(int x, int y, float tick) {
		drawDefaultBackground();
		GL11.glPushMatrix();
		final float scale = 1.5f;
		GL11.glTranslatef(width / 2 - (fontRenderer.getStringWidth("Gliby's Voice Chat Options") / 2) * scale, 0, 0);
		GL11.glScalef(scale, scale, 0);
		drawString(fontRenderer, "Gliby's Voice Chat Options", 0, 6, -1);
		GL11.glPopMatrix();
		for (int i = 0; i < warningMessages.size(); i++) {
			final int warnY = i * fontRenderer.FONT_HEIGHT + height / 2 + 66 - ((fontRenderer.FONT_HEIGHT * warningMessages.size()) / 2);
			drawCenteredString(fontRenderer, warningMessages.get(i), width / 2, warnY, -1);
		}
		super.drawScreen(x, y, tick);
	}

	public boolean inBounds(int x, int y, int posX, int posY, int width, int height) {
		return x >= posX && y >= posY && x < posX + width && y < posY + height;
	}

	@Override
	public void initGui() {
		final String[] array = new String[voiceChat.getSettings().getDeviceHandler().getDevices().size()];
		for (int i = 0; i < voiceChat.getSettings().getDeviceHandler().getDevices().size(); i++) {
			array[i] = voiceChat.getSettings().getDeviceHandler().getDevices().get(i).getName();
		}
		final int heightOffset = 55;
		dropDown = new GuiDropDownMenu(0, this.width / 2 - 152, (height / 2) - heightOffset, 150, 20, voiceChat.getSettings().getInputDevice() != null ? voiceChat.getSettings().getInputDevice().getName() : "None", array);
		microphoneMode = new GuiButton(5, this.width / 2 - 152, (height / 2 + 25) - heightOffset, 150, 20, StringTranslate.getInstance().translateKey("menu.speakMode") + ": " + (voiceChat.getSettings().getSpeakMode() == 0 ? StringTranslate.getInstance().translateKey("menu.speakModePushToTalk") : StringTranslate.getInstance().translateKey("menu.speakModeToggleToTalk")));
		UIPosition = new GuiButton(4, this.width / 2 + 2, (height / 2 + 25) - heightOffset, 150, 20, StringTranslate.getInstance().translateKey("menu.uiOptions"));
		voiceVolume = new GuiBoostSlider(910, this.width / 2 + 2, (height / 2 - 25) - heightOffset, "", StringTranslate.getInstance().translateKey("menu.worldVolume") + ": " + (voiceChat.getSettings().getWorldVolume() == 0 ? StringTranslate.getInstance().translateKey("options.off") : String.valueOf((int) (voiceChat.getSettings().getWorldVolume() * 100)) + "%"), 0);
		voiceVolume.sliderValue = voiceChat.getSettings().getWorldVolume();
		boostSlider = new GuiBoostSlider(900, this.width / 2 + 2, (height / 2) - heightOffset, "", StringTranslate.getInstance().translateKey("menu.boost") + ": " + ((int) (voiceChat.getSettings().getInputBoost() * 5f) <= 0 ? StringTranslate.getInstance().translateKey("options.off") : "" + (int) (voiceChat.getSettings().getInputBoost() * 5) + "db"), 0);
		boostSlider.sliderValue = voiceChat.getSettings().getInputBoost();
		advancedOptions = new GuiCustomButton(899, this.width / 2 + 2, (height / 2 + 49) - heightOffset, 150, 20, StringTranslate.getInstance().translateKey("menu.advancedOptions"));
		buttonList.add(new GuiButton(1, (this.width / 2) - 151, height - 34, 75, 20, StringTranslate.getInstance().translateKey("menu.gman.supportGliby")));
		buttonList.add(new GuiButton(2, width / 2 - 152, (height / 2 - 25) - heightOffset, 150, 20, !tester.recording ? StringTranslate.getInstance().translateKey("menu.microphoneTest") : StringTranslate.getInstance().translateKey("menu.microphoneStopTest")));
		buttonList.add(new GuiButton(3, width / 2 - 75, height - 34, 150, 20, StringTranslate.getInstance().translateKey("menu.returnToGame")));
		buttonList.add(advancedOptions);
		buttonList.add(new GuiCustomButton(898, width / 2 - 152, (height / 2 + 49) - heightOffset, 150, 20, StringTranslate.getInstance().translateKey("menu.openOptionsWizard")));
		buttonList.add(UIPosition);
		buttonList.add(microphoneMode);
		buttonList.add(boostSlider);
		buttonList.add(voiceVolume);
		buttonList.add(mutePlayer = new GuiCustomButton(897, width / 2 - 152, (height / 2 + 73) - heightOffset, 304, 20, StringTranslate.getInstance().translateKey("menu.mutePlayers")));
		buttonList.add(dropDown);
		if (voiceChat.getSettings().getDeviceHandler().isEmpty()) {
			((GuiButton) buttonList.get(0)).enabled = false;
			((GuiButton) buttonList.get(3)).enabled = false;
			boostSlider.enabled = false;
			mutePlayer.enabled = false;
			microphoneMode.enabled = false;
			mutePlayer.enabled = false;
		}
		super.initGui();

		warningMessages = new ArrayList<String>();
		if (voiceChat.getSettings().getDeviceHandler().isEmpty()) warningMessages.add(EnumChatFormatting.DARK_RED + "No input devices found, add input device and restart Minecraft.");

		if (voiceChat.getModInfo().updateNeeded()) {
			warningMessages.add(updateMessage = StringTranslate.getInstance().translateKey("menu.downloadLatest") + "\247b " + voiceChat.getModInfo().updateURL);
			warningMessages.add(EnumChatFormatting.RED + StringTranslate.getInstance().translateKey("menu.modOutdated"));
		}

		if (mc.isSingleplayer() && !mc.getIntegratedServer().getPublic()) warningMessages.add(EnumChatFormatting.RED + StringTranslate.getInstance().translateKey("menu.warningSingleplayer"));

		if (!voiceChat.getClientNetwork().isConnected() && !mc.isSingleplayer()) {
			warningMessages.add(EnumChatFormatting.RED + StringTranslate.getInstance().translateKey("Server doesn't support voice chat."));
		}
	}

	@Override
	public void keyTyped(char c, int key) {
		if (key == 1) {
			voiceChat.getSettings().getConfiguration().save();
			this.mc.displayGuiScreen((GuiScreen) null);
			this.mc.setIngameFocus();
		}
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (b == 0) {
			if (voiceChat.getModInfo().updateNeeded() || true) {
				for (int i = 0; i < warningMessages.size(); i++) {
					final String s = warningMessages.get(i);
					if (s.equals(updateMessage)) {
						final int warnY = i * fontRenderer.FONT_HEIGHT + height / 2 + 66 - ((fontRenderer.FONT_HEIGHT * warningMessages.size()) / 2);
						final int length = fontRenderer.getStringWidth(s);
						if (inBounds(x, y, width / 2 - (length / 2), warnY, length, fontRenderer.FONT_HEIGHT)) this.openURL(voiceChat.modInfo.updateURL);
					}
				}
			}
			if (dropDown.getMouseOverInteger() != -1 && dropDown.dropDownMenu && !voiceChat.getSettings().getDeviceHandler().isEmpty()) {
				final Device device = voiceChat.getSettings().getDeviceHandler().getDevices().get(dropDown.getMouseOverInteger());
				if (device == null) return;
				voiceChat.getSettings().setInputDevice(device);
				dropDown.setDisplayString(device.getName());
			}
		}
		super.mouseClicked(x, y, b);
	}

	@Override
	public void onGuiClosed() {
		if (tester.recording) tester.stop();
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

	@Override
	public void updateScreen() {
		voiceChat.getSettings().setWorldVolume(voiceVolume.sliderValue);
		voiceChat.getSettings().setInputBoost(boostSlider.sliderValue);
		voiceVolume.setDisplayString(StringTranslate.getInstance().translateKey("menu.worldVolume") + ": " + (voiceChat.getSettings().getWorldVolume() == 0 ? StringTranslate.getInstance().translateKey("options.off") : +(int) (voiceChat.getSettings().getWorldVolume() * 100) + "%"));
		boostSlider.setDisplayString(StringTranslate.getInstance().translateKey("menu.boost") + ": " + ((int) (voiceChat.getSettings().getInputBoost() * 5f) <= 0 ? StringTranslate.getInstance().translateKey("options.off") : (int) (voiceChat.getSettings().getInputBoost() * 5f) + "db"));
		advancedOptions.allowed = !dropDown.dropDownMenu;
		mutePlayer.allowed = !dropDown.dropDownMenu;
	}
}
