package frogmodaiEngine;

import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class CancellableEvent implements Event, Cancellable {
	private boolean cancelled;
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean value) { cancelled = value; }
}
