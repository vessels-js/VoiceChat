package net.gliby.voicechat.client.networking;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;

public class ClientPacketHandler {
	private static ClientNetwork network = VoiceChat.getProxyInstance().getClientNetwork();

	public static void handleChunkVoiceData(byte[] payload) {
		if (network.isConnected()) {
			if (VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient() == null) return;
			ByteArrayInputStream bais = new ByteArrayInputStream(payload);
			DataInputStream dis = new DataInputStream(bais);
			int entityID = -1;
			byte chunkSize = 0;
			boolean direct = false;
			byte[] data = null;
			try {
				int size = dis.readInt();
				data = new byte[size];
				for (int i = 0; i < size; i++) {
					data[i] = dis.readByte();
				}
				chunkSize = dis.readByte();
				entityID = dis.readInt();
				direct = dis.readBoolean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			network.getVoiceClient().handlePacket(entityID, data, chunkSize, direct);
		}
	}

	public static void handleEntityData(byte[] payload) {
		if (network.isConnected()) {
			ByteArrayInputStream bais = new ByteArrayInputStream(payload);
			DataInputStream dis = new DataInputStream(bais);
			int entityID = -1;
			double x = -1, y = -1, z = -1;
			String name = null;
			try {
				entityID = dis.readInt();
				name = dis.readUTF();
				x = dis.readDouble();
				y = dis.readDouble();
				z = dis.readDouble();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (name != null) network.handleEntityData(entityID, name, x, y, z);
		}
	}

	public static void handleEntityPosition(byte[] payload) {
		if (network.isConnected()) {
			ByteArrayInputStream bais = new ByteArrayInputStream(payload);
			DataInputStream dis = new DataInputStream(bais);
			int entityID = -1;
			double x = -1, y = -1, z = -1;
			try {
				entityID = dis.readInt();
				x = dis.readDouble();
				y = dis.readDouble();
				z = dis.readDouble();
			} catch (Exception e) {
				e.printStackTrace();
			}
			network.getVoiceClient().handleEntityPosition(entityID, x, y, z);
		}
	}

	public static void handleVoiceData(byte[] payload) {
		if (network.isConnected()) {
			ByteArrayInputStream bais = new ByteArrayInputStream(payload);
			DataInputStream dis = new DataInputStream(bais);
			int entityID = -1;
			boolean direct = false;
			byte[] data = null;
			try {
				int size = dis.readInt();
				data = new byte[size];
				for (int i = 0; i < size; i++) {
					data[i] = dis.readByte();
				}
				entityID = dis.readInt();
				direct = dis.readBoolean();
			} catch (Exception e) {
				e.printStackTrace();
			}
			network.getVoiceClient().handlePacket(entityID, data, data.length, direct);
		}
	}

	public static void handleVoiceEnd(byte[] payload) {
		if (network.isConnected()) {
			if (VoiceChat.getProxyInstance().getClientNetwork().getVoiceClient() == null) return;
			ByteArrayInputStream bais = new ByteArrayInputStream(payload);
			DataInputStream dis = new DataInputStream(bais);
			int entityID = -1;
			try {
				entityID = dis.readInt();
			} catch (Exception e) {
				e.printStackTrace();
			}
			network.getVoiceClient().handleEnd(entityID);
		}
	}

	public static void handleVoiceServer(byte[] payload) {
		ByteArrayInputStream bais = new ByteArrayInputStream(payload);
		DataInputStream dis = new DataInputStream(bais);
		int type = 0, maxSoundDistance = 64, x0 = 0, x1 = 0, bufferSize = 0;
		boolean showVoicePlates = true, showPlayerIcons = true;
		try {
			showVoicePlates = dis.readBoolean();
			showPlayerIcons = dis.readBoolean();
			x0 = dis.readInt();
			x1 = dis.readInt();
			bufferSize = dis.readInt();
			maxSoundDistance = dis.readInt();
			type = dis.readInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		network.startClientNetwork(EnumVoiceNetworkType.values()[type], null, null, 0, maxSoundDistance, bufferSize, x0, x1, showVoicePlates, showPlayerIcons);
	}

	public static void handleVoiceServerAuthentication(byte[] payload) {
		ByteArrayInputStream bais = new ByteArrayInputStream(payload);
		DataInputStream dis = new DataInputStream(bais);
		int type = 0, bufferSize = 0, x0 = 0, x1 = 0;
		String hash = null, ip = null;
		int udp = 0, maxSoundDistance = 64;
		boolean showVoicePlates = true, showPlayerIcons = true;
		try {
			showVoicePlates = dis.readBoolean();
			showPlayerIcons = dis.readBoolean();
			x0 = dis.readInt();
			x1 = dis.readInt();
			bufferSize = dis.readInt();
			maxSoundDistance = dis.readInt();
			type = dis.readInt();
			hash = dis.readUTF();
			udp = dis.readInt();
			ip = dis.readUTF();
		} catch (Exception e) {
			e.printStackTrace();
		}
		network.startClientNetwork(EnumVoiceNetworkType.values()[type], hash, ip, udp, maxSoundDistance, bufferSize, x0, x1, showVoicePlates, showPlayerIcons);
	}
}
