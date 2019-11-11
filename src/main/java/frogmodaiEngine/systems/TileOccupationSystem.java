package frogmodaiEngine.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Aspect.Builder;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.*;
import frogmodaiEngine.components.*;
import frogmodaiEngine.events.DebugInt;
import frogmodaiEngine.events.EventTemplate;
import frogmodaiEngine.events.TileOccupationFinished;
import frogmodaiEngine.events.TriggerTileOccupation;
import frogmodaiEngine.events.TurnCycle;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class TileOccupationSystem extends IteratingSystem {
	ComponentMapper<Position> mPosition;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<Tile> mTile;
	ComponentMapper<TimedActor> mTimedActor;
	ComponentMapper<Char> mChar;
	
	boolean needsProcessing = false;
	
	EventSystem es;
	
	//This system helps keep a handy list of entities refreshed on each tile
	
	public TileOccupationSystem() {
		super(Aspect.all(Position.class, ChunkAddress.class).exclude(IsInContainer.class, Tile.class));
	}
	
	@Override
	protected boolean checkProcessing() {
		//System.out.println("checkProcessing() " + needsProcessing);
		return needsProcessing;
	}

	@Override
	protected void process(int e) {
		Position pos = mPosition.get(e);
		ChunkAddress chunkAddress = mChunkAddress.get(e);
		Chunk chunk = FrogmodaiEngine.worldManager.getChunk(chunkAddress.worldID);
		//chunk.entityOnTile(pos.x, pos.y, e);
		int t = chunk.getTile(pos.x, pos.y); //THIS SHIT IS BROKEN. OW. Chunk attaching is issue.
		//System.out.println(e + ", " + mChar.get(e).tile.character + ", " + t);
		if (t == -1) System.out.println(String.format("%d, %d", pos.x, pos.y));
		if (t != -1) {
			Tile tile = mTile.get(t);
			tile.add(e);
			//System.out.println(e + ", " + mChar.get(e).tile.character + ", " + t + ", " + tile.entitiesHere);
			//System.out.println(tile.entitiesHere.size());
			if (mTimedActor.has(e)) {
				tile.occupied = true;
			}
		}
		
		//Tile tile = mTile.get(t);
		//tile.add(e);
		//System.out.println(e + ", " + mChar.get(e).tile.character + ", " + t + ", " + tile.entitiesHere);
		
		//es.dispatch(new DebugInt(t, e));
	}
	
	@Override
	protected void end() {
		//System.out.println("end() " + needsProcessing);
		needsProcessing = false;
		es.dispatch(new TileOccupationFinished());
	}
	
	/*@Subscribe
	void DebugIntListener(DebugInt event) {
		int t = event.value1;
		int e = event.value2;
		Tile tile = mTile.get(t);
		System.out.println(e + ", " + mChar.get(e).tile.character + ", " + t + ", " + tile.entitiesHere);
	}*/
	
	@Subscribe
	void TriggerTileOccupationListener(TriggerTileOccupation event) {
		System.out.println("TriggerTileOccupation[TileOccupation]");
		needsProcessing = true;
	}
}
