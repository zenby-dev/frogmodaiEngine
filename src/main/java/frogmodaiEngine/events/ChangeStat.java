package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import frogmodaiEngine.FrogmodaiEngine;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class ChangeStat {
	public static void run(String name, int entity, int amount) {
		FrogmodaiEngine.worldManager.runEventSet("ChangeStat",
				new ChangeStat.Before(name, entity, amount),
				new ChangeStat.During(name, entity, amount),
				new ChangeStat.After(name, entity, amount));
	}
	
	public static class Before extends CancellableEvent {
		public String name;
		public int entity;
		public int amount;

		public Before(String _name, int _entity, int _amount) {
			name = _name;
			entity = _entity;
			amount = _amount;
		}
	}
	
	public static class During extends CancellableEvent {
		public String name;
		public int entity;
		public int amount;

		public During(String _name, int _entity, int _amount) {
			name = _name;
			entity = _entity;
			amount = _amount;
		}
	}
	
	public static class After implements Event {
		public String name;
		public int entity;
		public int amount;

		public After(String _name, int _entity, int _amount) {
			name = _name;
			entity = _entity;
			amount = _amount;
		}
	}

}