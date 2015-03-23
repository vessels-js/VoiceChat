package net.gliby.voicechat.common.networking.voiceservers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.gliby.voicechat.common.VoiceChatServer;
import net.gliby.voicechat.common.networking.PacketDispatcher;
import net.gliby.voicechat.common.networking.packets.PacketClientChunkVoiceSample;
import net.gliby.voicechat.common.networking.packets.PacketClientEntityPosition;
import net.gliby.voicechat.common.networking.packets.PacketClientVoiceEnd;
import net.gliby.voicechat.common.networking.packets.PacketClientVoiceSample;
import net.minecraft.entity.player.EntityPlayerMP;

public class MinecraftVoiceServer extends VoiceServer {

	private VoiceChatServer voiceChat;

	public MinecraftVoiceServer(VoiceChatServer voiceChat) {
		this.voiceChat = voiceChat;
	}

	@Override
	public EnumVoiceNetworkType getType() {
		return EnumVoiceNetworkType.MINECRAFT;
	}

	@Override
	public void handleVoiceData(EntityPlayerMP player, byte[] data, byte divider, int id, boolean end) {
		voiceChat.getServerNetwork().getDataManager().addQueue(player, data, divider, id, end);
	}

	@Override
	public void sendChunkVoiceData(EntityPlayerMP player, int entityID, boolean direct, byte[] samples, byte chunkSize) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
		DataOutputStream outputStream = new DataOutputStream(bos);
		if (samples != null) {
			try {
				outputStream.writeInt(samples.length);
				for (int i = 0; i < samples.length; i++) {
					outputStream.writeByte(samples[i]);
				}
				outputStream.writeByte(chunkSize);
				outputStream.writeInt(entityID);
				outputStream.writeBoolean(direct);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			PacketDispatcher.sendPacketToPlayer(new PacketClientChunkVoiceSample(bos.toByteArray()), player);
		}
	}

	@Override
	public void sendEntityPosition(EntityPlayerMP player, int entityID, double x, double y, double z) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(entityID);
			outputStream.writeDouble(x);
			outputStream.writeDouble(y);
			outputStream.writeDouble(z);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		PacketDispatcher.sendPacketToPlayer(new PacketClientEntityPosition(bos.toByteArray()), player);
	}

	@Override
	public void sendVoiceData(EntityPlayerMP player, int entityID, boolean global, byte[] samples) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
		DataOutputStream outputStream = new DataOutputStream(bos);
		if (samples != null) {
			try {
				outputStream.writeInt(samples.length);
				for (int i = 0; i < samples.length; i++) {
					outputStream.writeByte(samples[i]);
				}
				outputStream.writeInt(entityID);
				outputStream.writeBoolean(global);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			PacketDispatcher.sendPacketToPlayer(new PacketClientVoiceSample(bos.toByteArray()), player);
		}
	}

	@Override
	public void sendVoiceEnd(EntityPlayerMP player, int id) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			outputStream.writeInt(id);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		PacketDispatcher.sendPacketToPlayer(new PacketClientVoiceEnd(bos.toByteArray()), player);
	}

	@Override
	public boolean start() {
		voiceChat.getLogger().warn("Minecraft Networking is not recommended and is consider very slow, please setup UDP.");
		return true;
	}

	@Override
	public void stop() {
	}
}
