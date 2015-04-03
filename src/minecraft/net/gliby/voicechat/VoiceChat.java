package net.gliby.voicechat;

import java.util.logging.Logger;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.networking.voiceclients.MinecraftVoiceClient;
import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.SimpleNetworkFaker;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityDataPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientEntityPositionPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoicePacket;
import net.gliby.voicechat.common.networking.packets.MinecraftClientVoiceServerPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoiceEndPacket;
import net.gliby.voicechat.common.networking.packets.MinecraftServerVoicePacket;
import net.gliby.voicechat.common.networking.voiceservers.MinecraftVoiceServer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerAboutToStart;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopped;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = VoiceChat.MOD_ID, name = "Gliby's Voice Chat Mod", version = "0.6.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, 
clientPacketHandlerSpec = @SidedPacketHandler(channels = {"GVC-ED", "GVC-EP", "GVC-AS", "GVC-E", "GVC-V", "GVC-S"}, packetHandler = MinecraftVoiceClient.PacketHandler.class),
serverPacketHandlerSpec = @SidedPacketHandler(channels = {"GVC-E", "GVC-V"}, packetHandler = MinecraftVoiceServer.PacketHandler.class))
public class VoiceChat {

	@Mod.Instance
	public static VoiceChat instance;

	@SidedProxy(modId = VoiceChat.MOD_ID, clientSide = "net.gliby.voicechat.client.VoiceChatClient", serverSide = "net.gliby.voicechat.common.VoiceChatServer")
	public static VoiceChatServer proxy;

	public static SimpleNetworkFaker DISPATCH;

	public static final String MOD_ID = "gvc";

	public static SimpleNetworkFaker getDispatcher() {
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

	@Init
	public void init(FMLInitializationEvent event) {
		proxy.initMod(this, event);
	}

	@ServerStarted
	public void initServer(FMLServerStartedEvent event) {
		proxy.initServer(event);
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInitMod(this, event);
	}

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		localization();
		registerNetwork();
		proxy.commonInit(event);
		proxy.preInitClient(event);
	}

	private void localization() {
		LanguageRegistry.instance().loadLocalization("/gvc/lang/en_US.lang", "en_US", false);
		LanguageRegistry.instance().loadLocalization("/gvc/lang/de_DE.lang", "de_DE", false);
		LanguageRegistry.instance().loadLocalization("/gvc/lang/fr_FR.lang", "fr_FR", false);
		LanguageRegistry.instance().loadLocalization("/gvc/lang/lv_LV.lang", "lv_LV", false);
		LanguageRegistry.instance().loadLocalization("/gvc/lang/nl_NL.lang", "nl_NL", false);
		LanguageRegistry.instance().loadLocalization("/gvc/lang/ru_RU.lang", "ru_RU", false);
	}


	@ServerStarting
	public void preInitServer(FMLServerStartingEvent event) {
		proxy.preInitServer(event);
	}

	/**
	 * Do you even back-port bro?
	 **/
	private void registerNetwork() {
		DISPATCH = new SimpleNetworkFaker();
	}

	@ServerStopped
	public void stopServer(FMLServerStoppedEvent event) {
		proxy.stop();
		VoiceChat.getLogger().info("Stopped Voice Server.");
	}
	
	@ServerAboutToStart
	public void aboutToStartServer(FMLServerAboutToStartEvent event) {
		proxy.aboutToStartServer(event);
	}
}
