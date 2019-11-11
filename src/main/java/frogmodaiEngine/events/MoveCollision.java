package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class MoveCollision implements Event {
	public int entity;
	public int neighborTile;
	public MoveCollision(int _entity, int _neighborTile) {
		entity = _entity;
		neighborTile = _neighborTile;
	}
}