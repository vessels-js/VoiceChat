package net.gliby.voicechat;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityDataPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityPositionPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoicePacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceServerPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoicePacket;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = VoiceChat.MOD_ID, name = "Gliby's Voice Chat Mod", version = VoiceChatServer.VERSION)
public class VoiceChat {

	@Mod.Instance
	public static VoiceChat instance;

	@SidedProxy(modId = VoiceChat.MOD_ID, clientSide = "net.gliby.voicechat.client.VoiceChatClient", serverSide = "net.gliby.voicechat.common.VoiceChatServer")
	public static VoiceChatServer proxy;

	public static SimpleNetworkWrapper DISPATCH;

	public static final String MOD_ID = "gvc";

	public static SimpleNetworkWrapper getDispatcher() {
		return DISPATCH;
	}

	public static VoiceChat getInstance() {
		return instance;
	}

	public static Logger getLogger() {
		return VoiceChatServer.getLogger();
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
		proxy.initMod(this, event);
	}

	@Mod.EventHandler
	public void initServer(FMLServerStartedEvent event) {
		proxy.initServer(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInitMod(this, event);
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		registerNetwork();
		proxy.commonInit(event);
		proxy.preInitClient(event);
	}

	@Mod.EventHandler
	public void preInitServer(FMLServerStartingEvent event) {
		proxy.preInitServer(event);
	}

	@Mod.EventHandler
	public void aboutToStartServer(FMLServerAboutToStartEvent event) {
		proxy.aboutToStartServer(event);
	}

	/**
	 * Do you even back-port bro?
	 **/
	private void registerNetwork() {
		DISPATCH = NetworkRegistry.INSTANCE.newSimpleChannel("GVC");
		DISPATCH.registerMessage(MinecraftServerVoicePacket.class, MinecraftServerVoicePacket.class, 1, Side.SERVER);
		DISPATCH.registerMessage(MinecraftServerVoiceEndPacket.class, MinecraftServerVoiceEndPacket.class, 2, Side.SERVER);

		DISPATCH.registerMessage(MinecraftClientVoiceEndPacket.class, MinecraftClientVoiceEndPacket.class, 9, Side.CLIENT);
		DISPATCH.registerMessage(MinecraftClientVoicePacket.class, MinecraftClientVoicePacket.class, 3, Side.CLIENT);
		DISPATCH.registerMessage(MinecraftClientEntityDataPacket.class, MinecraftClientEntityDataPacket.class, 4, Side.CLIENT);
		DISPATCH.registerMessage(MinecraftClientEntityPositionPacket.class, MinecraftClientEntityPositionPacket.class, 5, Side.CLIENT);
		DISPATCH.registerMessage(MinecraftClientVoiceServerPacket.class, MinecraftClientVoiceServerPacket.class, 6, Side.CLIENT);
		DISPATCH.registerMessage(MinecraftClientVoiceAuthenticatedServer.class, MinecraftClientVoiceAuthenticatedServer.class, 7, Side.CLIENT);
	}

	@Mod.EventHandler
	public void stopServer(FMLServerStoppedEvent event) {
		proxy.stop();
		VoiceChat.getLogger().info("Stopped Voice Server.");
	}
}
