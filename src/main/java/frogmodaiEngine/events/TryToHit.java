package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import frogmodaiEngine.FrogmodaiEngine;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class TryToHit {
	public static void run(int entity, int target) {
		FrogmodaiEngine.worldManager.runEventSet("TryToHit",
				new TryToHit.Before(entity, target),
				new TryToHit.During(entity, target),
				new TryToHit.After(entity, target));
	}
	
	public static class Before extends CancellableEvent {
		public int entity;
		public int target;

		public Before(int _entity, int _target) {
			entity = _entity;
			target = _target;
		}
	}
	
	public static class During extends CancellableEvent {
		public int entity;
		public int target;

		public During(int _entity, int _target) {
			entity = _entity;
			target = _target;
		}
	}
	
	public static class After implements Event {
		public int entity;
		public int target;

		public After(int _entity, int _target) {
			entity = _entity;
			target = _target;
		}
	}

}