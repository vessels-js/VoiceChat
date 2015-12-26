package net.gliby.voicechat.client.gui;

import java.awt.Rectangle;
import java.util.ArrayList;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GuiScreenLocalMute extends GuiScreen {
	@SideOnly(Side.CLIENT)
	class List extends GuiSlot {
		private final Rectangle buttonCross;

		public List() {
			super(GuiScreenLocalMute.this.mc, GuiScreenLocalMute.this.width, GuiScreenLocalMute.this.height, 32, GuiScreenLocalMute.this.height - 65 + 4, 18);
			buttonCross = new Rectangle(0, 0, 20, 20);
		}

		@Override
		protected void drawBackground() {
			GuiScreenLocalMute.this.drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int valueId, int par1, int par2, int par3, Tessellator tessellator, int par4, int par5) {
			GuiScreenLocalMute.this.drawCenteredString(GuiScreenLocalMute.this.fontRendererObj, VoiceChatClient.getSoundManager().playerMutedData.get(VoiceChatClient.getSoundManager().playersMuted.get(valueId)), this.width / 2, par2 + 1, 16777215);
			GuiScreenLocalMute.this.drawCenteredString(GuiScreenLocalMute.this.fontRendererObj, "\247lX", this.width / 2 + 88, par2 + 3, 0xff0000);
		}

		/**
		 * The element in the slot that was clicked, boolean for whether it was double clicked or not
		 */
		@Override
		protected void elementClicked(int index, boolean p_148144_2_, int x, int y) {
			VoiceChatClient.getSoundManager().playersMuted.remove(index);
			VoiceChatClient.getSoundManager();
			ClientStreamManager.playerMutedData.remove(index);
		}

		/**
		 * Return the height of the content being scrolled
		 */
		@Override
		protected int getContentHeight() {
			return this.getSize() * 18;
		}

		@Override
		protected int getSize() {
			return VoiceChatClient.getSoundManager().playersMuted.size();
		}

		/**
		 * Returns true if the element passed in is currently selected
		 */
		@Override
		protected boolean isSelected(int p_148131_1_) {
			return true;
		}
	}

	protected GuiScreen parent;
	private GuiScreenLocalMute.List listPlayers;
	private GuiOptionButton doneButton;
	private GuiTextField playerTextField;
	private boolean playerNotFound;

	private ArrayList<String> autoCompletionNames;

	public GuiScreenLocalMute(GuiScreen par1GuiScreen, VoiceChatClient voiceChat) {
		this.parent = par1GuiScreen;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		actionPerformed(button.id);
	}

	private void actionPerformed(int button) {
		switch (button) {
		case 0:
			playerNotFound = false;
			final EntityPlayer entityPlayer = mc.theWorld.getPlayerEntityByName(playerTextField.getText().trim().replaceAll(" ", ""));
			if (entityPlayer != null) {
				if (!entityPlayer.isClientWorld() && !VoiceChatClient.getSoundManager().playersMuted.contains(entityPlayer.getEntityId())) {
					VoiceChatClient.getSoundManager().playersMuted.add(entityPlayer.getEntityId());
					VoiceChatClient.getSoundManager();
					ClientStreamManager.playerMutedData.put(entityPlayer.getEntityId(), entityPlayer.getCommandSenderName());
				}
			} else playerNotFound = true;
			break;
		case 1:
			this.mc.displayGuiScreen(this.parent);
			break;
		}
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.listPlayers.drawScreen(par1, par2, par3);
		this.drawCenteredString(this.fontRendererObj, I18n.format("menu.mutedPlayers"), this.width / 2, 16, -1);
		if (playerNotFound) {
			this.drawCenteredString(this.fontRendererObj, "\247c" + I18n.format("commands.generic.player.notFound"), this.width / 2, height - 59, -1);
		}
		playerTextField.drawTextBox();
		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		autoCompletionNames = new ArrayList<String>();
		final int heightOffset = -9;
		playerTextField = new GuiTextField(this.fontRendererObj, width / 2 - 100, this.height - 57 - heightOffset, 130, 20);
		playerTextField.setFocused(true);
		this.buttonList.add(this.doneButton = new GuiOptionButton(0, this.width / 2 + 32, this.height - 57 - heightOffset, 98, 20, I18n.format("menu.add")));
		this.buttonList.add(this.doneButton = new GuiOptionButton(1, this.width / 2 - 75, this.height - 32 - heightOffset, I18n.format("gui.done")));
		this.listPlayers = new GuiScreenLocalMute.List();
		this.listPlayers.registerScrollButtons(7, 8);
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		playerNotFound = false;
		this.playerTextField.textboxKeyTyped(par1, par2);
		super.keyTyped(par1, par2);
		switch (par2) {
		case Keyboard.KEY_RETURN:
			actionPerformed(0);
			break;
		case Keyboard.KEY_TAB:
			if (autoCompletionNames.size() > 0) {
				shuffleCompleition();
			} else {
				autoCompletionNames.clear();
				final Object[] astring1 = mc.theWorld.playerEntities.toArray();
				final int i = astring1.length;
				for (int j = 0; j < i; ++j) {
					final Object obj = astring1[j];
					if (obj instanceof EntityOtherPlayerMP) {
						final String s2 = ((EntityOtherPlayerMP) obj).getCommandSenderName();
						if (s2.toLowerCase().startsWith(playerTextField.getText().toLowerCase().trim().replaceAll(" ", ""))) {
							autoCompletionNames.add(s2);
						}
					}
				}
				shuffleCompleition();
			}
			break;
		default:
			autoCompletionNames.clear();
			break;
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	private void shuffleCompleition() {
		if (autoCompletionNames.iterator().hasNext()) {
			final String name = autoCompletionNames.iterator().next();
			autoCompletionNames.add(name);
			playerTextField.setText(name);
			autoCompletionNames.remove(name);
		}
	}

	@Override
	public void updateScreen() {
		playerTextField.updateCursorCounter();
	}
}
