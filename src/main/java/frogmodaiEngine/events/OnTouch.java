package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class OnTouch implements Event {
	public int entity;
	public int neighbor;
	public OnTouch(int _entity, int _neighbor) {
		entity = _entity;
		neighbor = _neighbor;
	}
}