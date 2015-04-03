package net.gliby.voicechat.client.keybindings;

import java.util.EnumSet;

import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeyBindingHandler extends KeyHandler {

	KeyManager keyManager;
	public KeyBindingHandler(KeyBinding[] keyBindings, boolean[] repeating, KeyManager manager) {
		super(keyBindings, repeating);
		this.keyManager = manager;
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		for(int i = 0; i < keyManager.getKeyEvents().size(); i++) {
			KeyEvent keyEvent = keyManager.getKeyEvents().get(i);
			if(keyEvent.keyBind.name.equals(kb.keyDescription) && tickEnd)
				keyEvent.keyDown(kb, tickEnd, isRepeat);
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		for(int i = 0; i < keyManager.getKeyEvents().size(); i++) {
			KeyEvent keyEvent = keyManager.getKeyEvents().get(i);
			if(keyEvent.keyBind.name.equals(kb.keyDescription) && tickEnd)
				keyEvent.keyUp(kb, tickEnd);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}

