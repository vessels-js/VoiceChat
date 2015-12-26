package net.gliby.voicechat.client.networking;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.networking.voiceclients.MinecraftVoiceClient;
import net.gliby.voicechat.client.networking.voiceclients.UDPVoiceClient;
import net.gliby.voicechat.client.networking.voiceclients.VoiceAuthenticatedClient;
import net.gliby.voicechat.client.networking.voiceclients.VoiceClient;
import net.gliby.voicechat.common.PlayerProxy;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;

public class ClientNetwork {

	private final VoiceChatClient voiceChat;
	private VoiceClient voiceClient;

	private Thread voiceClientThread;
	public boolean connected;

	public ClientNetwork(VoiceChatClient voiceChatClient) {
		this.voiceChat = voiceChatClient;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				stopClientNetwork();
			}
		});
	}

	public VoiceClient getVoiceClient() {
		return voiceClient;
	}

	public void handleEntityData(int entityID, String name, double x, double y, double z) {
		PlayerProxy proxy = VoiceChatClient.getSoundManager().playerData.get(entityID);
		if (proxy != null) {
			proxy.setName(name);
			proxy.setPosition(x, y, z);
		} else {
			proxy = new PlayerProxy(null, entityID, name, x, y, z);
			VoiceChatClient.getSoundManager().playerData.put(entityID, proxy);
		}
	}

	public void handleVoiceAuthenticatedServer(boolean showVoicePlates, boolean showVoiceIcons, int minQuality, int maxQuality, int bufferSize, int soundDistance, int voiceServerType, int udpPort, String hash, String ip) {
		this.startClientNetwork(EnumVoiceNetworkType.values()[voiceServerType], hash, ip, udpPort, soundDistance, bufferSize, minQuality, maxQuality, showVoicePlates, showVoiceIcons);
	}

	public void handleVoiceServer(boolean canShowVoicePlates, boolean canShowVoiceIcons, int minQuality, int maxQuality, int bufferSize, int soundDistance, int voiceServerType) {
		this.startClientNetwork(EnumVoiceNetworkType.values()[voiceServerType], null, null, 0, soundDistance, bufferSize, minQuality, maxQuality, canShowVoicePlates, canShowVoiceIcons);
	}

	public final boolean isConnected() {
		return connected;
	}

	public void sendSamples(byte divider, byte[] samples, boolean end) {
		if (voiceClientExists()) voiceClient.sendVoiceData(divider, samples, end);
	}


	public VoiceClient startClientNetwork(EnumVoiceNetworkType type, String hash, String ip, int udpPort, int soundDist, int bufferSize, int soundQualityMin, int soundQualityMax, boolean showVoicePlates, boolean showVoiceIcons) {
		this.voiceChat.getSettings().resetQuality();
		if (connected) stopClientNetwork();
		VoiceChatClient.getSoundManager().reset();
		switch (type) {
		case MINECRAFT:
			voiceClient = new MinecraftVoiceClient(type);
			break;
		case UDP:
			String serverAddress = ip;
			if (serverAddress.isEmpty()) {
				ServerData serverData;
				if ((serverData = Minecraft.getMinecraft().func_147104_D()) != null) {
					final ServerAddress server = ServerAddress.func_78860_a(serverData.serverIP);
					serverAddress = server.getIP();
				} else serverAddress = "localhost";
			}
			voiceClient = new UDPVoiceClient(type, hash, serverAddress, udpPort);
			break;
		default:
			voiceClient = new MinecraftVoiceClient(type);
			break;
		}
		voiceChat.getSettings().setBufferSize(bufferSize);
		voiceChat.getSettings().setNetworkQuality(soundQualityMin, soundQualityMax);
		voiceChat.getSettings().setSoundDistance(soundDist);
		voiceChat.getSettings().setVoiceIconsAllowed(showVoiceIcons);
		voiceChat.getSettings().setVoicePlatesAllowed(showVoicePlates);
		voiceClientThread = new Thread(voiceClient, "Voice Client");
		voiceClientThread.setDaemon(voiceClient instanceof VoiceAuthenticatedClient);
		voiceClientThread.start();
		connected = true;
		VoiceChatClient.getLogger().info("Connecting to [" + type.name + "] Server, settings[Buffer=" + bufferSize + ", MinQuality=" + soundQualityMin + ", MaxQuality=" + soundQualityMax + ", Distance=" + soundDist + ", Display Voice Icons: " + showVoiceIcons + ", Display Voice Plates: " + showVoicePlates + "]");
		return voiceClient;
	}

	public void stopClientNetwork() {
		connected = false;
		VoiceChatClient.getSoundManager().reset();
		if (voiceClient != null) {
			voiceClient.stop();
			VoiceChatClient.getLogger().info("Stopped Voice Client.");
		}
		if (voiceClientThread != null) voiceClientThread.stop();
		voiceClient = null;
		voiceClientThread = null;
	}

	public boolean voiceClientExists() {
		return voiceClient != null;
	}

}
