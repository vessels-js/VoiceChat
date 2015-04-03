package net.gliby.voicechat.client.gui.options;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.device.Device;
import net.gliby.voicechat.client.gui.GuiBoostSlider;
import net.gliby.voicechat.client.gui.GuiCustomButton;
import net.gliby.voicechat.client.gui.GuiDropDownMenu;
import net.gliby.voicechat.client.keybindings.EnumBinding;
import net.gliby.voicechat.client.sound.MicrophoneTester;
import net.gliby.voicechat.client.textures.IndependentGUITexture;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringTranslate;

import org.lwjgl.opengl.GL11;

public class GuiScreenOptionsWizard extends GuiScreen {
	private final VoiceChatClient voiceChat;
	private final GuiScreen parent;

	private boolean dirty;
	private String[] textBatch;
	private GuiDropDownMenu dropDown;
	private final MicrophoneTester tester;

	private GuiCustomButton nextButton, previousButton, doneButton, backButton;

	private GuiBoostSlider boostSlider;
	private final Map<GuiButton, Integer> buttonMap = new HashMap<GuiButton, Integer>();
	private int currentPage = 1, lastPage = -1;
	private final int maxPages = 4;

	String title = "Voice Chat Setup Wizard.", text = "";

	public GuiScreenOptionsWizard(VoiceChatClient voiceChat, GuiScreen parent) {
		this.voiceChat = voiceChat;
		this.parent = parent;
		tester = new MicrophoneTester(voiceChat);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if ((button == nextButton || button == previousButton || doneButton == button) || (buttonMap.get(button) != null ? buttonMap.get(button) == currentPage : false)) {
			if (!dropDown.dropDownMenu) {
				switch (button.id) {
				case 0:
					if (currentPage < maxPages) currentPage++;
					break;
				case 1:
					if (currentPage >= 2) currentPage--;
					break;
				case 2:
					if (currentPage == maxPages) {
						voiceChat.getSettings().setSetupNeeded(false);
						mc.displayGuiScreen(null);
					}
					break;
				case 3:
					voiceChat.getSettings().setSetupNeeded(false);
					mc.displayGuiScreen(parent);
					break;
				}
			}
		}
	}

	public void drawPage(int x, int y, float tick) {
		if (tester.recording && currentPage != 3) tester.stop();
		if (currentPage != 2 && dropDown.dropDownMenu) dropDown.dropDownMenu = false;
		if (!text.equals(textBatch[currentPage - 1])) text = textBatch[currentPage - 1];
		switch (currentPage) {
		case 1:
			title = "Gliby's Voice Chat " + StringTranslate.getInstance().translateKey("menu.setupWizard");
			break;
		case 2:
			title = StringTranslate.getInstance().translateKey("menu.selectInputDevice");
			dropDown.drawButton(mc, x, y);
			break;
		case 3:
			if (lastPage != currentPage) tester.start();
			title = StringTranslate.getInstance().translateKey("menu.adjustMicrophone");
			IndependentGUITexture.GUI_WIZARD.bindTexture(mc);
			glPushMatrix();
			glEnable(GL_BLEND);
			glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glDisable(GL_ALPHA_TEST);
			glColor4f(1.0F, 1.0F, 1.0F, 1.0f);
			glTranslatef((width / 2) - (26.5f * 1.5f), (height / 2) - (45 * 1.5f), 0);
			glScalef(2.0f, 2.0f, 0.0f);
			IndependentGUITexture.GUI_WIZARD.bindTexture(mc);
			drawTexturedModalRect(0, 0, 0, 127, 35, 20);
			final float progress = tester.currentAmplitude;
			final float procent = (progress / (100 / 31.6f));
			drawTexturedModalRect(3.35f, 0, 35, 127, procent, 20);
			glEnable(GL_ALPHA_TEST);
			glPopMatrix();
			final String ratingText = StringTranslate.getInstance().translateKey("menu.boostVoiceVolume");
			drawCenteredString(fontRenderer, ratingText, width / 2, (height / 2) - 26, -1);
			break;
		case 4:
			title = StringTranslate.getInstance().translateKey("menu.finishWizard");
			break;
		}
		lastPage = currentPage;
	}

	@Override
	public void drawScreen(int x, int y, float tick) {
		drawDefaultBackground();
		IndependentGUITexture.GUI_WIZARD.bindTexture(mc);
		glPushMatrix();
		glTranslatef(((width / 2) - (190 / 2 * 1.5f)), ((height / 2) - (127 / 2 * 1.5f)), 0);
		glScalef(1.5f, 1.5f, 0);
		drawTexturedModalRect(0, 0, 0, 0, 190, 127);
		glPopMatrix();
		drawString(mc.fontRenderer, currentPage + "/" + maxPages, width / 2 + 108, height / 2 + 67, -1);
		if (title != null) drawString(mc.fontRenderer, EnumChatFormatting.BOLD + title, (width / 2) - (mc.fontRenderer.getStringWidth(title) / 2) - 12, height / 2 - 80, -1);
		if (text != null) {
			this.fontRenderer.drawSplitString(CustomEnumFormatting.getTextWithoutFormattingCodes(text), (width / 2) - (215 / 2) - 1 + 1, (height / 2) - 65 + 1, 230, 0);
			this.fontRenderer.drawSplitString((text), (width / 2) - (215 / 2) - 1, (height / 2) - 65, 230, -1);
		}
		for (int k = 0; k < this.buttonList.size(); ++k) {
			final GuiButton guibutton = (GuiButton) this.buttonList.get(k);
			if ((guibutton == nextButton || guibutton == previousButton || guibutton == doneButton) || (buttonMap.get(guibutton) != null ? buttonMap.get(guibutton) == currentPage : false)) {
				guibutton.drawButton(this.mc, x, y);
			}
		}
		drawPage(x, y, tick);
	}

	public void drawTexturedModalRect(float par1, float par2, float par3, float par4, float par5, float par6) {
		final float f = 0.00390625F;
		final float f1 = 0.00390625F;
		final Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(par1 + 0, par2 + par6, this.zLevel, (par3 + 0) * f, (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + par6, this.zLevel, (par3 + par5) * f, (par4 + par6) * f1);
		tessellator.addVertexWithUV(par1 + par5, par2 + 0, this.zLevel, (par3 + par5) * f, (par4 + 0) * f1);
		tessellator.addVertexWithUV(par1 + 0, par2 + 0, this.zLevel, (par3 + 0) * f, (par4 + 0) * f1);
		tessellator.draw();
	}

	@Override
	public void initGui() {
		final String[] array = new String[voiceChat.getSettings().getDeviceHandler().getDevices().size()];
		for (int i = 0; i < voiceChat.getSettings().getDeviceHandler().getDevices().size(); i++)
			array[i] = voiceChat.getSettings().getDeviceHandler().getDevices().get(i).getName();
		dropDown = new GuiDropDownMenu(-1, width / 2 - 75, (height / 2) - 55, 150, 20, voiceChat.getSettings().getInputDevice() != null ? voiceChat.getSettings().getInputDevice().getName() : "None", array);
		buttonList.add(nextButton = new GuiCustomButton(0, width / 2 - 90, height / 2 + 60, 180, 20, StringTranslate.getInstance().translateKey("menu.next") + " ->"));
		buttonList.add(previousButton = new GuiCustomButton(1, width / 2 - 90, height / 2, 180, 20, "<- " + StringTranslate.getInstance().translateKey("menu.previous")));
		buttonList.add(doneButton = new GuiCustomButton(2, width / 2 - 90, height / 2, 180, 20, StringTranslate.getInstance().translateKey("gui.done")));
		buttonList.add(backButton = new GuiCustomButton(3, width / 2 - 90, height / 2 + 18, 180, 20, StringTranslate.getInstance().translateKey("gui.back")));
		buttonList.add(boostSlider = new GuiBoostSlider(900, this.width / 2 - 75, (height / 2) - 15, "", StringTranslate.getInstance().translateKey("menu.boost") + ": " + ((int) (voiceChat.getSettings().getInputBoost() * 5f) <= 0 ? StringTranslate.getInstance().translateKey("options.off") : "" + (int) (voiceChat.getSettings().getInputBoost() * 5) + "db"), 0));
		boostSlider.sliderValue = voiceChat.getSettings().getInputBoost();
		doneButton.drawButton = false;
		buttonMap.put(backButton, 1);
		buttonMap.put(boostSlider, 3);
		dirty = true;
		textBatch = new String[] { StringTranslate.getInstance().translateKey("menu.setupWizardPageOne").replaceAll(Pattern.quote("$n"), "\n").replaceAll(Pattern.quote("$a"), voiceChat.keyManager.getKeyName(EnumBinding.OPEN_GUI_OPTIONS)), StringTranslate.getInstance().translateKey("menu.setupWizardPageTwo").replaceAll(Pattern.quote("$n"), "\n"), StringTranslate.getInstance().translateKey("menu.setupWizardPageThree").replaceAll(Pattern.quote("$n"), "\n"),
				StringTranslate.getInstance().translateKey("menu.setupWizardPageFour").replaceAll(Pattern.quote("$n"), "\n").replaceAll(Pattern.quote("$a"), voiceChat.keyManager.getKeyName(EnumBinding.OPEN_GUI_OPTIONS)).replaceAll(Pattern.quote("$b"), voiceChat.keyManager.getKeyName(EnumBinding.SPEAK)) };
	}

	@Override
	public void mouseClicked(int x, int y, int b) {
		if (currentPage == 2) {
			if (dropDown.getMouseOverInteger() != -1 && dropDown.dropDownMenu && !voiceChat.getSettings().getDeviceHandler().isEmpty()) {
				final Device device = voiceChat.getSettings().getDeviceHandler().getDevices().get(dropDown.getMouseOverInteger());
				if (device != null) {
					voiceChat.getSettings().setInputDevice(device);
					dropDown.setDisplayString(device.getName());
				}
			}
			if (dropDown.mousePressed(this.mc, x, y) && (b == 0)) {
				this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
				dropDown.dropDownMenu = !dropDown.dropDownMenu;
			}
		}

		if (b == 0) {
			for (int l = 0; l < this.buttonList.size(); ++l) {
				final GuiButton guibutton = (GuiButton) this.buttonList.get(l);
				if ((guibutton == nextButton || guibutton == previousButton || doneButton == guibutton) || (buttonMap.get(guibutton) != null ? buttonMap.get(guibutton) == currentPage : false)) {
					if (guibutton.mousePressed(this.mc, x, y)) {
						super.mouseClicked(x, y, b);
					}
				}
			}
		}
	}

	@Override
	public void onGuiClosed() {
		if (tester.recording) tester.stop();
		voiceChat.getSettings().getConfiguration().save();
	}

	@Override
	public void updateScreen() {
		boostSlider.setDisplayString(StringTranslate.getInstance().translateKey("menu.boost") + ": " + ((int) (voiceChat.getSettings().getInputBoost() * 5f) <= 0 ? StringTranslate.getInstance().translateKey("options.off") : "" + (int) (voiceChat.getSettings().getInputBoost() * 5) + "db"));
		voiceChat.getSettings().setInputBoost(boostSlider.sliderValue);
		if (lastPage != currentPage || dirty) {
			if (currentPage == 1) {
				previousButton.drawButton = false;
				doneButton.drawButton = false;
				nextButton.xPosition = width / 2 - 90;
				nextButton.yPosition = height / 2 + 60;
				nextButton.setWidth(180);
				nextButton.setHeight(20);
			} else if (currentPage == 2) {
				previousButton.drawButton = false;
				doneButton.drawButton = false;
				nextButton.xPosition = width / 2 - 90;
				nextButton.yPosition = height / 2 + 60;
				nextButton.setWidth(180);
				nextButton.setHeight(20);
			} else if (currentPage == maxPages) {
				nextButton.drawButton = false;
				doneButton.drawButton = true;
				doneButton.xPosition = width / 2;
				doneButton.yPosition = height / 2 + 60;
				doneButton.setWidth(95);
				doneButton.setHeight(20);
				previousButton.xPosition = width / 2 - 95;
				previousButton.yPosition = height / 2 + 60;
				previousButton.setWidth(95);
				previousButton.setHeight(20);
			} else {
				previousButton.drawButton = true;
				nextButton.drawButton = true;
				doneButton.drawButton = false;
				nextButton.xPosition = width / 2;
				nextButton.yPosition = height / 2 + 60;
				nextButton.setWidth(95);
				nextButton.setHeight(20);
				previousButton.xPosition = width / 2 - 95;
				previousButton.yPosition = height / 2 + 60;
				previousButton.setWidth(95);
				previousButton.setHeight(20);
			}
			dirty = false;
		}
	}
}
