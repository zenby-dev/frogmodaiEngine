package frogmodaiEngine.systems;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.components.*;

public class ItemRelocatingSystem extends IteratingSystem {
	ComponentMapper<IsInContainer> mIsInContainer;
	ComponentMapper<Position> mPosition;
	ComponentMapper<Container> mContainer;

	public ItemRelocatingSystem() {
		super(Aspect.all(Pickupable.class, IsInContainer.class, Position.class));
		
	}

	@Override
	protected void process(int e) {
		//In here is where if you picked up an entity, they can struggle and try to escape (maybe)
		//Also its position gets updated
		Position itemPos = mPosition.create(e);
		IsInContainer iscon = mIsInContainer.create(e);
		Container container = mContainer.create(iscon.parent);
		Position holderPos = mPosition.create(iscon.parent);
		itemPos.x = holderPos.x;
		itemPos.y = holderPos.y;
	}

}
