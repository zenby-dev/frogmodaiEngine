package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class KeyboardInput implements Event {
	public char key;
	public int keyCode;
	public KeyboardInput(char _key, int _keyCode) {
		key = _key;
		keyCode = _keyCode;
	}
}