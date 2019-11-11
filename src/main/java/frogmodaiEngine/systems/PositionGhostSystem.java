package frogmodaiEngine.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Aspect.Builder;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.Chunk;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.PScreen;
import frogmodaiEngine.components.*;

public class PositionGhostSystem extends IteratingSystem {
	ComponentMapper<Position> mPosition;
	ComponentMapper<CameraWindow> mCameraWindow;
	
	PScreen screen;
	
	public PositionGhostSystem(PScreen _screen) {
		super(Aspect.all(IsPositionGhost.class, Position.class));
		screen = _screen;
	}

	@Override
	protected void process(int e) { //This could be adapted into targeting systems etc
		/*if (FrogmodaiEngine.cameraID == -1) return;
		CameraWindow camWindow = mCameraWindow.create(FrogmodaiEngine.cameraID);
		if (e != camWindow.focus) return;
		Position pos = mPosition.create(e);
		Position camPos = mPosition.create(FrogmodaiEngine.cameraID);
		if (FrogmodaiEngine.keystroke != null) {
			KeyStroke k = FrogmodaiEngine.keystroke;
			Chunk chunk = FrogmodaiEngine.worldManager.getActiveChunk();
			if (k.getKeyType() == KeyType.ArrowLeft && pos.x > 0) pos.x--;
			if (k.getKeyType() == KeyType.ArrowRight && pos.x < chunk.width - 1) pos.x++;
			if (k.getKeyType() == KeyType.ArrowUp && pos.y > 0) pos.y--;
			if (k.getKeyType() == KeyType.ArrowDown && pos.y < chunk.height - 1) pos.y++;
			
			//System.out.println(String.format("%d %d", screenPos.x, screenPos.y));
		}
		Position screenPos = new Position();
		screenPos.x = pos.x - camPos.x;
		screenPos.y = pos.y - camPos.y;
		screen.setCharacter(screenPos.x, screenPos.y, new TextCharacter('X', TextColor.ANSI.WHITE, TextColor.ANSI.BLACK, SGR.BOLD));
	*/
	}

}
