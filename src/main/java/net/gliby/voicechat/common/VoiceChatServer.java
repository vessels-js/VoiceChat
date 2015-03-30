package net.gliby.voicechat.common;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Random;

import net.gliby.voicechat.VoiceChat;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

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

	/** Voice Server for lan/vanilla **/
	private VoiceServer voiceServer;

	private Thread voiceServerThread;

	public ServerNetwork serverNetwork;

	public ServerSettings serverSettings;

	private File configurationDirectory;

	private int getAvailablePort() throws IOException {
		int port = 0;
		do {
			port = randInt(4001, 65534);
		} while (!available(port));

		return port;
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

	public synchronized VoiceServer getVoiceServer() {
		return voiceServer;
	}

	public void init(FMLServerStartedEvent event) {
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

	public void initClient(VoiceChat voiceChat, FMLInitializationEvent event) {
	}

	public void postInit(VoiceChat voiceChat, FMLPostInitializationEvent event) {
	}

	public void preInitClient(FMLPreInitializationEvent event) {
	}

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
		final Thread thread = new Thread(voiceServer, "Voice Server");
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
