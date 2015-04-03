package net.gliby.voicechat.client.networking.game;

import net.gliby.voicechat.VoiceChat;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.TcpConnection;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class ClientDisconnectHandler implements IConnectionHandler {
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {}

	private Object networkManager;
	
	@Override
	public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
		this.networkManager = manager;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
		this.networkManager = manager;
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT && manager.equals((INetworkManager)networkManager)) {
			VoiceChat.getProxyInstance().getClientNetwork().stopClientNetwork();
		}
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {}

}
