package frogmodaiEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.function.*;

import com.artemis.*;
import com.artemis.Aspect.Builder;
import com.artemis.io.JsonArtemisSerializer;
import com.artemis.link.EntityLinkManager;
import com.artemis.link.LinkAdapter;
import com.artemis.managers.WorldSerializationManager;
import com.artemis.utils.IntBag;

import frogmodaiEngine.components.*;
import frogmodaiEngine.events.CameraShift;
import frogmodaiEngine.events.ChangeStat;
import frogmodaiEngine.events.HPAtZero;
import frogmodaiEngine.events.ProcessIntermediate;
import frogmodaiEngine.events.ProcessTurnCycle;
import frogmodaiEngine.events.ProcessWorld;
import frogmodaiEngine.events.ScreenRefreshRequest;
import frogmodaiEngine.events.TurnCycle;
import frogmodaiEngine.generators.*;
import frogmodaiEngine.systems.*;
import net.mostlyoriginal.api.event.common.Event;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;
import net.mostlyoriginal.plugin.OperationsPlugin;

public class WorldManager {
	ArrayList<Chunk> chunkList;
	public World world;
	public CreatureBuilder creatureBuilder;
	public ItemBuilder itemBuilder;
	public UIHelper uiHelper;
	public int activeChunk = -1;

	private boolean screenRefreshRequested = false;
	// TileRenderingSystem tileRender;
	PScreen screen;

	final WorldSerializationManager serialManager = new WorldSerializationManager();

	public CaveGenerator caveGenerator;
	public MapLoader mapLoader;

	ComponentMapper<Tile> mTile;
	ComponentMapper<Char> mChar;
	ComponentMapper<Position> mPosition;
	ComponentMapper<ChunkAddress> mChunkAddress;

	EventSystem es;
	
	FrogmodaiEngine _p;

	public WorldManager(FrogmodaiEngine __p, PScreen _screen) {
		_p = __p;
		chunkList = new ArrayList<Chunk>();
		initWorld(_screen);
		creatureBuilder = new CreatureBuilder(world);
		itemBuilder = new ItemBuilder(world);
		//uiHelper = new UIHelper(world);
		caveGenerator = new CaveGenerator(world);
		mapLoader = new MapLoader();
	}

	void initWorld(PScreen _screen) {
		WorldConfiguration config = new WorldConfigurationBuilder()
				// .dependsOn(MyPlugin.class)
				//.dependsOn(EntityLinkManager.class)
				.dependsOn(EventSystem.class).with(serialManager,
						// Setup Phase
						new TimeSystem(),
						// new TileRenderingSystem(_screen),
						new VisionSystem(),

						// Behavior Systems (mostly event driven ie. not in order)
						new PlayerSystem(_p), new GoblinSystem(),

						// Logic Phase ??? (non-event driven ie. in order)
						new CharacterMovingSystem(), new MoveCollisionSystem(), new CameraMovingSystem(_p),
						new TileOccupationClearingSystem(), new TileOccupationSystem(), new PickupSystem(),
						new DropSystem(), new ItemRelocatingSystem(), new HPSystem(), new DeathSystem1(),

						// Rendering Phase
						new DescriptiveTextSystem(_p, _screen), new TileRenderingSystem(_p, _screen),
						new PositionGhostSystem(_screen), new SphereRenderingSystem(_screen),

						// Post-Rendering Phase
						new DeathSystem2())
				.build();
		world = new World(config);
		world.inject(this);
		world.inject(_p);
		
		//EntitySubscription subscription = world.getAspectSubscriptionManager().get(Aspect.all(ComponentY.class));
		//IntBag entityIds = subscription.getEntities();

		serialManager.setSerializer(new JsonArtemisSerializer(world));

		registerAllEvents();

		screen = _screen;
	}
	
	public IntBag getEntitiesFromAspect(Builder builder) {
		EntitySubscription subscription = world.getAspectSubscriptionManager().get(builder);
		return subscription.getEntities();
	}
	
	@Subscribe
	public void ProcessTurnCycleListener(ProcessTurnCycle event) {
		_p.logEventReceive("WorldManager", "ProcessTurnCycle");
		
		TimeSystem timeSystem = world.getSystem(TimeSystem.class);
		int actorsPerUpdate = timeSystem.getNumActors();
		
		FrogmodaiEngine.logEventEmit("WorldManager", String.format("TurnCycle.Before(%d)", actorsPerUpdate));
		FrogmodaiEngine.dispatch(new TurnCycle.Before());
		
		FrogmodaiEngine.logPush();
		for (int i = 0; i < actorsPerUpdate; i++) {
			if (!timeSystem.tick(i))
				break;
			timeSystem.actedCount++;
		}
		FrogmodaiEngine.logPop();
		
		if (timeSystem.actedCount >= actorsPerUpdate) {
			timeSystem.actedCount = 0;
		}
		
		FrogmodaiEngine.logEventEmit("WorldManager", "TurnCycle.After");
		FrogmodaiEngine.dispatch(new TurnCycle.After());
		FrogmodaiEngine.logEventEmit("WorldManager", "TURN CYCLE COMPLETE");
	}

	@Subscribe
	public void ProcessWorldListener(ProcessWorld event) {
		//_p.logEventReceive("WorldManager", "ProcessWorld");
		// OOPS! the order this queue goes in isn't the order that entities are
		// processed by the ECS!!!!
		//getActiveChunk().update();
		
		//FrogmodaiEngine.log("world.process() Begin");
		world.process(); //This is for all non-event systems
		//FrogmodaiEngine.log("world.process() End");
	}

	public void registerEvents(Object a) {
		world.getSystem(EventSystem.class).registerEvents(a);
	}

	public void registerAllEvents() {
		registerEvents(this);
		registerEvents(_p);
		// registerEvents(uiHelper);
		registerCoupledPortalDefListener();
	}

	void registerCoupledPortalDefListener() {
		/*world.getSystem(EntityLinkManager.class).register(CoupledPortalDef.class, new LinkAdapter() {

			// relevant fields are injected by default
			private ComponentMapper<CoupledPortalDef> mCoupledPortalDef;

			@Override
			public void onTargetDead(int sourceId, int deadTargetId) {
				// deadTargetId is the deleted entity currently referenced
				// by CoupledPortalDef, owned by entity with sourceId
				// mCoupledPortalDef.remove(sourceId);
				FrogmodaiEngine.delete(sourceId);
			}
		});*/

	}

	public void runEventSet(String eventName, CancellableEvent _before, CancellableEvent _during, Event _after) {
		CancellableEvent before = _before;
		// System.out.println(es);
		if (eventName != null)
			FrogmodaiEngine.logEventEmit("---", String.format("%s.Before", eventName));
		FrogmodaiEngine.dispatch(before);
		if (!before.isCancelled()) {
			CancellableEvent during = _during;
			if (eventName != null)
				FrogmodaiEngine.logEventEmit("---", String.format("%s.During", eventName));
			FrogmodaiEngine.dispatch(during);
			if (!during.isCancelled()) {
				Event after = _after;
				if (eventName != null)
					FrogmodaiEngine.logEventEmit("---", String.format("%s.After", eventName));
				FrogmodaiEngine.dispatch(after);
			}
		}
	}

	@Subscribe
	public void ScreenRefreshRequestListener(ScreenRefreshRequest event) {
		screenRefreshRequested = true;
		world.getSystem(TileRenderingSystem.class).triggerRedraw();
		world.getSystem(DescriptiveTextSystem.class).triggerRedraw();
	}

	public boolean refreshNeeded() {
		if (screenRefreshRequested) {
			screenRefreshRequested = false;
			_p.log("REFRESH APPROVED");
			// System.out.println("a");
			// world.getSystem(TileRenderingSystem.class).triggerRedraw();
			// world.getSystem(DescriptiveTextSystem.class).triggerRedraw();
			return true;
		}
		if (world.getSystem(TileRenderingSystem.class).drewThisFrame
				|| world.getSystem(DescriptiveTextSystem.class).drewThisFrame) {
			// System.out.println("b");
			_p.log("REFRESH APPROVED2");
			world.getSystem(TileRenderingSystem.class).drewThisFrame = false;
			world.getSystem(DescriptiveTextSystem.class).drewThisFrame = false;
			return true;
		}
		return false;
	}

	public void start() {
		BasicDungeon dungeon = new BasicDungeon(world);
		dungeon.someshit();

		// creatureBuilder.sphere(64+32, 16, 16, 20.0f);

		// getChunk(chunk1).attachCorner(getChunk(chunk1), 0);
		// getChunk(chunk1).attachCorner(getChunk(chunk3), 1);
		// getChunk(chunk1).attachCorner(getChunk(chunk3), 2);
		// getChunk(chunk1).attachCorner(getChunk(chunk1), 3);

		// getChunk(chunk2).attachCorner(getChunk(chunk3), 0);
		// getChunk(chunk2).attachCorner(getChunk(chunk2), 1);
		// getChunk(chunk2).attachCorner(getChunk(chunk2), 2);
		// getChunk(chunk2).attachCorner(getChunk(chunk3), 3);

		// getChunk(chunk3).attachCorner(getChunk(chunk1), 0);
		// getChunk(chunk3).attachCorner(getChunk(chunk2), 1);
		// getChunk(chunk3).attachCorner(getChunk(chunk2), 2);
		// getChunk(chunk3).attachCorner(getChunk(chunk1), 3);
	}

	void queueEdit() { // proposes an edit to the map.
		// to resolve conflicts, edits should have priority? or should I move on to time
		// management
		// because if it's added and handled in order i can have it ignore edits for
		// tiles that
		// already have accepted proposals
	}

	void flushQueue() { // updates the map with all accepted proposed edits from last frame

	}

	public void setRenderingPerspective(int e) {
		// world.getSystem(CharacterRenderingSystem.class).perspective = e;
		// world.getSystem(ItemRenderingSystem.class).perspective = e;
		if (world.getSystem(TileRenderingSystem.class) == null)
			return;

		world.getSystem(TileRenderingSystem.class).perspective = e;
	}

	public int getRenderingPerspective() {
		if (world.getSystem(TileRenderingSystem.class) == null)
			return -1;

		return world.getSystem(TileRenderingSystem.class).perspective;
	}

	public void triggerTileRedraw() {
		if (world.getSystem(TileRenderingSystem.class) == null)
			return;

		world.getSystem(TileRenderingSystem.class).triggerRedraw();
	}

	int createChunk() {
		Chunk chunk = new Chunk();
		return addChunk(chunk);
	}

	public int createChunk(int w, int h) {
		Chunk chunk = new Chunk(w, h, screen);
		return addChunk(chunk);
	}

	int addChunk(Chunk chunk) {
		chunk.worldID = chunkList.size();
		chunk.initTiles(world);
		// System.out.println(chunk.worldID);
		if (activeChunk == -1)
			activeChunk = chunk.worldID;
		chunkList.add(chunk);
		return chunk.worldID;
	}

	public void setActiveChunk(int i) {
		if (i < 0 || i >= chunkList.size())
			return;
		activeChunk = i;
	}

	void setActiveChunk(Chunk chunk) {
		if (chunk.worldID < 0)
			return;
		activeChunk = chunk.worldID;
	}

	public void shiftChunks(Chunk newChunk) { // Does the "activeChunk != playerChunk" issue arise here?
		Chunk oldChunk = getActiveChunk();
		for (Chunk chunk : oldChunk.neighbors) {
			chunk.unload();
		}
		setActiveChunk(newChunk);
		newChunk.load();
		oldChunk.unload(); // idk this just seems right LOL
		for (Chunk chunk : newChunk.neighbors) {
			chunk.load();
		}
		// System.out.println("SHIFT!!");
	}

	public Chunk getChunk(int i) {
		try {
			return chunkList.get(i);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Chunk getActiveChunk() {
		try {
			return chunkList.get(activeChunk);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Subscribe
	void CameraShiftAfterListener(CameraShift.After event) {
		//triggerTileRedraw();
		// FrogmodaiEngine.sendMessage(event.dx + ", " + event.dy);
	}

	void generateTest() {
		Chunk chunk = getActiveChunk();
		chunk = caveGenerator.generate(chunk);
		// int ghost = creatureBuilder.positionGhost(10, 10);
		// creatureBuilder.camera(ghost, 0, 0, 64, 32, 14);
	}

	public int[] findLine(int x0, int y0, int x1, int y1) {
		int length = (int) Math.ceil(Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1)));
		// System.out.println(length);
		if (length == 0)
			return null;
		int[] line = new int[(length) * 2 + 4]; // allocate extra space
		// for (int i = 0; i < line.length; i++)
		// line[i] = -1;
		int c = 0;

		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int e2;

		while (true) { // TODO: adjust to tile.neighbor based
			if (c < line.length) {
				line[c] = x0;
				line[c + 1] = y0;
				// System.out.println(String.format("%d,%d", x0, y0));
				c += 2;
				// if (c >= line.length) break;
			}
			// line.add(XYToi(x0, y0));

			if (x0 == x1 && y0 == y1)
				break;

			e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}

			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
		}

		if (c < line.length) { // Mark points beyond end, if they exist (should there be a -1?)
			line[c] = -9999;
			line[c + 1] = -9999;
		}
		// System.out.println("");
		return line;
	}

	/*public boolean worseLOS(Chunk start, int x0, int y0, int x1, int y1, int sx, int sy, ScreenBuffer buffer,
			HashMap<String, RelativePosition> vision) {
		int t = start.getTile(x0, y0);
		int[] line = findLine(x0, y0, x1, y1);
		if (line == null)
			return true; // making this false makes the player unseeable
		// int lastX = x0;
		// int lastY = y0;
		// if (line.length <= 2) return true; //very short line
		// screen.setCharacter(x0, y0, new TextCharacter('@', TextColor.ANSI.CYAN,
		// TextColor.ANSI.RED));
		for (int i = 2; i < line.length; i += 2) {
			int x = line[i];
			int y = line[i + 1];

			if (x == -9999 || y == -9999)
				return false;

			System.out.println(x + "|" + y);

			if (!vision.containsKey(x + "|" + y))
				return false; // SHRUG IDK BRO

			RelativePosition rel = vision.get(x + "|" + y);

			Tile newTile = mTile.create(rel.e);
			Char newChar = mChar.create(rel.e);
			ChunkAddress newTileChunkAddress = mChunkAddress.create(rel.e);

			buffer.setCharacterAt(x - sx, y - sy, newChar.getTextCharacter());
			newTile.seen = true;

			if (newTile.solid) {
				return false;
			}
		}
		// screen.setCharacter(x1, y1, new TextCharacter('X', TextColor.ANSI.GREEN,
		// TextColor.ANSI.YELLOW));
		// for (int i : line) { // doesn't count solids at the beginning/end
		// if (i != XYToi(x0, y0) && i != XYToi(x1, y1) && i != -1) {
		// // if (x0 != x1 && y0 != y1)
		// // System.out.println(String.format("%d %b", i, isSolid(i)));
		// if (isSolid(i))
		// return false;
		// }
		// }
		// if (x0 != x1 && y0 != y1) System.out.println("AAA");
		return true;
	}*/

	public boolean LOS(Chunk start, int x0, int y0, int x1, int y1, int cx, int cy, PScreen buffer) {
		int t = start.getTile(x0, y0);
		int[] line = findLine(x0, y0, x1, y1);
		if (line == null)
			return true; // making this false makes the player unseeable
		// int lastX = x0;
		// int lastY = y0;
		// if (line.length <= 2) return true; //very short line
		// screen.setCharacter(x0, y0, new TextCharacter('@', TextColor.ANSI.CYAN,
		// TextColor.ANSI.RED));
		for (int i = 2; i < line.length; i += 2) {
			int x = line[i];
			int y = line[i + 1];
			// if (x == -1 && y == -1) break;
			Position d = new Position();
			d.x = x - line[i - 2];
			d.y = y - line[i - 1];

			// screen.setCharacter(x, y, new TextCharacter((char)((i/2)+48),
			// TextColor.ANSI.RED, TextColor.ANSI.CYAN));
			// System.out.println(String.format("%d,%d %d,%d", x, y, d.x, d.y));
			// if (x == 0 && y == 0 && (Math.abs(d.x) > 1 || Math.abs(d.y) > 1)) return
			// true; //passed end of the line
			if (x == -9999 && y == -9999) {
				// screen.setCharacter(line[i-2], line[i-1], new TextCharacter((char)((i/2)+48),
				// TextColor.ANSI.RED, TextColor.ANSI.CYAN));
				return true; // if false, there diagonal directions cannot be seen
			} // doesn't affect tunnel issue

			Tile oldTile = mTile.create(t);
			ChunkAddress tileChunkAddress = mChunkAddress.create(t);

			// screen.setCharacter(line[i-2], line[i-1], new TextCharacter((char)((i/2)+48),
			// TextColor.ANSI.GREEN, TextColor.ANSI.MAGENTA));

			int dir = DirectionConverter.toInt(d);
			// screen.setCharacter(x, y, new TextCharacter((char)((dir)+48),
			// TextColor.ANSI.RED, TextColor.ANSI.CYAN));
			// There's an extra row present when the bleed is present!!!!

			// if (dir == -1) System.out.println(String.format("%d,%d %d,%d", x, y, d.x,
			// d.y));
			if (dir == -1)
				return false; // TODO: why is this returning -1 ever?
			int neighbor = oldTile.neighbors[dir];
			if (neighbor == -1)
				return false; // THE BLEED IN THE TUNNEL
			t = neighbor;

			// screen.setCharacter(x, y, new TextCharacter((char)((dir)+48),
			// TextColor.ANSI.RED, TextColor.ANSI.CYAN));

			Tile newTile = mTile.create(t);
			Char newChar = mChar.create(t);
			ChunkAddress newTileChunkAddress = mChunkAddress.create(t);

			buffer.set(x - cx, y - cy, newChar.getTextCharacter());
			newTile.seen = true;

			// if (x != x0 && x != x1 && y != y0 && y != y1) { //don't check solidity of
			// start/end tiles
			// if (!((x == x0 && y == y0) || (x == x1 && y == y1))) {
			if (!((x == x0 && y == y0) || (x == x1 && y == y1))) {
				if (newTile.solid) {
					// screen.setCharacter(x, y, new TextCharacter('X', TextColor.ANSI.CYAN,
					// TextColor.ANSI.RED));
					return false;
				}
			}
		}
		// screen.setCharacter(x1, y1, new TextCharacter('X', TextColor.ANSI.GREEN,
		// TextColor.ANSI.YELLOW));
		// for (int i : line) { // doesn't count solids at the beginning/end
		// if (i != XYToi(x0, y0) && i != XYToi(x1, y1) && i != -1) {
		// // if (x0 != x1 && y0 != y1)
		// // System.out.println(String.format("%d %b", i, isSolid(i)));
		// if (isSolid(i))
		// return false;
		// }
		// }
		// if (x0 != x1 && y0 != y1) System.out.println("AAA");
		return true;
	}

	// This function directly crawls through the tiles.neighbors array.
	public boolean badLOS(Chunk start, int x0, int y0, int x1, int y1) {
		int t = start.getTile(x0, y0);
		int[] line = findLine(x0, y0, x1, y1);
		if (line == null)
			return true; // making this false makes the player unseeable
		// int lastX = x0;
		// int lastY = y0;
		// if (line.length <= 2) return true; //very short line
		// screen.setCharacter(x0, y0, new TextCharacter('@', TextColor.ANSI.CYAN,
		// TextColor.ANSI.RED));
		int solidHits = 0;

		for (int i = 2; i < line.length; i += 2) {
			int x = line[i];
			int y = line[i + 1];
			// if (x == -1 && y == -1) break;
			Position d = new Position();
			d.x = x - line[i - 2];
			d.y = y - line[i - 1];

			// screen.setCharacter(x, y, new TextCharacter((char)((i/2)+48),
			// TextColor.ANSI.RED, TextColor.ANSI.CYAN));
			// System.out.println(String.format("%d,%d %d,%d", x, y, d.x, d.y));
			// if (x == 0 && y == 0 && (Math.abs(d.x) > 1 || Math.abs(d.y) > 1)) return
			// true; //passed end of the line
			if (x == -9999 && y == -9999) {
				// screen.setCharacter(line[i-2], line[i-1], new TextCharacter((char)((i/2)+48),
				// TextColor.ANSI.RED, TextColor.ANSI.CYAN));
				return true; // if false, there diagonal directions cannot be seen
			} // doesn't affect tunnel issue

			Tile oldTile = mTile.get(t);
			ChunkAddress tileChunkAddress = mChunkAddress.get(t);

			// screen.setCharacter(line[i-2], line[i-1], new TextCharacter((char)((i/2)+48),
			// TextColor.ANSI.GREEN, TextColor.ANSI.MAGENTA));

			int dir = DirectionConverter.toInt(d);
			// screen.setCharacter(x, y, new TextCharacter((char)((dir)+48),
			// TextColor.ANSI.RED, TextColor.ANSI.CYAN));
			// There's an extra row present when the bleed is present!!!!

			// if (dir == -1) System.out.println(String.format("%d,%d %d,%d", x, y, d.x,
			// d.y));
			if (dir == -1)
				return false; // TODO: why is this returning -1 ever?
			int neighbor = oldTile.neighbors[dir];
			if (neighbor == -1)
				return false; // THE BLEED IN THE TUNNEL
			t = neighbor;

			// screen.setCharacter(x, y, new TextCharacter((char)((dir)+48),
			// TextColor.ANSI.RED, TextColor.ANSI.CYAN));

			Tile newTile = mTile.get(t);
			Char newChar = mChar.get(t);
			ChunkAddress newTileChunkAddress = mChunkAddress.get(t);

			// if (x != x0 && x != x1 && y != y0 && y != y1) { //don't check solidity of
			// start/end tiles
			// if (!((x == x0 && y == y0) || (x == x1 && y == y1))) {
			if (!((x == x0 && y == y0) || (x == x1 && y == y1))) {
				if (newTile.solid) {
					// FrogmodaiEngine.sendMessage(d.x + ", " + d.y);
					// System.out.println(x+", "+y);
					// screen.setCharacter(x, y, new TextCharacter('X', TextColor.ANSI.CYAN,
					// TextColor.ANSI.RED));
					if (solidHits >= 0) { // eh
						return false;
					} else {
						solidHits++;
					}
				}
			}
		}
		// screen.setCharacter(x1, y1, new TextCharacter('X', TextColor.ANSI.GREEN,
		// TextColor.ANSI.YELLOW));
		// for (int i : line) { // doesn't count solids at the beginning/end
		// if (i != XYToi(x0, y0) && i != XYToi(x1, y1) && i != -1) {
		// // if (x0 != x1 && y0 != y1)
		// // System.out.println(String.format("%d %b", i, isSolid(i)));
		// if (isSolid(i))
		// return false;
		// }
		// }
		// if (x0 != x1 && y0 != y1) System.out.println("AAA");
		return true;
	}
}