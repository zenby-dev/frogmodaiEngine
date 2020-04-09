package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import frogmodaiEngine.FrogmodaiEngine;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class ActorTakeTurn {
	public static void run(int entity) {
		//FrogmodaiEngine.logEventEmit("???", "ActorTakeTurn");
		FrogmodaiEngine.worldManager.runEventSet("ActorTakeTurn",
				new ActorTakeTurn.Before(entity),
				new ActorTakeTurn.During(entity),
				new ActorTakeTurn.After(entity));
	}
	
	public static class Before extends CancellableEvent {
		public int entity;
		public int actionCost=0;
		public boolean passing=true;

		public Before(int _entity) {
			entity = _entity;
		}
	}
	
	public static class During extends CancellableEvent {
		public int entity;
		public int actionCost=0;
		public boolean passing=true;

		public During(int _entity) {
			entity = _entity;
		}
	}
	
	public static class After implements Event {
		public int entity;
		public int actionCost=0;
		public boolean passing=true;

		public After(int _entity) {
			entity = _entity;
		}
	}

}