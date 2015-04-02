package net.gliby.voicechat.client.networking.game;

import net.gliby.voicechat.VoiceChat;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientDisconnectHandler {

	@SubscribeEvent
	public void onClientDisconnected(ClientDisconnectionFromServerEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			VoiceChat.getProxyInstance().getClientNetwork().stopClientNetwork();
		}
	}

}
