package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class ActorTakeTurn implements Event {
	public int entity;
	public int actionCost=0;
	public boolean passing=true;
	public ActorTakeTurn(int _entity) {
		entity = _entity;
	}
}