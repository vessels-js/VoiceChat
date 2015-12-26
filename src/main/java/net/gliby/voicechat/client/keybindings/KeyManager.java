package net.gliby.voicechat.client.keybindings;

import java.util.ArrayList;
import java.util.List;

import net.gliby.voicechat.client.VoiceChatClient;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyManager {

	private final VoiceChatClient voiceChat;
	@SideOnly(Side.CLIENT)
	private final List<KeyEvent> keyEvents = new ArrayList<KeyEvent>();

	protected boolean[] keyDown;

	public KeyManager(VoiceChatClient voiceChat) {
		this.voiceChat = voiceChat;
	}

	@SideOnly(Side.CLIENT)
	public List<KeyEvent> getKeyEvents() {
		return keyEvents;
	}

	public String getKeyName(EnumBinding binding) {
		for (int i = 0; i < keyEvents.size(); i++) {
			final KeyEvent event = keyEvents.get(i);
			if (event.keyBind == binding) { return Keyboard.getKeyName(event.keyID); }
		}
		return null;
	}

	public void init() {
		keyEvents.add(new KeySpeakEvent(voiceChat, EnumBinding.SPEAK, Keyboard.KEY_V, false));
		keyEvents.add(new KeyOpenOptionsEvent(voiceChat, EnumBinding.OPEN_GUI_OPTIONS, Keyboard.KEY_PERIOD, false));
		registerKeyBindings();
		FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void keyEvent(KeyInputEvent event) {
		for (int i = 0; i < keyEvents.size(); i++) {
			final KeyEvent keyEvent = keyEvents.get(i);
			final KeyBinding keyBinding = keyEvents.get(i).forgeKeyBinding;
			final int keyCode = keyBinding.getKeyCode();
			final boolean state = (keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode));
			final boolean tickEnd = true;
			if (state != keyDown[i] || (state && keyEvent.repeating)) {
				if (state) {
					keyEvent.keyDown(keyBinding, tickEnd, state != keyDown[i]);
				} else {
					keyEvent.keyUp(keyBinding, tickEnd);
				}
				if (tickEnd) {
					keyDown[i] = state;
				}
			}
		}
	}

	private KeyBinding[] registerKeyBindings() {
		final KeyBinding keyBinding[] = new KeyBinding[keyEvents.size()];
		for (int i = 0; i < keyBinding.length; i++) {
			final KeyEvent keyEvent = this.keyEvents.get(i);
			keyBinding[i] = new KeyBinding(keyEvent.keyBind.name, keyEvent.keyID, "key.categories.multiplayer");
			this.keyDown = new boolean[keyBinding.length];
			keyEvent.forgeKeyBinding = keyBinding[i];
			ClientRegistry.registerKeyBinding(keyBinding[i]);
		}
		return keyBinding;
	}
}
