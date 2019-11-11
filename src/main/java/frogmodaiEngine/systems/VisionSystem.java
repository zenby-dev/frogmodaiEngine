package frogmodaiEngine.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.Chunk;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.components.*;
import frogmodaiEngine.events.ActorDied;
import frogmodaiEngine.events.ChangeStat;
import frogmodaiEngine.events.HPAtZero;
import frogmodaiEngine.events.ScreenRefreshRequest;
import frogmodaiEngine.events.HPAtZero.After;
import frogmodaiEngine.events.HPAtZero.During;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class VisionSystem extends IteratingSystem {

	//Runs after everything else to delete dead creatures.
	
	ComponentMapper<IsDead> mIsDead;
	ComponentMapper<Sight> mSight;
	ComponentMapper<Position> mPosition;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<Tile> mTile;
	
	EventSystem es;

	public VisionSystem() {
		super(Aspect.all(Sight.class, Position.class, ChunkAddress.class).exclude(IsDead.class)); // ??
		//Need:
		//Sight
		//ChunkAddress/Chunk
	}

	@Override
	protected void process(int e) {
		Sight sight = mSight.get(e);
		if (!sight.refreshNeeded) return;
		Position myPos = mPosition.get(e);
		ChunkAddress ca = mChunkAddress.get(e);
		Chunk chunk = FrogmodaiEngine.worldManager.getChunk(ca.worldID);
		
		//System.out.println(e);
		//sight.clear();
		//chunk.floodGrab(myPos, sight.distance, sight.visibleTiles);
		
		//Grab all visitable tiles first
		HashMap<String, RelativePosition> visitable = new HashMap<String, RelativePosition>();
		chunk.floodGrab(myPos, sight.distance, visitable);
		
		//Prune down to visible tiles only
		sight.visibleTiles.clear();
		for (RelativePosition rel : visitable.values()) {
			boolean pass = FrogmodaiEngine.worldManager.badLOS(chunk, myPos.x, myPos.y, rel.x, rel.y);
			if (pass) {
				sight.visibleTiles.put(rel.toString(), rel);
			}
		}
		
		sight.refreshNeeded = false;
	}
}