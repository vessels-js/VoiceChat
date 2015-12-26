package net.gliby.voicechat.client;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.debug.Statistics;
import net.gliby.voicechat.client.gui.GuiInGameHandlerVoiceChat;
import net.gliby.voicechat.client.gui.options.GuiScreenOptionsWizard;
import net.gliby.voicechat.client.keybindings.KeyManager;
import net.gliby.voicechat.client.keybindings.KeyTickHandler;
import net.gliby.voicechat.client.networking.ClientNetwork;
import net.gliby.voicechat.client.networking.game.ClientDisconnectHandler;
import net.gliby.voicechat.client.networking.game.ClientEventHandler;
import net.gliby.voicechat.client.render.RenderPlayerVoiceIcon;
import net.gliby.voicechat.client.sound.ClientStreamManager;
import net.gliby.voicechat.client.sound.Recorder;
import net.gliby.voicechat.common.VoiceChatServer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class VoiceChatClient extends VoiceChatServer {
	@SideOnly(Side.CLIENT)
	private static ClientStreamManager soundManager;

	@SideOnly(Side.CLIENT)
	private static Statistics stats;

	public static ModMetadata modMetadata;

	public static synchronized Logger getLogger() {
		return LOGGER;
	}

	public static ModMetadata getModMetadata() {
		return modMetadata;
	}

	public static final ClientStreamManager getSoundManager() {
		return soundManager;
	}

	public static final Statistics getStatistics() {
		return stats;
	}

	@SideOnly(Side.CLIENT)
	private File configurationDirectory;

	@SideOnly(Side.CLIENT)
	private Settings settings;

	@SideOnly(Side.CLIENT)
	public KeyManager keyManager;

	@SideOnly(Side.CLIENT)
	private ClientNetwork clientNetwork;

	@SideOnly(Side.CLIENT)
	private boolean recorderActive;

	@SideOnly(Side.CLIENT)
	private VoiceChat voiceChat;

	public Recorder recorder;

	public Map<String, Integer> specialPlayers = new HashMap<String, Integer>();

	String[] testPlayers = { "captaindogfish", "starguy1245", "SheheryaB", "arsham123", "Chris9awesome", "TechnoX_X", "bubz052", "McJackson3180", "InfamousArgyle", "jdf2", "XxNotexX0", "SirDenerim", "Frankspark", "smith70831", "killazombiecow", "CraftAeternalis", "choclaterainxx", "dragonballkid4", "TH3_CR33PER", "yetshadow", "KristinnVikarJ", "TheMCBros99", "kevinlame" };

	public ClientNetwork getClientNetwork() {
		return clientNetwork;
	}

	public Settings getSettings() {
		return settings;
	}

	public String[] getTestPlayers() {
		return testPlayers;
	}

	private boolean init;

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onJoin(PlayerTickEvent event) {
		if (event.phase == Phase.END) {
			if (event.side == Side.CLIENT && !init) {
				final Minecraft mc = Minecraft.getMinecraft();
				final VoiceChatClient voiceChat = VoiceChat.getProxyInstance();
				if (voiceChat.getSettings().isSetupNeeded()) {
					mc.displayGuiScreen(new GuiScreenOptionsWizard(voiceChat, null));
				}
				init = true;
			}
		}
	}

	@Override
	public void initMod(VoiceChat voiceChat, FMLInitializationEvent event) {
		final Minecraft mc = Minecraft.getMinecraft();
		new UpdatedSoundManager(this);
		this.voiceChat = voiceChat;
		this.recorder = new Recorder(this);
		keyManager.init();
		if (settings.isDebug()) {
			getLogger().info("Debug enabled!");
			stats = new Statistics();
		}
		getLogger().info("Started client-side on version " + "(" + getVersion() + ")" + "");
		this.clientNetwork = new ClientNetwork(this);
		MinecraftForge.EVENT_BUS.register(new GuiInGameHandlerVoiceChat(this));
		MinecraftForge.EVENT_BUS.register(new RenderPlayerVoiceIcon(this, mc));
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler(this));
		FMLCommonHandler.instance().bus().register(new ClientDisconnectHandler());
		FMLCommonHandler.instance().bus().register(new KeyTickHandler(this));
		FMLCommonHandler.instance().bus().register(this);
	}

	@Override
	public void preInitClient(final FMLPreInitializationEvent event) {
		modMetadata = event.getModMetadata();
		configurationDirectory = new File(event.getModConfigurationDirectory(), "gliby_vc");
		if (!this.configurationDirectory.exists()) this.configurationDirectory.mkdir();
		this.settings = new Settings(new File(configurationDirectory, "ClientSettings.ini"));
		this.settings.init();
		// UUID check for each player is way to expensive for simple things like
		// this, so we are sticking with player names!
		this.keyManager = new KeyManager(this);
		specialPlayers.put("theGliby", 1);
		specialPlayers.put("Rinto", 1);
		specialPlayers.put("DanielSturk", 1);
		specialPlayers.put("CraftAeternalis", 3);
		specialPlayers.put("YETSHADOW", 5);
		specialPlayers.put("McJackson3180", 6);
		specialPlayers.put("smith70831", 7);
		specialPlayers.put("XxNotexX0", 8);
		specialPlayers.put("TheHaxman2", 9);
		VoiceChatClient.soundManager = new ClientStreamManager(Minecraft.getMinecraft(), this);
		VoiceChatClient.soundManager.init();
	}

	public final boolean isRecorderActive() {
		return recorderActive;
	}

	public void setRecorderActive(boolean b) {
		if (this.clientNetwork.voiceClientExists()) this.recorderActive = b;
	}
}
