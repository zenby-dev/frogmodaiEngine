package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import frogmodaiEngine.FrogmodaiEngine;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class ProcessIntermediate {
	public static void run() {
		FrogmodaiEngine.worldManager.runEventSet(new ProcessIntermediate.Before(),
				new ProcessIntermediate.During(),
				new ProcessIntermediate.After());
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