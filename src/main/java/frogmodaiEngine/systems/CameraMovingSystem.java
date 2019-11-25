package frogmodaiEngine.systems;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.Chunk;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.components.*;
import frogmodaiEngine.events.CameraShift;
import frogmodaiEngine.events.ScreenRefreshRequest;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class CameraMovingSystem extends IteratingSystem {
	ComponentMapper<Position> mPosition;
	ComponentMapper<CameraWindow> mCameraWindow;
	
	EventSystem es;

	public CameraMovingSystem() {
		super(Aspect.all(Position.class, CameraWindow.class));
	}

	@Override
	protected void process(int e) {
		Position camPos = mPosition.create(e);
		CameraWindow camWindow = mCameraWindow.create(e);

		if (camWindow.focus == -1) {
			return;
		}

		Position focusPos = mPosition.create(camWindow.focus);
		//System.out.println(String.format("%d %d", focusPos.x, focusPos.y));

		//int focusMove = focusNearEdge(camWindow.tolerance, focusPos, camPos, camWindow);
		int atVertical = focusNearVertical(camWindow.tolerance, focusPos, camPos, camWindow);
		int atHorizontal = focusNearHorizontal(camWindow.tolerance, focusPos, camPos, camWindow);

		int dx = 0;
		int dy = 0;
		
		if (atVertical == -1) {
			dx = -1;
		} else if (atVertical == 1) {
			dx = 1;
		}
		
		if (atHorizontal == -1) {
			dy = -1;
		} else if (atHorizontal == 1) {
			dy = 1;
		}
		
		Position testPos = new Position();
		testPos.x = camPos.x + dx;
		testPos.y = camPos.y + dy;
		
		//int chunkMove = cameraNearEdge(0, testPos, camWindow);
//		int camVertical = cameraNearVertical(0, testPos, camWindow);
//		int camHorizontal = cameraNearHorizontal(0, testPos, camWindow);
//		if (camVertical == 0 && atVertical != 0) { //Camera would not move out of chunk
//			camPos.x += dx;
//		}
//		if (camHorizontal == 0 && atHorizontal != 0) { //Camera would not move out of chunk
//			camPos.y += dy;
//		}
		camPos.x += dx;
		camPos.y += dy;
		
		if (dx != 0 || dy != 0) {
			FrogmodaiEngine.logEventEmit("CameraMovingSystem", "CameraShift");
			es.dispatch(new CameraShift(dx, dy));
		}
			//FrogmodaiEngine.worldManager.triggerTileRedraw();

	}
	
	@Subscribe
	public void CameraShiftListener(CameraShift event) {
		FrogmodaiEngine.logEventReceive("CameraMovingSystem", "CameraShift");
		FrogmodaiEngine.logEventEmit("CameraMovingSystem", "ScreenRefreshRequest");
		es.dispatch(new ScreenRefreshRequest());
	}

	private int focusNearEdge(int tolerance, Position focus, Position cam, CameraWindow window) {
		if (focus.x - cam.x < tolerance)
			return 0;
		if (focus.y - cam.y < tolerance)
			return 1;
		if (focus.x - cam.x >= window.width - tolerance)
			return 2;
		if (focus.y - cam.y >= window.height - tolerance)
			return 3;
		return -1;
	}
	
	private int focusNearHorizontal(int tolerance, Position focus, Position cam, CameraWindow window) {
		//System.out.println(String.format("%d %d %d %d", focus.y, cam.y, focus.y - cam.y, tolerance));
		if (focus.y - cam.y < tolerance)
			return -1;
		if (focus.y - cam.y >= window.height - tolerance)
			return 1;
		return 0;
	}
	
	private int focusNearVertical(int tolerance, Position focus, Position cam, CameraWindow window) {
		if (focus.x - cam.x < tolerance)
			return -1;
		if (focus.x - cam.x >= window.width - tolerance)
			return 1;
		return 0;
	}

	private int cameraNearEdge(int tolerance, Position cam, CameraWindow window) {
		Chunk chunk = FrogmodaiEngine.worldManager.getActiveChunk();
		if (cam.x < tolerance) // left
			return 0;
		if (cam.y < tolerance) // top
			return 1;
		if (cam.x > chunk.width - window.width - tolerance)
			return 2;
		if (cam.y > chunk.height - window.height - tolerance)
			return 3;
		return -1;
	}
	
	private int cameraNearVertical(int tolerance, Position cam, CameraWindow window) {
		Chunk chunk = FrogmodaiEngine.worldManager.getActiveChunk();
		if (cam.x < tolerance) // left
			return -1;
		if (cam.x > chunk.width - window.width - tolerance)
			return 1;
		return 0;
	}
	
	private int cameraNearHorizontal(int tolerance, Position cam, CameraWindow window) {
		Chunk chunk = FrogmodaiEngine.worldManager.getActiveChunk();
		if (cam.y < tolerance) // left
			return -1;
		if (cam.y > chunk.height - window.height - tolerance)
			return 1;
		return 0;
	}

}
