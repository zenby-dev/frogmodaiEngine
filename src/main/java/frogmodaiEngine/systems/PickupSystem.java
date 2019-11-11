package frogmodaiEngine.systems;

import java.util.ArrayList;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Aspect.Builder;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.*;
//import frogmodaiEngine.commands.MoveCommand;
//import frogmodaiEngine.commands.PickupCommand;
import frogmodaiEngine.components.*;

public class PickupSystem extends IteratingSystem {
	ComponentMapper<Position> mPosition;
	ComponentMapper<VirtualController> mVirtualController;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<Container> mContainer;

	ComponentMapper<Pickupable> mPickupable;
	ComponentMapper<IsInContainer> mIsInContainer;

	public PickupSystem() {
		super(Aspect.all(Position.class, VirtualController.class, ChunkAddress.class));

	}

	@Override
	protected void process(int e) {
		// Access Components
		Position pos = mPosition.create(e);
		VirtualController virtualController = mVirtualController.create(e);
		ChunkAddress chunkAddress = mChunkAddress.create(e);
		Chunk chunk = FrogmodaiEngine.worldManager.getChunk(chunkAddress.worldID);
		Container container = mContainer.create(e);

		//if (!(virtualController.peek() instanceof PickupCommand)) // only skim off and process appropriate commands!
		//	return;

		//virtualController.poll(); // toss that command

		ArrayList<Integer> entities = chunk.getEntitiesAtPos(pos.x, pos.y);
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i) == e || !mPickupable.has(entities.get(i))) {
				entities.remove(i);
			}
		}
		
		if (entities.size() == 0) {
			//TODO: Fire message about lack of things to pick up
			return;
		}

		if (entities.size() == 1) {
			doPickup(e, entities.get(0));
			return;
		}
		
		//MODAL TIME!!!
		//FrogmodaiEngine.worldManager.uiHelper.Pickup(e, entities); //FORMS ARE BLOCKING YAY
	}

	public void doPickupAll(int e, ArrayList<Integer> entities) {
		for (int i = 0; i < entities.size(); i++) {
			int ent = entities.get(i);
			doPickup(e, ent);
		}
	}

	public void doPickup(int e, int ent) {
		Container container = mContainer.create(e);
		if (mPickupable.has(ent)) { // AND strength > pickup.requisite
			if (mIsInContainer.has(e)) { // Is this moving down a nested layer?
				IsInContainer conInternal = mIsInContainer.create(ent);
				IsInContainer conExternal = mIsInContainer.create(e);
				Container outerContainer = mContainer.create(conExternal.parent);
				conInternal.parent = e; // Object now in e
				container.addObject(ent);
				outerContainer.removeObject(ent);
			} else { // Picked up from outside
				IsInContainer con = mIsInContainer.create(ent);
				con.parent = e;
				container.addObject(ent);
				// TODO: Fire off pickup message/event
			}
		}
	}

}
