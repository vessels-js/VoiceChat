package net.gliby.voicechat.common.networking;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public abstract class MinecraftPacket implements IMessage {

	public MinecraftPacket() {
		super();
	}
	
}
