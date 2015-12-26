package net.gliby.voicechat.client.networking.voiceclients;

import net.gliby.voicechat.common.networking.voiceservers.EnumVoiceNetworkType;

public abstract class VoiceAuthenticatedClient extends VoiceClient {

	private boolean connected;

	public boolean authed;
	public final String hash;

	public VoiceAuthenticatedClient(EnumVoiceNetworkType enumVoiceServer, String hash) {
		super(enumVoiceServer);
		this.hash = hash;
	}

	public abstract void autheticate();

	public final String getHash() {
		return hash;
	}

	public boolean isAuthed() {
		return authed;
	}

	public final boolean isConnected() {
		return connected;
	}

	public void setAuthed(boolean authed) {
		this.authed = authed;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
}
