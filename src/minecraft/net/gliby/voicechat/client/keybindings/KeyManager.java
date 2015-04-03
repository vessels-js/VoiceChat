package net.gliby.voicechat.client.keybindings;

import java.util.ArrayList;
import java.util.List;

import net.gliby.voicechat.client.VoiceChatClient;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//TODO rework keymanager
@SideOnly(Side.CLIENT)
public class KeyManager {


	private VoiceChatClient voiceChat;
	public KeyManager(VoiceChatClient voiceChat) {
		this.voiceChat = voiceChat;
	}
	
	@SideOnly(Side.CLIENT)
	private List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();
	
	@SideOnly(Side.CLIENT)
	public List<KeyEvent> getKeyEvents() {
		return keyEvents;
	}

	public void init() {
		keyEvents.add(new KeySpeakEvent(voiceChat, EnumBinding.SPEAK, Keyboard.KEY_V, false));
		keyEvents.add(new KeyGuiOptionsEvent(voiceChat, EnumBinding.OPEN_GUI_OPTIONS, Keyboard.KEY_PERIOD, false));
		KeyBindingRegistry.registerKeyBinding(new KeyBindingHandler(compileKeyBindings(), compileRepeating(), this));
	}
	
	private boolean[] compileRepeating() {
		boolean keyRepeating[] = new boolean[keyEvents.size()];
		for(int i = 0; i < keyRepeating.length; i++) {
			KeyEvent keyEvent = this.keyEvents.get(i);
			keyRepeating[i] = keyEvent.repeating;
		}
		return keyRepeating;
	}

	private KeyBinding[] compileKeyBindings() {
		KeyBinding keyBinding[] = new KeyBinding[keyEvents.size()];
		for(int i = 0; i < keyBinding.length; i++) {
			KeyEvent keyEvent = this.keyEvents.get(i);
			keyBinding[i] = new KeyBinding(keyEvent.keyBind.name, keyEvent.keyID);
		}
		return keyBinding;
	}
	
	public String getKeyName(EnumBinding binding) {
		for(int i = 0; i < keyEvents.size(); i++) {
			KeyEvent event = keyEvents.get(i);
			if(event.keyBind == binding) {
				return Keyboard.getKeyName(event.keyID);
			}
		}
		return null;
	}

}