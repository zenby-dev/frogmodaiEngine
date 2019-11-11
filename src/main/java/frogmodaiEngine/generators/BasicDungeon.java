package frogmodaiEngine.generators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.awt.color.*;

import com.artemis.ComponentMapper;
import com.artemis.World;

import frogmodaiEngine.Chunk;
import frogmodaiEngine.ColorUtils;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.components.Char;
import frogmodaiEngine.components.ChunkAddress;
import frogmodaiEngine.components.CoupledPortalDef;
import frogmodaiEngine.components.DirectedPortalDef;
import frogmodaiEngine.components.Position;
import frogmodaiEngine.components.Tile;

public class BasicDungeon {

	ComponentMapper<Tile> mTile;
	ComponentMapper<Char> mChar;
	ComponentMapper<Position> mPosition;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<CoupledPortalDef> mCoupledPortalDef;

	HashMap<Integer, ArrayList<Entrance>> entrances;
	ArrayList<Integer> rooms;
	char wallCharacter = '#';
	int wallColor = ColorUtils.Colors.GREY;
	int numRooms = 10;
	
	int maxWidth = 12;
	int maxHeight = 12;

	public BasicDungeon(World world) {
		world.inject(this);
		entrances = new HashMap<Integer, ArrayList<Entrance>>();
		rooms = new ArrayList<Integer>();
	}

	public void generate() {
		// The plan here is to generate *rooms* and *hallways*
		// Many oddly connected rooms could be stored in a single chunk...
		// Because chunks can connect to themselves
		// Side idea: dungeons where as soon as a door closes, it changes where it goes
		// (lost room style)
		// Hallways could be just any path within an otherwise empty rectangular chunk,
		// with openings replacing walls
		// This would allow really tight twisting hallways that lead to far too many
		// rooms to fit in space normally
		// Also, zelda lost-woods style dungeons
		// Every generation of a hallway or room should COME WITH coordinates for
		// portals that other rooms
		// can link to if the orientation and width are aligned

		// HashMap<Integer, ArrayList<Entrance>> entrances; //lists of entrances indexed
		// by chunk index
		// Or should rooms have their own indexing system within a dungeon
		// object????????
		// class Entrance { //This is really just parameters for a portal
		// int chunk
		// int width
		// int dir
		// int x
		// int y
		// }
		// boolean linkEntrances(Entrance a, Entrance b) {
		// if a.width == b.width and a.dir == (b.dir + 2)%4
		// portal portal
		// }

		for (int i = 1; i < numRooms; i++) {
			int chunk = generateBasicRoom(maxWidth / 2 + FrogmodaiEngine.random.nextInt(maxWidth / 2),
					maxHeight / 2 + FrogmodaiEngine.random.nextInt(maxHeight / 2));
		}

		for (int tries = 0; tries < 2000; tries++) {
			int room1 = rooms.get(Math.abs(FrogmodaiEngine.random.nextInt(rooms.size())));
			int room2 = rooms.get(Math.abs(FrogmodaiEngine.random.nextInt(rooms.size())));
			for (int fries = 0; fries < 1; fries++) {
				tryConnectRooms(room1, room2);
				//if (FrogmodaiEngine.random.nextInt(100)%3==0)
					tryConnectRooms(room1, room1);
			}
		}
	}
	
	public void createCoupledPortal(Chunk chunk1, Chunk chunk2, Entrance entrance1, Entrance entrance2) {
		int portal1 = chunk1.portal(chunk2, entrance1.width, entrance1.dir, entrance1.x, entrance1.y, entrance2.x, entrance2.y,
				true);
		int portal2 = chunk2.portal(chunk1, entrance2.width, entrance2.dir, entrance2.x, entrance2.y, entrance1.x, entrance1.y,
				true);
		int e = FrogmodaiEngine.worldManager.world.create();
		CoupledPortalDef portalDef = mCoupledPortalDef.create(e);
		portalDef.portal1 = portal1;
		portalDef.portal2 = portal2;
	}

	public void tryConnectRooms(int room1, int room2) {
		// int room1 = Math.abs(FrogmodaiEngine.random.nextInt(rooms.size()));
		// int room2 = Math.abs(FrogmodaiEngine.random.nextInt(rooms.size()));
		// System.out.println(entrances.get(room1).size());
		if (entrances.get(room1).size() < 1 || entrances.get(room2).size() < 1)
			return;
		int _entrance1 = Math.abs(FrogmodaiEngine.random.nextInt(entrances.get(room1).size()));
		int _entrance2 = Math.abs(FrogmodaiEngine.random.nextInt(entrances.get(room2).size()));
		if (_entrance1 == _entrance2 && room1 == room2)
			return; // going to itself is not allowed
		Entrance entrance1 = entrances.get(room1).get(_entrance1);
		Entrance entrance2 = entrances.get(room2).get(_entrance2);
		if (entrance1.width == entrance2.width && entrance1.dir == (entrance2.dir + 2) % 4) {
			Chunk chunk1 = FrogmodaiEngine.worldManager.getChunk(entrance1.chunk);
			Chunk chunk2 = FrogmodaiEngine.worldManager.getChunk(entrance2.chunk);
			createCoupledPortal(chunk1, chunk2, entrance1, entrance2);
			//System.out.printf("%d %d %d %d\n", room1, room2, _entrance1, _entrance2);
			entrances.get(room1).remove(entrance1);
			entrances.get(room2).remove(entrance2);
			if (entrance1.dir % 2 == 0) {
				for (int j = 0; j < entrance1.width; j++) {
					removeWall(entrance1.chunk, entrance1.x, entrance1.y+j);
					removeWall(entrance2.chunk, entrance2.x, entrance2.y+j);
				}
			} else {
				for (int j = 0; j < entrance1.width; j++) {
					removeWall(entrance1.chunk, entrance1.x+j, entrance1.y);
					removeWall(entrance2.chunk, entrance2.x+j, entrance2.y);
				}
			}
		}
	}

	public int generateHallway(int w, int h) {
		int chunk = FrogmodaiEngine.worldManager.createChunk(w, h);
		return chunk;
	}

	public int generateBasicRoom(int w, int h) {
		int chunk = FrogmodaiEngine.worldManager.createChunk(w, h);
		Chunk c = FrogmodaiEngine.worldManager.getChunk(chunk);
		rooms.add(chunk);
		entrances.put(chunk, new ArrayList<Entrance>());
		int index = rooms.size() - 1;

		for (int i = 0; i < c.tiles.length; i++) {
			Char chara = mChar.create(c.tiles[i]);
			chara.tile.style.fgc = ColorUtils.Colors.GREY;
			chara.tile.character = '.';
		}

		ArrayList<String> unclaimedWalls = new ArrayList<String>();

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (x == 0 || x == w - 1 || y == 0 || y == h - 1) {
					makeWall(chunk, x, y);
					unclaimedWalls.add(x + "|" + y);
				}
			}
		}

		//Add Entrance markers
		int numEntrances = 4;//1+FrogmodaiEngine.random.nextInt(8);
		// System.out.println(numEntrances);
		next: for (int i = 0; i < numEntrances; i++) {
			// System.out.println("> " + i);
			retry: for (int tries = 0; tries < 3; tries++) {
				//System.out.println("> " + i + ", " + tries);
				int dir = Math.abs(FrogmodaiEngine.random.nextInt()) % 4;
				int width = 2+FrogmodaiEngine.random.nextInt(1); // MAX width (smaller portals can still connect)
				int x = -1;
				int y = -1;
				// System.out.println("> " + i + ", " + tries + ", " + dir);
				if (dir == 0 || dir == 2) { // left
					x = dir == 2 ? 0 : w - 1;
					y = 1 + Math.abs(FrogmodaiEngine.random.nextInt()) % (h - 2 - (width - 1));
					for (int j = -1; j < width + 1; j++) {
						int xp = x;
						int yp = y + j;
						// System.out.println(xp+", "+yp);
						if (!unclaimedWalls.contains(xp + "|" + yp)) {
							//System.out.println(xp + ", " + yp);
							continue retry;
						}
					}
					for (int j = 0; j < width; j++) {
						int xp = x;
						int yp = y + j;
						unclaimedWalls.remove(xp + "|" + yp);
						// removeWall(chunk, xp, yp);
					}
					// x = dir == 2 ? 1 : w-2;
				} else if (dir == 1 || dir == 3) {
					y = dir == 3 ? 0 : h - 1;
					x = 1 + Math.abs(FrogmodaiEngine.random.nextInt()) % (w - 2 - (width - 1));
					for (int j = -1; j < width + 1; j++) {
						int xp = x + j;
						int yp = y;
						// System.out.println(xp+", "+yp);
						if (!unclaimedWalls.contains(xp + "|" + yp)) {
							//System.out.println(xp + ", " + yp);
							continue retry;
						}
					}
					for (int j = 0; j < width; j++) {
						int xp = x + j;
						int yp = y;
						unclaimedWalls.remove(xp + "|" + yp);
						// removeWall(chunk, xp, yp);
					}
					// y = dir == 3 ? 1 : h-2;
				}
				if (x == -1 || y == -1) {
					//System.out.println(x + ", " + y);
					continue retry;
				}
				entrances.get(chunk).add(new Entrance(chunk, width, dir, x, y));
				//System.out.printf("%d %d %d %d\n", width, dir, x, y);
				continue next;
			}
		}
		
		//Add Goblins
		int numGoblins = 1;
		for (int i = 0; i < numGoblins; i++) {
			FrogmodaiEngine.worldManager.creatureBuilder.goblin(c, 1+FrogmodaiEngine.random.nextInt(c.width-2), 1+FrogmodaiEngine.random.nextInt(c.height-2));
		}

		return chunk;
	}

	void makeWall(int chunk, int x, int y) {
		int me = FrogmodaiEngine.worldManager.getChunk(chunk).getTile(x, y);
		Tile tile = mTile.create(me);
		Char character = mChar.create(me);
		tile.solid = true;
		character.tile.character = wallCharacter;
		character.tile.style.fgc = ColorUtils.color(255, 0, 255);
				//ColorUtils.HSBtoRGB((chunk % 6)*255/6, 255, 255);
				//1 + (chunk % (TextColor.ANSI.values().length - 1));
		//character.bold = false;
	}

	void removeWall(int chunk, int x, int y) {
		int me = FrogmodaiEngine.worldManager.getChunk(chunk).getTile(x, y);
		Tile tile = mTile.create(me);
		Char character = mChar.create(me);
		tile.solid = false;
		character.tile.character = '.';
		character.tile.style.fgc = ColorUtils.Colors.GREY;
		//character.bold = false;
	}

	public void someshit() {
		int _chunk1 = FrogmodaiEngine.worldManager.createChunk(12 * 3, 12 * 3);
		int _chunk2 = FrogmodaiEngine.worldManager.createChunk(4, 36);
		int _chunk3 = FrogmodaiEngine.worldManager.createChunk(36, 4);
		FrogmodaiEngine.worldManager.setActiveChunk(_chunk1);
		Chunk chunk1 = FrogmodaiEngine.worldManager.getChunk(_chunk1);
		Chunk chunk2 = FrogmodaiEngine.worldManager.getChunk(_chunk2);
		Chunk chunk3 = FrogmodaiEngine.worldManager.getChunk(_chunk3);
		// generateTest();
		loadTestWorld(chunk1);
		FrogmodaiEngine.worldManager.caveGenerator.generate(chunk1);
		loadTest2(chunk2);
		loadTest2(chunk3);
		// generateTest();

		int _chunk4 = generateBasicRoom(16, 8);
		Chunk chunk4 = FrogmodaiEngine.worldManager.getChunk(_chunk4);

		chunk1.portal(chunk4, 3, 0, 11, 3, 1, 2, true);

		generate();
		// chunk4.portal(chunk1, 3, 2, 1, 2, 11, 3, true);

		// getChunk(chunk1).attach(getChunk(chunk2), 2, 0);
		chunk1.attach(chunk3, 0, -4);
		// getChunk(chunk2).attach(getChunk(chunk1), 0, 0);
		(chunk2).attach((chunk3), 2, -(36 / 2 - 2));
		(chunk3).attach((chunk2), 0, 36 / 2 - 2);
		(chunk3).attach((chunk1), 2, 4);

		(chunk1).attach((chunk2), 1, -4);
		(chunk2).attach((chunk1), 3, 4);

		(chunk1).attach((chunk2), 3, -4);
		(chunk2).attach((chunk1), 1, 4);

		chunk3.portal(chunk1, 1, 3, 18, 0, 10, chunk1.height-1, false);
		chunk1.portal(chunk3, 1, 1, 10, chunk1.height-1, 18, 0, false);
		//(chunk3).attachSingleTile((chunk1), 1, 0, (36 / 2), 10);
		//(chunk1).attachSingleTile((chunk3), 3, 0, 10, (36 / 2));
	}

	void loadTestWorld(Chunk chunk) {
		Random r = new Random();
		int numRocks = 0;
		for (int i = 0; i < numRocks; i++) { // make boulders
			int q = r.nextInt(chunk.width * chunk.height);
			int e = chunk.tiles[q];
			Tile tile = mTile.create(e);
			Char character = mChar.create(e);
			tile.solid = true;
			character.tile.character = '0';
			character.tile.style.fgc = ColorUtils.Colors.GREY;
		}

		FrogmodaiEngine.worldManager.creatureBuilder.player(chunk, 3, 3);
		FrogmodaiEngine.worldManager.setRenderingPerspective(FrogmodaiEngine.playerID);
		FrogmodaiEngine.worldManager.creatureBuilder.camera(FrogmodaiEngine.playerID, 0, 0, 64, 32, 14);
		int numGoblins = 0;
		for (int i = 0; i < numGoblins; i++) {
			FrogmodaiEngine.worldManager.creatureBuilder.goblin(chunk, r.nextInt(chunk.width), r.nextInt(chunk.height));
		}

		FrogmodaiEngine.worldManager.itemBuilder.createTest(chunk, 12, 6);
		// itemBuilder.createTest(chunk, 12, 6);
		// itemBuilder.createTest(chunk, 12, 6);

		// creatureBuilder.sphere(64+32, 16);
	}

	void loadTest2(Chunk chunk) {
		chunk.setGroundColor(ColorUtils.Colors.YELLOW);
		Random r = new Random();
		int numRocks = 0;
		for (int i = 0; i < numRocks; i++) { // make boulders
			int q = r.nextInt(chunk.width * chunk.height);
			int e = chunk.tiles[q];
			Tile tile = mTile.create(e);
			Char character = mChar.create(e);
			tile.solid = true;
			character.tile.character = '0';
			character.tile.style.fgc = ColorUtils.Colors.RED;
		}

		int numGoblins = 0;
		for (int i = 0; i < numGoblins; i++) {
			FrogmodaiEngine.worldManager.creatureBuilder.goblin(chunk, r.nextInt(chunk.width), r.nextInt(chunk.height));
		}

		// itemBuilder.createTest(chunk, 12, 6);
	}

	/*
	 * class V2 { int x; int y;
	 * 
	 * public V2() { x=0; y=0; }
	 * 
	 * public V2(int _x, int _y) { x=_x; y=_y; } }
	 */

	class Entrance {
		int chunk;
		int width;
		int dir;
		int x;
		int y;

		public Entrance(int _chunk, int _width, int _dir, int _x, int _y) {
			chunk = _chunk;
			width = _width;
			dir = _dir;
			x = _x;
			y = _y;
		}
	}
}
