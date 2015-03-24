package net.gliby.voicechat.client.networking;

import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.networking.voiceservers.UDPVoiceClient;
import net.gliby.voicechat.common.PlayerProxy;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;

public class ClientNetwork {

	private VoiceChatClient voiceChat;
	private VoiceClient voiceClient;

	private Thread voiceClientThread;
	public boolean connected;

	public ClientNetwork(VoiceChatClient voiceChatClient) {
		this.voiceChat = voiceChatClient;
	}

	public VoiceClient getVoiceClient() {
		return voiceClient;
	}

	public void handleEntityData(int entityID, String name, double x, double y, double z) {
		PlayerProxy proxy = voiceChat.getSoundManager().playerData.get(entityID);
		if (proxy != null) {
			proxy.setName(name);
			proxy.setPosition(x, y, z);
		} else {
			proxy = new PlayerProxy(null, entityID, name, x, y, z);
			voiceChat.getSoundManager().playerData.put(entityID, proxy);
		}
	}

	public final boolean isConnected() {
		return connected;
	}

	public void sendSamples(byte divider, byte[] samples, boolean end) {
		if (voiceClientExists()) voiceClient.sendVoiceData(divider, samples, end);
	}

	/** TODO LAST - REMOVE LOG AND SYSO **/

	public VoiceClient startClientNetwork(EnumVoiceNetworkType type, String hash, String ip, int udpPort, int soundDist, int bufferSize, int soundQualityMin, int soundQualityMax, boolean showVoicePlates, boolean showVoiceIcons) {
		this.voiceChat.sndSystem.refresh();
		this.voiceChat.getSettings().resetQuality();
		if (connected) stopClientNetwork();
		voiceChat.getSoundManager().reset();
		switch (type) {
			case MINECRAFT:
				voiceClient = new MinecraftVoiceClient(type);
				break;
			case UDP:
				//TODO Doesn't work on non-NAT.
				String serverAddress = ip;
				if (serverAddress.isEmpty()) {
					ServerData serverData;
					if ((serverData = Minecraft.getMinecraft().func_147104_D()) != null) {
						ServerAddress server = ServerAddress.func_78860_a(serverData.serverIP);
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
		voiceChat.getLogger().info("Connecting to [" + type.name + "] Server, settings[Buffer=" + bufferSize + ", MinQuality=" + soundQualityMin + ", MaxQuality=" + soundQualityMax + ", Distance=" + soundDist + ", Display Voice Icons: " + showVoiceIcons + ", Display Voice Plates: " + showVoicePlates + "]");
		return voiceClient;
	}

	public void stopClientNetwork() {
		connected = false;
		voiceChat.getSoundManager().reset();
		if (voiceClient != null) {
			voiceClient.stop();
			voiceChat.getLogger().info("Stopped Voice Client.");
		}
		if (voiceClientThread != null) voiceClientThread.stop();
		voiceClient = null;
		voiceClientThread = null;
	}

	public boolean voiceClientExists() {
		return voiceClient != null;
	}

}