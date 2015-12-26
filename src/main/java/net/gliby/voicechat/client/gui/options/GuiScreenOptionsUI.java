package net.gliby.voicechat.client.gui.options;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.gui.GuiBoostSlider;
import net.gliby.voicechat.client.gui.GuiUIPlacement;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenOptionsUI extends GuiScreen {

	private final VoiceChatClient voiceChat;
	private final GuiScreen parent;

	private GuiBoostSlider opacity;
	public GuiScreenOptionsUI(VoiceChatClient voiceChat, GuiScreen parent) {
		this.voiceChat = voiceChat;
		this.parent = parent;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0:
			mc.displayGuiScreen(parent);
			break;
		case 1:
			voiceChat.getSettings().resetUI(width, height);
			opacity.sliderValue = 1.0f;
			break;
		case 2:
			mc.displayGuiScreen(new GuiUIPlacement(this));
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
		glTranslatef(width / 2 - (fontRendererObj.getStringWidth(I18n.format("menu.uiOptions")) / 2), 12, 0);
		drawString(mc.fontRenderer, I18n.format("menu.uiOptions"), 0, 12, -1);
		glPopMatrix();
		super.drawScreen(x, y, time);
	}

	@Override
	public void initGui() {
		buttonList.add(new GuiButton(0, (this.width / 2) - 75, height - 34, 150, 20, I18n.format("gui.back", new Object[0])));
		buttonList.add(new GuiButton(1, (this.width / 2) - 75, 73, 150, 20, I18n.format("menu.resetAll")));
		buttonList.add(new GuiButton(2, (this.width / 2) - 150, 50, 150, 20, I18n.format("menu.uiPlacement")));
		buttonList.add(opacity = new GuiBoostSlider(-1, this.width / 2 + 2, 50, "", I18n.format("menu.uiOpacity") + ": " + (voiceChat.getSettings().getUIOpacity() == 0 ? I18n.format("options.off") : (int) (voiceChat.getSettings().getUIOpacity() * 100) + "%"), 0));
		opacity.sliderValue = voiceChat.getSettings().getUIOpacity();
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		voiceChat.getSettings().getConfiguration().save();
	}

	@Override
	public void updateScreen() {
		super.onGuiClosed();
		voiceChat.getSettings().setUIOpacity(opacity.sliderValue);
		opacity.setDisplayString(I18n.format("menu.uiOpacity") + ": " + (voiceChat.getSettings().getUIOpacity() == 0 ? I18n.format("options.off") : (int) (voiceChat.getSettings().getUIOpacity() * 100) + "%"));
	}
}
