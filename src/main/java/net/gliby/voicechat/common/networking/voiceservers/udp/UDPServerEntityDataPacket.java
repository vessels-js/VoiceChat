package net.gliby.voicechat.common.networking.voiceservers.udp;

import com.google.common.io.ByteArrayDataOutput;

public class UDPServerEntityDataPacket extends UDPPacket {

	public int entityId;
	public String name;
	public double x, y, z;

	public UDPServerEntityDataPacket(String name, int entityId, double x, double y, double z) {
		this.name = name;
		this.entityId = entityId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public byte id() {
		return 3;
	}

	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeInt(entityId);
		out.writeUTF(name);
		out.writeDouble(x);
		out.writeDouble(y);
		out.writeDouble(z);
	}

}
