package net.gliby.voicechat;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.packets.PacketClientChunkVoiceSample;
import net.gliby.voicechat.common.networking.packets.PacketClientEntityData;
import net.gliby.voicechat.common.networking.packets.PacketClientEntityPosition;
import net.gliby.voicechat.common.networking.packets.PacketClientVoiceEnd;
import net.gliby.voicechat.common.networking.packets.PacketClientVoiceSample;
import net.gliby.voicechat.common.networking.packets.PacketClientVoiceServer;
import net.gliby.voicechat.common.networking.packets.PacketClientVoiceServerAuth;
import net.gliby.voicechat.common.networking.packets.PacketServerVoiceEnd;
import net.gliby.voicechat.common.networking.packets.PacketServerVoiceSample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = VoiceChat.MOD_ID, name = "Gliby's Voice Chat Mod", version = "0.5.9")
public class VoiceChat {

	@Mod.Instance
	public static VoiceChat instance;

	@SidedProxy(modId = VoiceChat.MOD_ID, clientSide = "net.gliby.voicechat.client.VoiceChatClient", serverSide = "net.gliby.voicechat.common.VoiceChatServer")
	public static VoiceChatServer proxy;

	public static SimpleNetworkWrapper DISPATCH;

	protected static final Logger LOGGER = LogManager.getLogger("Gliby's Voice Chat Mod");

	public static final String MOD_ID = "gvc";

	public static SimpleNetworkWrapper getDispatcher() {
		return DISPATCH;
	}

	public static VoiceChat getInstance() {
		return instance;
	}

	public static Logger getLogger() {
		return proxy.getLogger();
	}

	public static VoiceChatClient getProxyInstance() {
		return (VoiceChatClient) (proxy instanceof VoiceChatClient ? (VoiceChatClient) proxy : proxy);
	}

	public static VoiceChatServer getServerInstance() {
		return proxy;
	}

	public synchronized static VoiceChat getSynchronizedInstance() {
		return instance;
	}

	public synchronized static VoiceChatClient getSynchronizedProxyInstance() {
		return (VoiceChatClient) proxy;
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		if (event.getSide() == Side.CLIENT) {
			proxy.initClient(this, event);
		}
	}

	@Mod.EventHandler
	public void initServer(FMLServerStartedEvent event) {
		proxy.init(event);
	}

	/**
	 * Do you even back-port bro?
	 **/
	private void networkClient() {
		DISPATCH = NetworkRegistry.INSTANCE.newSimpleChannel("GVC");
		DISPATCH.registerMessage(PacketClientChunkVoiceSample.class, PacketClientChunkVoiceSample.class, 1, Side.CLIENT);
		DISPATCH.registerMessage(PacketClientEntityData.class, PacketClientEntityData.class, 2, Side.CLIENT);
		DISPATCH.registerMessage(PacketClientEntityPosition.class, PacketClientEntityPosition.class, 3, Side.CLIENT);
		DISPATCH.registerMessage(PacketClientVoiceEnd.class, PacketClientVoiceEnd.class, 4, Side.CLIENT);
		DISPATCH.registerMessage(PacketClientVoiceSample.class, PacketClientVoiceSample.class, 5, Side.CLIENT);
		DISPATCH.registerMessage(PacketClientVoiceServer.class, PacketClientVoiceServer.class, 6, Side.CLIENT);
		DISPATCH.registerMessage(PacketClientVoiceServerAuth.class, PacketClientVoiceServerAuth.class, 7, Side.CLIENT);
		DISPATCH.registerMessage(PacketServerVoiceSample.class, PacketServerVoiceSample.class, 8, Side.SERVER);
		DISPATCH.registerMessage(PacketServerVoiceEnd.class, PacketServerVoiceEnd.class, 9, Side.SERVER);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(this, event);
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		networkClient();
		proxy.preInitClient(event);
	}

	@Mod.EventHandler
	public void preInitServer(FMLServerStartingEvent event) {
		proxy.preInitServer(event);
	}

	@Mod.EventHandler
	public void stopServer(FMLServerStoppingEvent event) {
		VoiceChat.getLogger().info("Stopping Voice Server.");
		proxy.stop();
	}
}
