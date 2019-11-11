package frogmodaiEngine.systems;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.*;
import frogmodaiEngine.components.*;

public class ItemRenderingSystem extends IteratingSystem {
	ComponentMapper<Position> mPosition;
	ComponentMapper<Char> mChar;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<CameraWindow> mCameraWindow;
	ComponentMapper<Sight> mSight;

	PScreen screen;
	public int perspective = -1;

	public ItemRenderingSystem(PScreen _screen) {
		super(Aspect.all(Position.class, Char.class, ChunkAddress.class).exclude(Tile.class, TimedActor.class,
				IsInContainer.class));
		screen = _screen;
	}

	@Override
	protected void process(int e) {
//		Position pos = mPosition.create(e);
//		Char character = mChar.create(e);
//		ChunkAddress chunkAddress = mChunkAddress.create(e);
//		Chunk chunk = FFMain.worldManager.getChunk(chunkAddress.worldID);
//
//		Position camPos = mPosition.create(FFMain.cameraID);
//		CameraWindow camWindow = mCameraWindow.create(FFMain.cameraID);
//
//		if (chunkAddress.worldID == FFMain.worldManager.activeChunk) {
//			Position screenPos = new Position();
//			screenPos.x = pos.x - camPos.x;
//			screenPos.y = pos.y - camPos.y;
//			if (screenPos.x >= 0 && screenPos.y >= 0 && screenPos.x < camWindow.width && screenPos.y < camWindow.height) {
//				//if (FFMain.playerID == -1 || FFMain.playerID == e) {
//				if (perspective == -1) {
//					screen.setCharacter(screenPos.x, screenPos.y, character.getTextCharacter());
//				} else {
//					Position playerPos = mPosition.create(perspective);
//					Sight sight = mSight.create(perspective);
//					if (pos.withinDistance(playerPos, sight.distance)) {
//						if (FFMain.worldManager.getActiveChunk().LOS(playerPos.x, playerPos.y, pos.x, pos.y)) {
//							TextCharacter ct = character.getTextCharacter();
//							int tile = chunk.getTile(playerPos.x, playerPos.y);
//							Char tileChar = mChar.create(tile);
//							screen.setCharacter(screenPos.x, screenPos.y, ct.withBackgroundColor(TextColor.ANSI.values()[tileChar.bgc]));
//						}
//					}
//				}
//			}
//		}
	}

}
