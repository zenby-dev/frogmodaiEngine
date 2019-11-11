package frogmodaiEngine;

import com.artemis.*;

import frogmodaiEngine.components.*;

public enum ArchetypeBuilders {
	
	Tile(new ArchetypeBuilder()
			.add(Position.class)
			.add(Tile.class)
			.add(Char.class)
			.add(ChunkAddress.class)
			.add(Description.class)),
	Player(new ArchetypeBuilder()
			.add(Position.class)
			.add(Char.class)
			.add(Solid.class)
			.add(Mobile.class)
			.add(ChunkAddress.class)
			.add(VirtualController.class)
			.add(IsPlayer.class)
			.add(TimedActor.class)
			.add(Description.class)
			.add(Weight.class)
			.add(Container.class)
			.add(Sight.class)
			.add(OnTile.class)),
	Actor(new ArchetypeBuilder()
			.add(Position.class)
			.add(Char.class)
			.add(Solid.class)
			.add(Mobile.class)
			.add(ChunkAddress.class)
			.add(VirtualController.class)
			.add(TimedActor.class)
			.add(Description.class)
			.add(Weight.class)
			.add(Container.class)
			.add(Sight.class)
			.add(OnTile.class)),
	Item(new ArchetypeBuilder()
			.add(IsItem.class)
			.add(Position.class) //All items have a location in the world?
			.add(Char.class) //All items have a visual representation
			.add(ChunkAddress.class)
			.add(Description.class)
			.add(Weight.class)
			.add(Pickupable.class)
			.add(OnTile.class)),
	Camera(new ArchetypeBuilder()
			.add(Position.class)
			.add(CameraWindow.class));
	
	public final ArchetypeBuilder archetypeBuilder;
	public Archetype archetype;
	
	ArchetypeBuilders(ArchetypeBuilder arch) {
		this.archetypeBuilder = arch;
	}
	
	void build(World world) {
		archetype = archetypeBuilder.build(world);
	}
	
	static void initArchetypes() {
		ArchetypeBuilders.Tile.build(FrogmodaiEngine.worldManager.world);
		ArchetypeBuilders.Player.build(FrogmodaiEngine.worldManager.world);
		ArchetypeBuilders.Actor.build(FrogmodaiEngine.worldManager.world);
		ArchetypeBuilders.Item.build(FrogmodaiEngine.worldManager.world);
		ArchetypeBuilders.Camera.build(FrogmodaiEngine.worldManager.world);
	}
}
