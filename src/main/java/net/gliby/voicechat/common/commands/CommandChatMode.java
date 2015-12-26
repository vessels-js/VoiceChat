package net.gliby.voicechat.common.commands;

import java.util.List;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.ServerStream;
import net.gliby.voicechat.common.networking.ServerStreamManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

public class CommandChatMode extends CommandBase {

	@Override
	public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] { "distance", "global", "world" }) : (par2ArrayOfStr.length == 2 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getListOfPlayerUsernames()) : null);
	}

	public String getChatMode(int chatMode) {
		return chatMode == 0 ? "distance" : chatMode == 2 ? "global" : chatMode == 1 ? "world" : "distance";
	}

	protected int getChatModeFromCommand(ICommandSender par1ICommandSender, String par2Str) {
		return (par2Str.equalsIgnoreCase("distance") || par2Str.startsWith("d") || par2Str.equalsIgnoreCase("0")) ? 0 : ((par2Str.equalsIgnoreCase("world") || par2Str.startsWith("w") || par2Str.equalsIgnoreCase("1")) ? 1 : (par2Str.equalsIgnoreCase("global") || par2Str.startsWith("g") || par2Str.equalsIgnoreCase("2")) ? 2 : 0);
	}

	@Override
	public String getCommandName() {
		return "vchatmode";
	}

	@Override
	public String getCommandUsage(ICommandSender par1ICommandSender) {
		return "/vchatmode <mode> or /vchatmode <mode> [player]";
	}

	/**
	 * Returns String array containing all player usernames in the server.
	 */
	protected String[] getListOfPlayerUsernames() {
		return MinecraftServer.getServer().getAllUsernames();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 3;
	}

	/**
	 * Return whether the specified command parameter index is a username parameter.
	 */
	@Override
	public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2) {
		return par2 == 1;
	}

	@Override
	public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr) {
		/*
		 * if (par2ArrayOfStr.length == 1 && par2ArrayOfStr[0].length() > 0) { EntityPlayerMP player =
		 * getPlayer(par1ICommandSender, par2ArrayOfStr[0]); if(player != null) { } else throw new
		 * WrongUsageException("commands.generic.player.notFound", new Object[0]); } else { } }
		 */

		if (par2ArrayOfStr.length > 0) {
			final int chatMode = this.getChatModeFromCommand(par1ICommandSender, par2ArrayOfStr[0]);
			final EntityPlayerMP player = par2ArrayOfStr.length >= 2 ? getPlayer(par1ICommandSender, par2ArrayOfStr[1]) : getCommandSenderAsPlayer(par1ICommandSender);
			if (player != null) {
				final ServerStreamManager dataManager = VoiceChat.getServerInstance().getServerNetwork().getDataManager();
				dataManager.chatModeMap.put(player.getPersistentID(), Integer.valueOf(chatMode));
				final ServerStream stream = dataManager.getStream(player.getEntityId());
				if (stream != null) stream.dirty = true;
				if (player != par1ICommandSender) {
					func_152373_a(par1ICommandSender, this, player.getCommandSenderName() + " set chat mode to " + getChatMode(chatMode).toUpperCase() + " (" + chatMode + ")", new Object[] { par2ArrayOfStr[0] });
				} else {
					player.addChatMessage(new ChatComponentText("Set own chat mode to " + getChatMode(chatMode).toUpperCase() + " (" + chatMode + ")"));
					switch (chatMode) {
					case 0:
						player.addChatMessage(new ChatComponentText("Only players near you can hear you."));
						break;
					case 1:
						player.addChatMessage(new ChatComponentText("Every player in this world can hear you"));
						break;
					case 2:
						player.addChatMessage(new ChatComponentText("Every player can hear you."));
						break;
					}
				}
			} else {
				throw new WrongUsageException("commands.generic.player.notFound", new Object[0]);
			}
		} else {
			throw new WrongUsageException(getCommandUsage(null), new Object[0]);
		}
	}

}
