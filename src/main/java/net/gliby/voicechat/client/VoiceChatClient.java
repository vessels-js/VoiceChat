package net.gliby.voicechat.client;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import net.gliby.gman.GMan;
import net.gliby.gman.ModInfo;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.debug.Statistics;
import net.gliby.voicechat.client.gui.GuiInGameHandlerVoiceChat;
import net.gliby.voicechat.client.keybindings.KeyManager;
import net.gliby.voicechat.client.keybindings.KeyTickHandler;
import net.gliby.voicechat.client.networking.ClientNetwork;
import net.gliby.voicechat.client.networking.game.ClientDisconnectHandler;
import net.gliby.voicechat.client.networking.game.ClientEventHandler;
import net.gliby.voicechat.client.render.RenderPlayerVoiceIcon;
import net.gliby.voicechat.client.sound.Recorder;
import net.gliby.voicechat.client.sound.SoundManager;
import net.gliby.voicechat.client.sound.SoundSystemWrapper;
import net.gliby.voicechat.common.VoiceChatServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VoiceChatClient extends VoiceChatServer {
	@SideOnly(Side.CLIENT)
	private static SoundManager soundManager;

	@SideOnly(Side.CLIENT)
	private static Statistics stats;

	public static synchronized Logger getLogger() {
		return LOGGER;
	}

	public static final SoundManager getSoundManager() {
		return soundManager;
	}

	public static final Statistics getStatistics() {
		return stats;
	}

	public static ModMetadata modMetadata;

	@SideOnly(Side.CLIENT)
	private File configurationDirectory;

	@SideOnly(Side.CLIENT)
	private Settings settings;

	@SideOnly(Side.CLIENT)
	public KeyManager keyManager;

	@SideOnly(Side.CLIENT)
	public ModInfo modInfo;
	@SideOnly(Side.CLIENT)
	private ClientNetwork clientNetwork;

	@SideOnly(Side.CLIENT)
	private boolean recorderActive;

	@SideOnly(Side.CLIENT)
	public SoundSystemWrapper sndSystem;

	@SideOnly(Side.CLIENT)
	private VoiceChat voiceChat;

	public Recorder recorder;

	public Map<String, Integer> specialPlayers = new HashMap<String, Integer>();

	public ClientNetwork getClientNetwork() {
		return clientNetwork;
	}

	String[] testPlayers = { "captaindogfish", "starguy1245", "SheheryaB", "arsham123", "Chris9awesome", "TechnoX_X", "bubz052", "McJackson3180", "InfamousArgyle", "jdf2", "XxNotexX0", "SirDenerim", "Frankspark", "smith70831", "killazombiecow", "CraftAeternalis", "choclaterainxx", "dragonballkid4", "TH3_CR33PER", "yetshadow", "KristinnVikarJ", "TheMCBros99", "kevinlame" };

	public String[] getTestPlayers() {
		return testPlayers;
	}

	private net.minecraft.client.audio.SoundManager getMinecraftSoundManager(Minecraft mc) {
		try {
			Field field = SoundHandler.class.getDeclaredFields()[5];
			field.setAccessible(true);
			net.minecraft.client.audio.SoundManager soundManager = (net.minecraft.client.audio.SoundManager) field.get(mc.getSoundHandler());
			return soundManager;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Settings getSettings() {
		return settings;
	}

	public ModInfo getModInfo() {
		return modInfo;
	}

	@Override
	public void initClient(VoiceChat voiceChat, FMLInitializationEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		new UpdatedSoundManager(this, getMinecraftSoundManager(mc)).init(event);
		this.voiceChat = voiceChat;
		this.recorder = new Recorder(this);
		keyManager.init();
		if (settings.getDebugMode()) {
			getLogger().info("Debug enabled!");
			stats = new Statistics();
		}
		getLogger().info("Started client-side on version " + "(" + getVersion() + ")" + "");
		this.clientNetwork = new ClientNetwork(this);
		MinecraftForge.EVENT_BUS.register(new GuiInGameHandlerVoiceChat(this));
		MinecraftForge.EVENT_BUS.register(new RenderPlayerVoiceIcon(this, mc));
		MinecraftForge.EVENT_BUS.register(sndSystem = new SoundSystemWrapper(mc.getSoundHandler()));
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler(this));
		FMLCommonHandler.instance().bus().register(new ClientDisconnectHandler());
		FMLCommonHandler.instance().bus().register(new KeyTickHandler(this));
		getLogger().info("Created SoundSystemWrapper: " + sndSystem + ".");
	}

	public final boolean isRecorderActive() {
		return recorderActive;
	}

	@Override
	public void preInitClient(final FMLPreInitializationEvent event) {
		this.modMetadata = event.getModMetadata();
		configurationDirectory = new File(event.getModConfigurationDirectory(), "gliby_vc");
		if (!this.configurationDirectory.exists()) this.configurationDirectory.mkdir();
		this.settings = new Settings(new File(configurationDirectory, "ClientSettings.ini"));
		this.settings.init();
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				GMan.launchMod(getLogger(), modInfo = new ModInfo(VoiceChat.MOD_ID, event.getModMetadata().updateUrl), getMinecraftVersion(), getVersion());
			}
		});

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
		this.soundManager = new SoundManager(Minecraft.getMinecraft(), this);
		this.soundManager.init();
	}

	public void setRecorderActive(boolean b) {
		if (this.clientNetwork.voiceClientExists()) this.recorderActive = b;
	}

	public static ModMetadata getModMetadata() {
		return modMetadata;
	}
}
