package net.gliby.voicechat.client;

import java.io.File;
import java.io.UnsupportedEncodingException;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.device.Device;
import net.gliby.voicechat.client.device.DeviceHandler;
import net.gliby.voicechat.client.gui.EnumUIPlacement;
import net.gliby.voicechat.client.gui.UIPosition;
import net.gliby.voicechat.common.MathUtility;
import net.gliby.voicechat.common.ModPackSettings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TODO NEXT-UPDATE UI Placement gets messed up sometimes.
// TODO NEXT-UPDATE Implement proper settings, that can be altered/added dynamically + reset functions for individual
// settings.
@SideOnly(Side.CLIENT)
public class Settings {
	private final DeviceHandler deviceHandler;
	private boolean debugMode;
	private Device inputDevice;
	private float worldVolume = 1.0f;
	private float inputBoost = 0.0f;
	private float uiOpacity = 1.0f;
	private int speakMode = 0;
	private int encodingMode = 0;
	private int minimumQuality = 0, maximumQuality = 10;
	private float encodingQuality = 0.6f;
	private UIPosition uiPositionSpeak, uiPositionPlate;
	private boolean perceptualEnchantment = true, setupNeeded, snooperEnabled = false, volumeControl = true;
	private int maxSoundDistance = 63;
	private boolean voicePlatesAllowed = true;
	private boolean voiceIconsAllowed = true;
	private int bufferSize = 144;
	private int modPackId = 1;
	Configuration configuration;

	public Settings(File file) {
		deviceHandler = new DeviceHandler();
		configuration = new Configuration(this, file);
		uiPositionSpeak = new UIPosition(EnumUIPlacement.SPEAK, EnumUIPlacement.SPEAK.x, EnumUIPlacement.SPEAK.y, EnumUIPlacement.SPEAK.positionType, 1.0f);
		uiPositionPlate = new UIPosition(EnumUIPlacement.VOICE_PLATES, EnumUIPlacement.VOICE_PLATES.x, EnumUIPlacement.VOICE_PLATES.y, EnumUIPlacement.VOICE_PLATES.positionType, 1.0f);
	}

	public final int getBufferSize() {
		return bufferSize;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public DeviceHandler getDeviceHandler() {
		return deviceHandler;
	}

	public int getEncodingMode() {
		return (int) MathUtility.clamp(encodingMode, 0, 2);
	}

	public String getEncodingModeString() {
		String s = "Narrowband";
		switch (encodingMode) {
		case 0:
			s = "Narrowband";
			break;
		case 1:
			s = "Wideband";
			break;
		case 2:
			s = "Ultrawideband";
			break;
		}
		return s;
	}

	public final float getEncodingQuality() {
		return MathUtility.clamp(encodingQuality, 0, 1);
	}

	public final float getInputBoost() {
		return inputBoost;
	}

	public Device getInputDevice() {
		if (inputDevice == null) inputDevice = deviceHandler.getDefaultDevice();
		return inputDevice;
	}

	public final int getMaximumQuality() {
		return maximumQuality;
	}

	/** Frustum culling is limited to vanilla, so we have an artificial limit of renderable voice icons at a given time. **/
	public final int getMaximumRenderableVoiceIcons() {
		return 20;
	}

	public final int getMinimumQuality() {
		return minimumQuality;
	}

	public final int getModPackID() {
		return modPackId;
	}

	public final int getSoundDistance() {
		return maxSoundDistance;
	}

	public final int getSpeakMode() {
		return speakMode;
	}

	public float getUIOpacity() {
		return uiOpacity;
	}

	public final UIPosition getUIPositionPlate() {
		return uiPositionPlate;
	}

	public final UIPosition getUIPositionSpeak() {
		return uiPositionSpeak;
	}

	public float getWorldVolume() {
		return worldVolume;
	}

	public void init() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				deviceHandler.loadDevices();
				configuration.init(deviceHandler);
				final ModPackSettings settings = new ModPackSettings();
				try {
					final ModPackSettings.GVCModPackInstructions newDefault = settings.init();
					if (newDefault.ID != getModPackID()) {
						VoiceChat.getLogger().info("Modpack defaults applied, original settings overwritten.");
						uiPositionSpeak = new UIPosition(EnumUIPlacement.SPEAK, newDefault.SPEAK_ICON.X, newDefault.SPEAK_ICON.Y, newDefault.SPEAK_ICON.TYPE, newDefault.SPEAK_ICON.SCALE);
						uiPositionPlate = new UIPosition(EnumUIPlacement.VOICE_PLATES, newDefault.VOICE_PLATE.X, newDefault.VOICE_PLATE.Y, newDefault.VOICE_PLATE.TYPE, newDefault.VOICE_PLATE.SCALE);
						setWorldVolume(newDefault.WORLD_VOLUME);
						setUIOpacity(newDefault.UI_OPACITY);
						setVolumeControl(newDefault.VOLUME_CONTROL);
						setVoicePlatesAllowed(newDefault.SHOW_PLATES);
						setVoiceIconsAllowed(newDefault.SHOW_PLAYER_ICONS);
						setModPackID(newDefault.ID);
						configuration.save();
					}
				} catch (final UnsupportedEncodingException e) {
					e.printStackTrace();
				}

			}
		}, "Settings Process").start();

	}

	public final boolean isDebug() {
		return debugMode;
	}

	public final boolean isPerceptualEnchantmentAllowed() {
		return perceptualEnchantment;
	}

	public final boolean isSetupNeeded() {
		return setupNeeded;
	}

	public final boolean isSnooperAllowed() {
		return snooperEnabled;
	}

	public final boolean isVoiceIconAllowed() {
		return voiceIconsAllowed;
	}

	public final boolean isVoicePlateAllowed() {
		return voicePlatesAllowed;
	}

	public final boolean isVolumeControlled() {
		return volumeControl;
	}

	public void resetQuality() {
		minimumQuality = 0;
		maximumQuality = 10;
	}

	public void resetUI(int width, int height) {
		uiPositionSpeak.type = uiPositionSpeak.info.positionType;
		uiPositionSpeak.x = uiPositionSpeak.info.x;
		uiPositionSpeak.y = uiPositionSpeak.info.y;
		uiPositionSpeak.scale = 1.0f;
		uiPositionPlate.type = uiPositionPlate.info.positionType;
		uiPositionPlate.x = uiPositionPlate.info.x;
		uiPositionPlate.y = uiPositionPlate.info.y;
		uiPositionPlate.scale = 1.0f;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void setDebug(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public void setEncodingMode(int encodingMode) {
		this.encodingMode = encodingMode;
	}

	public void setEncodingQuality(float encodingQuality) {
		this.encodingQuality = encodingQuality;
	}

	public void setInputBoost(float inputBoost) {
		this.inputBoost = inputBoost;
	}

	public void setInputDevice(Device loadedDevice) {
		this.inputDevice = loadedDevice;
	}

	public void setModPackID(int modPackId) {
		this.modPackId = modPackId;
	}

	public void setNetworkQuality(int soundQualityMin, int soundQualityMax) {
		this.minimumQuality = soundQualityMin;
		this.maximumQuality = soundQualityMax;
	}

	public void setPerceptualEnchantment(boolean perceptualEnchantment) {
		this.perceptualEnchantment = perceptualEnchantment;
	}

	public void setSetupNeeded(boolean setupNeeded) {
		this.setupNeeded = setupNeeded;
	}

	public void setSnooperAllowed(boolean b) {
		this.snooperEnabled = b;
	}

	public void setSoundDistance(int soundDist) {
		this.maxSoundDistance = soundDist;
	}

	public void setSpeakMode(int speakMode) {
		this.speakMode = speakMode;
	}

	public void setUIOpacity(float chatIconOpacity) {
		this.uiOpacity = chatIconOpacity;
	}

	public void setUIPosition(EnumUIPlacement placement, float x, float y, float scale, int type) {
		if (placement == EnumUIPlacement.SPEAK) uiPositionSpeak = new UIPosition(placement, x, y, type, scale);
		if (placement == EnumUIPlacement.VOICE_PLATES) uiPositionPlate = new UIPosition(placement, x, y, type, scale);
	}

	public final void setVoiceIconsAllowed(boolean voiceIconsAllowed) {
		this.voiceIconsAllowed = voiceIconsAllowed;
	}

	public final void setVoicePlatesAllowed(boolean voicePlatesAllowed) {
		this.voicePlatesAllowed = voicePlatesAllowed;
	}

	public void setVolumeControl(boolean volumeControl) {
		this.volumeControl = volumeControl;
	}

	public void setWorldVolume(float worldVolume) {
		this.worldVolume = worldVolume;
	}

}
