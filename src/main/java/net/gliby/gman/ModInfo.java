package net.gliby.gman;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModInfo {

	public String donateURL;
	public String updateURL;
	boolean upToDate;
	public String modId;
	public String otherStuff;
	public ModInfo(String modId) {
		upToDate = true;
		donateURL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=PBXHJ67N62ZRW";
		this.modId = modId;
	}

	public final String getUpdateSite() {
		return updateURL;
	}

	public final boolean updateNeeded() {
		return !upToDate;
	}
}
