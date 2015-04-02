package net.gliby.voicechat.common;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Random;
import java.util.concurrent.Executors;

import net.gliby.gman.GMan;
import net.gliby.gman.ModInfo;
import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.api.VoiceChatAPI;
import net.gliby.voicechat.common.commands.CommandChatMode;
import net.gliby.voicechat.common.commands.CommandVoiceMute;
import net.gliby.voicechat.common.networking.ServerNetwork;
import net.gliby.voicechat.common.networking.voiceservers.MinecraftVoiceServer;
import net.gliby.voicechat.common.networking.voiceservers.ServerConnectionHandler;
import net.gliby.voicechat.common.networking.voiceservers.VoiceAuthenticatedServer;
import net.gliby.voicechat.common.networking.voiceservers.VoiceServer;
import net.gliby.voicechat.common.networking.voiceservers.udp.UDPVoiceServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VoiceChatServer {
	private static final String VERSION = "0.6.0";
	private static final String MC_VERSION = "1.7.10";
	protected static final Logger LOGGER = LogManager.getLogger("Gliby's Voice Chat Mod");

	public static boolean available(int port) {
		if (port < 4000 || port > 65535) { throw new IllegalArgumentException("Invalid start port: " + port); }

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static synchronized Logger getLogger() {
		return LOGGER;
	}

	public static String getMinecraftVersion() {
		return MC_VERSION;
	}

	public static int randInt(int min, int max) {
		final Random rand = new Random();
		final int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public ModInfo modInfo;

	private VoiceServer voiceServer;

	private Thread voiceServerThread;

	public ServerNetwork serverNetwork;

	public ServerSettings serverSettings;

	private File configurationDirectory;

	public void commonInit(final FMLPreInitializationEvent event) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				GMan.launchMod(getLogger(), modInfo = new ModInfo(VoiceChat.MOD_ID, event.getModMetadata().updateUrl), getMinecraftVersion(), getVersion());
			}
		});
		new VoiceChatAPI().init();
	}

	private int getAvailablePort() throws IOException {
		int port = 0;
		do {
			port = randInt(4001, 65534);
		} while (!available(port));

		return port;
	}

	public ModInfo getModInfo() {
		return modInfo;
	}

	private int getNearestPort(int port) {
		return ++port;
	}

	public synchronized ServerNetwork getServerNetwork() {
		return serverNetwork;
	}

	public ServerSettings getServerSettings() {
		return serverSettings;
	}

	public String getVersion() {
		return VERSION;
	}


	public VoiceServer getVoiceServer() {
		return voiceServer;
	}

	public void initMod(VoiceChat voiceChat, FMLInitializationEvent event) {}

	public void initServer(FMLServerStartedEvent event) {
		final MinecraftServer server = MinecraftServer.getServer();
		if (serverSettings.getUDPPort() == 0) {
			if (server.isDedicatedServer()) {
				int queryPort = -1;
				if (((DedicatedServer) server).getBooleanProperty("enable-query", false)) queryPort = ((DedicatedServer) server).getIntProperty("query.port", 0);
				final boolean portTaken = queryPort == server.getServerPort();
				serverSettings.setUDPPort(portTaken ? getNearestPort(server.getPort()) : server.getPort());
				if (portTaken) VoiceChatServer.getLogger().warn("Hey! Over Here! It seems you are running a query on the default port. We can't run a voice server on this port, so I've found a new one just for you! I'd recommend changing the UDPPort in your configuration, if the voice server can't bind!");
			} else {
				try {
					serverSettings.setUDPPort(getAvailablePort());
				} catch (final IOException e) {
					VoiceChatServer.getLogger().fatal("Couldn't start voice server.");
					e.printStackTrace();
					return;
				}
			}
		}
		voiceServerThread = startVoiceServer();
	}

	public void postInitMod(VoiceChat voiceChat, FMLPostInitializationEvent event) {}

	public void preInitClient(FMLPreInitializationEvent event) {}

	public void preInitServer(FMLServerStartingEvent event) {
		FMLCommonHandler.instance().bus().register(new ServerConnectionHandler(this));
		serverSettings = new ServerSettings(this);
		configurationDirectory = new File("config/gliby_vc");
		if (!configurationDirectory.exists()) configurationDirectory.mkdir();
		serverSettings.preInit(new File(configurationDirectory, "ServerSettings.ini"));
		event.registerServerCommand(new CommandVoiceMute());
		event.registerServerCommand(new CommandChatMode());
	}

	private Thread startVoiceServer() {
		serverNetwork = new ServerNetwork(this);
		serverNetwork.init();
		switch (serverSettings.getAdvancedNetworkType()) {
		case 0:
			voiceServer = new MinecraftVoiceServer(this);
			break;
		case 1:
			voiceServer = new UDPVoiceServer(this);
			break;
		default:
			voiceServer = new MinecraftVoiceServer(this);
			break;
		}
		final Thread thread = new Thread(voiceServer, "Voice Server Process");
		thread.setDaemon(voiceServer instanceof VoiceAuthenticatedServer);
		thread.start();
		return thread;
	}

	public void stop() {
		serverNetwork.stop();
		if (voiceServer instanceof VoiceAuthenticatedServer) ((VoiceAuthenticatedServer) voiceServer).waitingAuth.clear();
		voiceServer.stop();
		voiceServer = null;
		voiceServerThread.stop();
	}
}
