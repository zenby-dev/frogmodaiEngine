package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class CameraShift implements Event {
	public int dx;
	public int dy;
	public CameraShift(int _dx, int _dy) {
		dx = _dx;
		dy = _dy;
	}
}