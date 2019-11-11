package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class ActorDied implements Event {
	public int entity;
	public ActorDied(int _entity) {
		entity = _entity;
	}
}