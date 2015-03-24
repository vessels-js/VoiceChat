package net.gliby.voicechat.common;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import net.gliby.voicechat.VoiceChat;
import net.gliby.voicechat.client.VoiceChatClient;
import net.gliby.voicechat.client.gui.EnumUIPlacement;

import org.apache.logging.log4j.Level;
import org.lwjgl.util.vector.Vector3f;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.MetadataCollection.ArtifactVersionAdapter;
import cpw.mods.fml.common.versioning.ArtifactVersion;

/** Very basic GSON settings file that gets included with archive **/
public class ModPackSettings {

	public GVCModPackInstructions init() throws UnsupportedEncodingException {
		Reader reader = new InputStreamReader(getClass().getResourceAsStream("/modpack.json"), "UTF-8");
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(reader, GVCModPackInstructions.class);
	}

	public class GVCModPackInstructions {

		public UIContainer VOICE_PLATE;
		public UIContainer SPEAK_ICON;

		public boolean VOLUME_CONTROL, SHOW_PLATES, SHOW_PLAYER_ICONS;
		public float WORLD_VOLUME, UI_OPACITY;
		public int ID;

		@Override
		public String toString() {
			return "PLATES: " + VOICE_PLATE + ", SPEAK ICON: " + SPEAK_ICON + ", WORLD VOLUME: " + WORLD_VOLUME + ", UI OPACITY: " + UI_OPACITY + ", VOLUME CONTROL: " + VOLUME_CONTROL + ", PLATES ENABLED: " + SHOW_PLATES + ", ICONS ENABLED: " + SHOW_PLAYER_ICONS;
		}
	}

	public class UIContainer {
		public float X;
		public float Y;
		public float SCALE;
		public int TYPE;

		@Override
		public String toString() {
			return "X: " + X + ", Y: " + Y + ", SCALE: " + SCALE + ", TYPE: " + TYPE;
		}
	}
}
