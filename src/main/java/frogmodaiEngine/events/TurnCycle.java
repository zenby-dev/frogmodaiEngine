package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import frogmodaiEngine.FrogmodaiEngine;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class TurnCycle {
	public static void run(int entity, int dx, int dy) {
		/*FFMain.worldManager.runEventSet(new TurnCycle.Before(),
				new TurnCycle.During(entity, dx, dy),
				new TurnCycle.After(entity, dx, dy));*/
	}
	
	public static class Before extends CancellableEvent {
		public Before() {
		}
	}
	
	public static class During extends CancellableEvent {
		public During() {
		}
	}
	
	public static class After implements Event {
		public After() {
		}
	}

}