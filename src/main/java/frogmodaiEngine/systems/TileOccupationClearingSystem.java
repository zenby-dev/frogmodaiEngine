package frogmodaiEngine.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;

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
	
	//boolean needsProcessing = false;
	
	EventSystem es;
	
	//This system helps keep a handy list of entities refreshed on each tile
	
	public TileOccupationClearingSystem() {
		super(Aspect.all(Tile.class));
	}
	
	@Override
	protected boolean checkProcessing() {
		//System.out.println("checkProcessing() " + needsProcessing);
		//return needsProcessing;
		return false;
	}

	@Override
	protected void process(int e) {
	}
	
	private void processTile(int e) {
		Tile tile = mTile.get(e);
		//if (tile.entitiesHere.size() > 0)
		//	System.out.println("CLEAR " + e + ", " + tile.entitiesHere.size());
		tile.clear();
	}
	
	@Override
	protected void end() {
		//System.out.println("end() " + needsProcessing);
		//needsProcessing = false;
		
	}
	
	@Subscribe
	void TurnCycleAfterListener(TurnCycle.After event) {
		FrogmodaiEngine.logEventReceive("TileOccupationClearing", "TurnCycleAfter");
		//needsProcessing = true;
		
		IntBag entities = subscription.getEntities();
		int[] ids = entities.getData();
		for (int i = 0, s = entities.size(); s > i; i++) {
		     processTile(ids[i]);
		}
		
		FrogmodaiEngine.logEventEmit("TileOccupationClearing", "TriggerTileOccupation");
		es.dispatch(new TriggerTileOccupation());
	}
}
