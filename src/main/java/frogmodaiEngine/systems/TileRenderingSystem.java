package frogmodaiEngine.systems;

import java.util.ArrayList;
import java.util.HashMap;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.Chunk;
import frogmodaiEngine.ColorUtils;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.PScreen;
import frogmodaiEngine.PTerminal;
import frogmodaiEngine.PTile;
import frogmodaiEngine.components.*;
import frogmodaiEngine.events.CameraShift;
import frogmodaiEngine.events.PostTileRendering;
import frogmodaiEngine.events.ProcessIntermediate;
import frogmodaiEngine.events.TileOccupationFinished;
import frogmodaiEngine.events.TileRenderingFinished;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class TileRenderingSystem extends BaseSystem { // This is for terrain only
	ComponentMapper<Tile> mTile;
	ComponentMapper<Position> mPosition;
	ComponentMapper<Char> mChar;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<CameraWindow> mCameraWindow;
	ComponentMapper<Sight> mSight;
	ComponentMapper<IsItem> mIsItem;
	ComponentMapper<TimedActor> mTimedActor;
	ComponentMapper<IsDead> mIsDead;
	ComponentMapper<IsPlayer> mIsPlayer;

	EventSystem es;

	PScreen screen;
	@EntityId
	public int perspective = -1;
	boolean fullRedraw = false;
	PTile emptyCharacter;
	double PI = 3.14159265;

	public boolean drewThisFrame = false;

	PScreen buffer;
	FrogmodaiEngine _p;

	public TileRenderingSystem(FrogmodaiEngine __p, PScreen _screen) { // Matches camera, not tiles, for performance
		//super(Aspect.all(Position.class, CameraWindow.class));
		super();
		_p = __p;
		screen = _screen;
		//System.out.printf("%d, %d\n", FrogmodaiEngine.screenWidth / 2, FrogmodaiEngine.screenHeight);
		// buffer = new ScreenBuffer(new TerminalSize(FrogmodaiEngine.screenWidth / 2,
		// FrogmodaiEngine.screenHeight),
		// new PTile(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK));
		buffer = new PScreen(_p, new PTerminal(_p, _p.screenWidth, _p.screenHeight));
		// emptyCharacter = new TextCharacter('X', TextColor.ANSI.YELLOW,
		// TextColor.ANSI.BLUE);
		emptyCharacter = new PTile(ColorUtils.emptyTile);
	}

	public void triggerRedraw() {
		fullRedraw = true;
	}
	
	@Override
	protected void processSystem() {
		/*drewThisFrame = false;
		if (fullRedraw) {
			if (_p.cameraID != -1) {
				FrogmodaiEngine.log("TileRenderingSystem.process (backup call)");
				process(_p.cameraID);
			}
		}*/
	}
	
	@Subscribe
	public void TileOccupationFinishedListener(TileOccupationFinished event) {
		drewThisFrame = false;
		FrogmodaiEngine.logEventReceive("TileRenderingSystem", "TileOccupationFinished");
		doCycle();
	}
	
	@Subscribe
	public void ProcessIntermediateAfterListener(ProcessIntermediate.After event) {
		if (!fullRedraw) return;
		FrogmodaiEngine.logEventReceive("TileRenderingSystem", "ProcessIntermediate.After");
		doCycle();
	}
	
	private void doCycle() {
		if (_p.cameraID != -1) {
			process(_p.cameraID);
		}
		FrogmodaiEngine.logEventEmit("TileRenderingSystem", "TileRenderingFinished");
		FrogmodaiEngine.dispatch(new TileRenderingFinished());
	}

	protected void process(int e) { // this happens with high frequency

		drewThisFrame = false;

		if (!fullRedraw)
			return;

		Position camPos = mPosition.create(e);
		CameraWindow camWindow = mCameraWindow.create(e);
		
		FrogmodaiEngine.log(String.format("TileRenderingSystem.process(camWindow.focus=%d)", camWindow.focus));
		
		if (camWindow.focus == -1)
			return;
		Position focusPos = mPosition.create(camWindow.focus); // It's possible for focusPos to be from a different
																// chunk than "chunk"
		Chunk chunk = FrogmodaiEngine.worldManager.getActiveChunk(); // getActiveChunk() sometimes returns a chunk the
																		// player is
		// not in
		Sight sight = mSight.create(perspective);

		HashMap<String, RelativePosition> vision = sight.visibleTiles;
		// HashMap<String, RelativePosition> vision = new HashMap<String,
		// RelativePosition>();
		// chunk.floodGrab(focusPos, sight.distance, vision);

		// This new method is also not the way
		// This grabs all tiles that could be seen
		// But unseen tiles in the chunk as determined
		// by the camera window should be drawn underneath
		// Also need to remember to accomodate for when perspective is -1
		// TODO: test by making a small chunk that is a torus
		// TODO: change the movement system to be based on local tile neighbors
		// TODO: By the palyer's hanging inTile reference, check if they've moved out of
		// the active chunk, if so, SWTICH active chunk

		clearBuffer();

		// memoryDraw(camPos, camWindow);
		// drawLocal(vision, sight.distance, camPos, camWindow);
		olddrawLocal(vision, camPos, camWindow);

		/*
		 * TextGraphics tg = screen.newTextGraphics(); tg.fillRectangle(new
		 * TerminalPosition(0, 0), new TerminalSize(FrogmodaiEngine.screenWidth / 2,
		 * FrogmodaiEngine.screenHeight), emptyCharacter); tg.drawImage(new
		 * TerminalPosition(0, 0), buffer);
		 */
		drawToScreen();

		fullRedraw = false;
		drewThisFrame = true;

		FrogmodaiEngine.logEventEmit("TileRenderingSystem", "PostTileRendering");
		FrogmodaiEngine.dispatch(new PostTileRendering());

		// FrogmodaiEngine.worldManager.mapLoader.drawMap();

	}

	public void drawToScreen() {
		screen.fill(emptyCharacter);
		screen.noStroke();
		screen.rect(0, 0, screen.terminal.resX/2, screen.terminal.resY);
		screen.image(buffer, 0, 0);
	}

	@Subscribe
	public void CameraShiftAfterListener(CameraShift.After event) {
		// FrogmodaiEngine.sendMessage(event.dx + ", " + event.dy);
	}

	private void clearBuffer() {
		// buffer.newTextGraphics().fillRectangle(new TerminalPosition(0, 0),
		// buffer.getSize(), emptyCharacter);
		buffer.fill(emptyCharacter);
		buffer.noStroke();
		buffer.rect(0, 0, buffer.terminal.resX, buffer.terminal.resY);
	}

	private void setCharacter(int x, int y, PTile tile) {
		buffer.set(x, y, tile);
	}

	private void drawLocal(HashMap<String, RelativePosition> vision, int viewDistance, Position camPos,
			CameraWindow camWindow) {
		// Right now this just goes through ALL the tiles grabbed a relative process
		// So no more relativeness SHOULD NEED TO BE DONE!!!
		// Instead, we should do a traditional LOS kinda
		// but change vision to be a hashmap of tile IDs indexed by relative positions
		// (which include ID anyways)
		// Then have a loop through the edge of a circle of radius=sight.distance
		// and it should draw every character along the way, from center out
		// to avoid retracing rays.
		// Do findLine,

		Position playerPos = mPosition.create(perspective);
		Sight sight = mSight.create(perspective);
		ChunkAddress playerChunkAddress = mChunkAddress.create(perspective);
		Chunk playerChunk = FrogmodaiEngine.worldManager.getChunk(playerChunkAddress.worldID);

		ArrayList<String> circle = new ArrayList<String>();
		int angleTicks = 100;
		for (int angle = 0; angle < angleTicks; angle++) {
			int x = (int) (viewDistance * Math.cos((angle * 1.0 / angleTicks) * PI * 2));
			int y = (int) (viewDistance * Math.sin((angle * 1.0 / angleTicks) * PI * 2));
			// x and y are circular offsets from the player's position
			// Check if this point in the circle has been looked at already
			if (!circle.contains(x + "|" + y)) {
				circle.add(x + "|" + y);

				// && vision.containsKey((x+playerPos.x)+"|"+(y+playerPos.y))

				Position screenPos = new Position();
				screenPos.x = x - camPos.x;
				screenPos.y = y - camPos.y;

				// Start is player position, end is player position and the circular offset
				// FrogmodaiEngine.worldManager.LOS(playerChunk, playerPos.x, playerPos.y, x +
				// playerPos.x, y + playerPos.y,
				// camPos.x, camPos.y, buffer);
			}
		}

		// To fill in gaps
		/*
		 * for (int angle=0; angle<angleTicks; angle++) { int x = (int) (viewDistance-1
		 * * Math.cos((angle*1.0/angleTicks)*PI*2)); int y = (int) (viewDistance-1 *
		 * Math.sin((angle*1.0/angleTicks)*PI*2)); //x and y are circular offsets from
		 * the player's position //Check if this point in the circle has been looked at
		 * already if (!circle.contains(x+"|"+y)) { circle.add(x+"|"+y);
		 * 
		 * //&& vision.containsKey((x+playerPos.x)+"|"+(y+playerPos.y))
		 * 
		 * Position screenPos = new Position(); screenPos.x = x - camPos.x; screenPos.y
		 * = y - camPos.y;
		 * 
		 * //Start is player position, end is player position and the circular offset
		 * FrogmodaiEngine.worldManager.LOS(playerChunk, playerPos.x, playerPos.y,
		 * x+playerPos.x, y+playerPos.y, camPos.x, camPos.y, buffer); } }
		 */

		for (RelativePosition rel : vision.values()) {
			Tile tile = mTile.create(rel.e);
			Char character = mChar.create(rel.e);
			Position screenPos = new Position();
			screenPos.x = rel.x - camPos.x;
			screenPos.y = rel.y - camPos.y;
			if (tile.seen) {
				drawEntity(screenPos, tile, character);
			}
		}
	}

	private void olddrawLocal(HashMap<String, RelativePosition> vision, Position camPos, CameraWindow camWindow) {
		// Right now this just goes through ALL the tiles grabbed a relative process
		// So no more relativeness SHOULD NEED TO BE DONE!!!
		// Instead, we should do a traditional LOS kinda
		// but change vision to be a hashmap of tile IDs indexed by relative positions
		// (which include ID anyways)
		// Then have a loop through the edge of a circle of radius=sight.distance
		// and it should draw every character along the way, from center out
		// to avoid retracing rays.
		// Do findLine,
		
		//_p.log(String.format("%d", vision.values().size()));
		
		for (RelativePosition rel : vision.values()) {
			int t = rel.e;
			Position pos = new Position();
			pos.x = rel.x;
			pos.y = rel.y;
			// int localX = rel.x;
			// int localY = rel.y
			// Position pos = mPosition.create(t); //TODO: RELATIVE POSITIONS???
			Char character = mChar.get(t);
			ChunkAddress chunkAddress = mChunkAddress.get(t);
			Chunk chunk = FrogmodaiEngine.worldManager.getChunk(chunkAddress.worldID);
			Tile tile = mTile.get(t);

			// if (fullRedraw) tile.cachedLOS = false;

			// if (chunkAddress.worldID == FrogmodaiEngine.worldManager.activeChunk) {
			Position screenPos = new Position();
			screenPos.x = pos.x - camPos.x;
			screenPos.y = pos.y - camPos.y;
			
			//_p.log(String.format("%d, %d", screenPos.x, screenPos.y));
			
			if (screenPos.x >= 0 && screenPos.y >= 0 && screenPos.x < camWindow.width
					&& screenPos.y < camWindow.height) { // If this tile is on screen
				if (perspective == -1) {
					if (!drawEntity(screenPos, tile, character))
						// buffer.setCharacterAt(screenPos.x, screenPos.y,
						// character.getTextCharacter());
						setCharacter(screenPos.x, screenPos.y, character.getTextCharacter());
				} else {
					Position playerPos = mPosition.create(perspective);
					Sight sight = mSight.create(perspective);
					ChunkAddress playerChunkAddress = mChunkAddress.create(perspective);
					Chunk playerChunk = FrogmodaiEngine.worldManager.getChunk(playerChunkAddress.worldID);
					// if (true) {//pos.withinDistance(playerPos, sight.distance) ) {
					// && FrogmodaiEngine.worldManager.getActiveChunk().LOS(playerPos.x,
					// playerPos.y, pos.x,
					// pos.y)) {
					// TODO: this is significantly broken
					// RESOLUTION(?): parameter "start" was the chunk of the destination tile, not
					// the player's chunk.
					// if (playerChunkAddress.worldID != chunkAddress.worldID) continue;
					// FrogmodaiEngine.worldManager.LOS(playerChunk, playerPos.x, playerPos.y,
					// pos.x, pos.y,
					// buffer);
					// if (FrogmodaiEngine.worldManager.badLOS(playerChunk, playerPos.x,
					// playerPos.y, pos.x,
					// pos.y)) { // TODO:
					// CROSSING
					// CHUNKS IS
					// FUCKING
					// BROKEN
					if (!drawEntity(screenPos, tile, t, character)) {
						// buffer.setCharacterAt(screenPos.x, screenPos.y,
						// character.getTextCharacter());
						//_p.log(String.format("(%d, %d) %c", screenPos.x, screenPos.y, character.getTextCharacter().character));
						setCharacter(screenPos.x, screenPos.y, character.getTextCharacter());
					}
					tile.seen = true;
					// tile.cachedLOS = true;
					// }
					// } //Don't draw anything not in your line of sight
				}
			}
			// }
		}
		fullRedraw = false;
	}

	// @Override
	private void memoryDraw(Position camPos, CameraWindow camWindow) {
		for (int y = 0; y < camWindow.height; y++) {
			for (int x = 0; x < camWindow.width; x++) {
				int tx = camPos.x + x;
				int ty = camPos.y + y;

				// For all spots in the camera window,
				// Check the ACTIVE CHUNK for a tile.

				Chunk chunk = FrogmodaiEngine.worldManager.getActiveChunk();
				int i = tx + ty * chunk.width;
				if (tx < 0 || ty < 0 || tx >= chunk.width || ty >= chunk.height) {
					// buffer.setCharacterAt(x, y, emptyCharacter);
					setCharacter(x, y, emptyCharacter);
					continue;
				}
				int t = chunk.tiles[i];

				Position pos = mPosition.create(t);
				Position screenPos = new Position();
				screenPos.x = pos.x - camPos.x;
				screenPos.y = pos.y - camPos.y;
				Char character = mChar.create(t);
				ChunkAddress chunkAddress = mChunkAddress.create(t);
				Tile tile = mTile.create(t);

				Position playerPos = mPosition.create(perspective);
				Sight sight = mSight.create(perspective);

				if (pos.withinDistance(playerPos, sight.distance))
					continue;

				if (chunkAddress.worldID == FrogmodaiEngine.worldManager.activeChunk
						&& FrogmodaiEngine.cameraID != -1) {

					if (screenPos.x >= 0 && screenPos.y >= 0 && screenPos.x < camWindow.width
							&& screenPos.y < camWindow.height) {
						if (perspective == -1) { // If no perspective, draw everything as-is
							if (!drawEntity(screenPos, tile, character))
								// buffer.setCharacterAt(screenPos.x, screenPos.y,
								// character.getTextCharacter());
								setCharacter(screenPos.x, screenPos.y, character.getTextCharacter());
						} else {
							if (tile.seen) { // Draw memory
								//buffer.setCharacterAt(screenPos.x, screenPos.y,
								//		character.getTextCharacter(8, 0, false));
							} else { // Black out unknown areas
								//buffer.setCharacterAt(screenPos.x, screenPos.y,
								//		character.getTextCharacter(0, 0, false));
							}
						}
					}
				}

				// TODO: render neighboring chunks
			}
		}
	}

	private boolean drawEntity(Position pos, Tile tile, Char tileChar) {
		if (tile.entitiesHere.size() == 0)
			return false;

		int winner = -1;
		for (int e : tile.entitiesHere) {
			System.out.println("DRAW " + e + ", " + mChar.get(e).tile.character);
			if (mChar.has(e) && !mIsDead.has(e)) { // don't bother with anything that doesn't draw a character
				if (winner == -1)
					winner = e;
				else {
					if (mIsItem.has(e) && !mIsItem.has(winner) && !mTimedActor.has(winner))
						winner = e;
					if (mTimedActor.has(e) && !mTimedActor.has(winner)) //Actors claim overe non-actors
						winner = e;
				}
			}
		}
		
		//System.out.println(winner + ", " + mChar.get(winner).tile.character);

		if (winner == -1)
			return false;

		//System.out.println(winner + ", " + mChar.get(winner).tile.character);// + ", " +
		// mIsDead.has(winner));

		PTile ct = mChar.get(winner).getTextCharacter();
		ct.style.bgc = tileChar.tile.style.bgc;
		//System.out.println("fsdafsdaf");
		//buffer.setCharacterAt(pos.x, pos.y, ct.withBackgroundColor(TextColor.ANSI.values()[tileChar.bgc]));
		setCharacter(pos.x, pos.y, ct);
		return true;
	}
	
	private boolean drawEntity(Position pos, Tile tile, int t, Char tileChar) {
		//System.out.println(t);
		//if (t == 111) System.out.println("HEY " + tile.entitiesHere + ", " + tile.occupied);
		if (tile.entitiesHere.size() == 0)
			return false;

		int winner = -1;
		for (int e : tile.entitiesHere) {
			
			//System.out.println("DRAW " + e + ", " + mChar.get(e).tile.character + ", " + t);
			if (mChar.has(e) && !mIsDead.has(e)) { // don't bother with anything that doesn't draw a character
				if (winner == -1)
					winner = e;
				else {
					if (mIsItem.has(e) && !mIsItem.has(winner) && !mTimedActor.has(winner))
						winner = e;
					if (mTimedActor.has(e) && !mTimedActor.has(winner)) //Actors claim overe non-actors
						winner = e;
				}
			}
		}
		
		//System.out.println(winner + ", " + mChar.get(winner).tile.character);

		if (winner == -1)
			return false;

		//System.out.println(winner + ", " + mChar.get(winner).tile.character);// + ", " +
		// mIsDead.has(winner));

		PTile ct = mChar.get(winner).getTextCharacter();
		ct.style.bgc = tileChar.tile.style.bgc;
		//System.out.println("fsdafsdaf");
		//buffer.setCharacterAt(pos.x, pos.y, ct.withBackgroundColor(TextColor.ANSI.values()[tileChar.bgc]));
		setCharacter(pos.x, pos.y, ct);
		return true;
	}
}
