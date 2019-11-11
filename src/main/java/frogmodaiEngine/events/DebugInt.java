package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class DebugInt implements Event {
	public int value1;
	public int value2;
	public DebugInt(int v1) {
		value1 = v1;
	}
	public DebugInt(int v1, int v2) {
		value1 = v1;
		value2 = v2;
	}
}