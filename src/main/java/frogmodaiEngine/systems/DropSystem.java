package frogmodaiEngine.systems;

import java.util.ArrayList;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Aspect.Builder;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.*;
//import frogmodaiEngine.commands.DropCommand;
//import frogmodaiEngine.commands.PickupCommand;
import frogmodaiEngine.components.*;

public class DropSystem extends IteratingSystem {
	ComponentMapper<Position> mPosition;
	ComponentMapper<VirtualController> mVirtualController;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<Container> mContainer;

	ComponentMapper<Pickupable> mPickupable;
	ComponentMapper<IsInContainer> mIsInContainer;

	public DropSystem() {
		super(Aspect.all(Position.class, VirtualController.class, ChunkAddress.class));

	}

	@Override
	protected void process(int e) {
		Position pos = mPosition.create(e);
		VirtualController virtualController = mVirtualController.create(e);
		ChunkAddress chunkAddress = mChunkAddress.create(e);
		Chunk chunk = FrogmodaiEngine.worldManager.getChunk(chunkAddress.worldID);
		Container container = mContainer.create(e);

		//if (!(virtualController.peek() instanceof DropCommand)) // only skim off and process appropriate commands!
		//	return;

		//virtualController.poll();

		//FrogmodaiEngine.worldManager.uiHelper.Drop(e, container.list);
		
	}
	
	public void dropItem(int e, int ent) {
		Container container = mContainer.create(e);
		if (mIsInContainer.has(e)) { //For objects moving up a level in a nested container
			IsInContainer conInternal = mIsInContainer.create(ent);
			IsInContainer conExternal = mIsInContainer.create(e);
			Container outerContainer = mContainer.create(conExternal.parent);
			conInternal.parent = conExternal.parent;
			container.removeObject(ent);
			outerContainer.addObject(ent);
		} else { //Drop it on the ground
			mIsInContainer.remove(ent);
			container.removeObject(ent);
		}
	}

	public void dropAll(int e) {
		Container container = mContainer.create(e);
		for (int i = 0; i < container.list.size(); i++) {
			int ent = container.list.get(i);
			dropItem(e, ent);
		}
	}
}
