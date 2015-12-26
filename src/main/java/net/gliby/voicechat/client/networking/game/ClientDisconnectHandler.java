package net.gliby.voicechat.client.networking.game;

import net.gliby.voicechat.VoiceChat;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;

public class ClientDisconnectHandler {

	@SubscribeEvent
	public void onClientDisconnected(ClientDisconnectionFromServerEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			VoiceChat.getProxyInstance().getClientNetwork().stopClientNetwork();
		}
	}

}
