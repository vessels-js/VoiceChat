package net.gliby.voicechat.client.networking.game;

import net.gliby.voicechat.VoiceChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.relauncher.Side;

public class ClientDisconnectHandler implements IPlayerTracker  {
	//TODO fix.
	public void onPlayerLogin(EntityPlayer player) {}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			System.out.println("GOT CALLLLLLLED ON CLIENT");
			VoiceChat.getProxyInstance().getClientNetwork().stopClientNetwork();
		}
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {}

}
