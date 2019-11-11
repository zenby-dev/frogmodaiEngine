package frogmodaiEngine.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.Chunk;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.components.ChunkAddress;
import frogmodaiEngine.components.IsInContainer;
import frogmodaiEngine.components.Position;
import frogmodaiEngine.components.Tile;
import frogmodaiEngine.events.TriggerTileOccupation;
import frogmodaiEngine.events.TurnCycle;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class TileOccupationClearingSystem extends IteratingSystem {
	ComponentMapper<Tile> mTile;
	
	boolean needsProcessing = false;
	
	EventSystem es;
	
	//This system helps keep a handy list of entities refreshed on each tile
	
	public TileOccupationClearingSystem() {
		super(Aspect.all(Tile.class));
	}
	
	@Override
	protected boolean checkProcessing() {
		//System.out.println("checkProcessing() " + needsProcessing);
		return needsProcessing;
	}

	@Override
	protected void process(int e) {
		Tile tile = mTile.get(e);
		//System.out.println("CLEAR " + e + ", " + tile.entitiesHere.size());
		tile.clear();
	}
	
	@Override
	protected void end() {
		//System.out.println("end() " + needsProcessing);
		needsProcessing = false;
		
		es.dispatch(new TriggerTileOccupation());
	}
	
	@Subscribe
	void TurnCycleAfterListener(TurnCycle.After event) {
		System.out.println("TurnCycleAfter[TileOccupationClearing]");
		needsProcessing = true;
	}
}
