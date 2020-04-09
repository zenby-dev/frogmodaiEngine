package frogmodaiEngine.events;

import frogmodaiEngine.CancellableEvent;
import frogmodaiEngine.FrogmodaiEngine;
import net.mostlyoriginal.api.event.common.Cancellable;
import net.mostlyoriginal.api.event.common.Event;

public class CameraShift {
	public static void run(int dx, int dy) {
		FrogmodaiEngine.worldManager.runEventSet("CameraShift",
				new CameraShift.Before(dx, dy),
				new CameraShift.During(dx, dy),
				new CameraShift.After(dx, dy));
	}
	
	public static class Before extends CancellableEvent {
		public int dx;
		public int dy;

		public Before(int _dx, int _dy) {
			dx = _dx;
			dy = _dy;
		}
	}
	
	public static class During extends CancellableEvent {
		public int dx;
		public int dy;

		public During(int _dx, int _dy) {
			dx = _dx;
			dy = _dy;
		}
	}
	
	public static class After implements Event {
		public int dx;
		public int dy;

		public After(int _dx, int _dy) {
			dx = _dx;
			dy = _dy;
		}
	}

}