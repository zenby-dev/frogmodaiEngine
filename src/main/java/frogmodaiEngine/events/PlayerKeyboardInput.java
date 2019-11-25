package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class PlayerKeyboardInput implements Event {
	public char key;
	public int keyCode;
	public PlayerKeyboardInput(char _key, int _keyCode) {
		key = _key;
		keyCode = _keyCode;
	}
}