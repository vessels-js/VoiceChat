package net.gliby.voicechat.common.commands;

import java.util.List;

import net.gliby.voicechat.VoiceChat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CommandVoiceMute extends CommandBase {

	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getPlayers()) : null;
	}

	@Override
	public String getCommandName() {
		return "vmute";
	}

	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "Usage: /vmute <player>";
	}

	protected String[] getPlayers() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	/**
	 * Return whether the specified command parameter index is a username
	 * parameter.
	 */
	public boolean isUsernameIndex(int par1) {
		return par1 == 0;
	}

	public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		if (par2ArrayOfStr.length == 1 && par2ArrayOfStr[0].length() > 0) {
			EntityPlayerMP player = getPlayer(par1ICommandSender, par2ArrayOfStr[0]);
			if (player != null) {
				if (VoiceChat.getServerInstance().getServerNetwork().getDataManager().mutedPlayers.contains(player.getUniqueID())) {
					VoiceChat.getServerInstance().getServerNetwork().getDataManager().mutedPlayers.remove(player.getUniqueID());
					func_152373_a(par1ICommandSender, this, player.getDisplayName() + " has been unmuted.", new Object[] { par2ArrayOfStr[0] });
					player.addChatMessage(new ChatComponentText("You have been unmuted!"));
				} else {
					func_152373_a(par1ICommandSender, this, player.getDisplayName() + " has been muted.", new Object[] { par2ArrayOfStr[0] });
					VoiceChat.getServerInstance().getServerNetwork().getDataManager().mutedPlayers.add(player.getUniqueID());
					player.addChatMessage(new ChatComponentText("You have been voice muted, you cannot talk untill you have been unmuted."));
				}
			} else {
				par1ICommandSender.addChatMessage(new ChatComponentText("Player not found for vmute."));
			}
		} else {
			throw new WrongUsageException(getCommandUsage(par1ICommandSender), new Object[0]);
		}
	}

}
