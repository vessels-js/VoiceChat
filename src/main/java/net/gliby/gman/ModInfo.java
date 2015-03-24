package net.gliby.gman;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModInfo {

	@SerializedName("DonateURL")
	public String donateURL;

	@SerializedName("UpdateURL")
	public String updateURL;

	@SerializedName("Versions")
	public List<String> versions;

	boolean updated = true;

	public final String modId;

	public ModInfo(String modId, String updateURL) {
		this.updateURL = updateURL;
		donateURL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=PBXHJ67N62ZRW";
		this.modId = modId;
	}

	public ModInfo() {
		this.modId = "NULL";
	}

	public final String getUpdateSite() {
		return updateURL;
	}

	public final boolean isUpdated() {
		return updated;
	}

	public final boolean updateNeeded() {
		return !updated;
	}

	public void determineUpdate(String currentModVersion, String currentMinecraftVersion) {
		for (String s : versions) {
			if (s.startsWith(currentMinecraftVersion)) {
				updated = s.split(":")[1].trim().equals(currentModVersion);
				break;
			}
		}
	}

	@Override
	public String toString() {
		String version = "";
		for (String s : versions) {
			version += " " + s;
		}
		return "[" + modId + "] Updated :" + updated + " Donate URL: " + donateURL + ", Update URL " + updateURL + ", Versions (" + version + ")";

	}
}
