package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import frogmodaiEngine.FrogmodaiEngine;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class HPAtZero {
	
	public static void run(int entity) {
		FrogmodaiEngine.worldManager.runEventSet("HPAtZero",
				new HPAtZero.Before(entity),
				new HPAtZero.During(entity),
				new HPAtZero.After(entity));
	}
	
	public static class Before extends CancellableEvent {
		public int entity;

		public Before(int _entity) {
			entity = _entity;
		}
	}
	
	public static class During extends CancellableEvent {
		public int entity;

		public During(int _entity) {
			entity = _entity;
		}
	}
	
	public static class After implements Event {
		public int entity;

		public After(int _entity) {
			entity = _entity;
		}
	}

}