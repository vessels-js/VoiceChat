package net.gliby.gman;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.common.ModPackSettings.GVCModPackInstructions;
import net.minecraft.client.Minecraft;

import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GMan {

	public static void launchMod(final Logger logger, ModInfo modInfo, final String minecraftVersion, final String modVersion) {
		String url = "https://raw.githubusercontent.com/Gliby/Mod-Information-Storage/master/" + modInfo.modId + ".json";
		try {
			Gson gson = new Gson();
			Reader reader;
			try {
				reader = new InputStreamReader(new URL(url).openStream());
			} catch (Exception e) {
				logger.info("Failed to retrieve mod info, either mod doesn't exist or host(" + ") is down?");
				e.printStackTrace();
				return;
			}

			ModInfo externalInfo = gson.fromJson(reader, ModInfo.class);
			modInfo.donateURL = externalInfo.donateURL;
			modInfo.updateURL = externalInfo.updateURL;
			modInfo.versions = externalInfo.versions;
			modInfo.determineUpdate(modVersion, minecraftVersion);
			logger.info(modInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
