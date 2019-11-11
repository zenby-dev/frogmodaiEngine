package frogmodaiEngine;

import java.util.function.Function;

import com.artemis.ComponentMapper;
import com.artemis.World;

//import frogmodaiEngine.behaviors.*;
import frogmodaiEngine.components.*;
import net.mostlyoriginal.api.event.common.EventSystem;

public class CreatureBuilder {

	ComponentMapper<Char> mChar;
	ComponentMapper<Position> mPosition;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<TimedActor> mTimedActor;
	ComponentMapper<Description> mDescription;
	ComponentMapper<CameraWindow> mCameraWindow;
	ComponentMapper<IsPositionGhost> mIsPositionGhost;
	ComponentMapper<SphereInfo> mSphereInfo;
	ComponentMapper<OnTouch> mOnTouch;
	ComponentMapper<OnTouched> mOnTouched;
	ComponentMapper<StatHP> mStatHP;
	ComponentMapper<IsFaction> mIsFaction;
	ComponentMapper<IsGoblin> mIsGoblin;
	
	EventSystem es;

	CreatureBuilder(World world) {
		world.inject(this);
	}

	public int player(Chunk chunk, int x, int y) {
		int player = FrogmodaiEngine.worldManager.world.create(ArchetypeBuilders.Player.archetype);
		FrogmodaiEngine.playerID = player;
		
		Position pos = mPosition.create(player);
		pos.x = x;
		pos.y = y;
		
		Char character = mChar.create(player);
		character.tile.character = '@';
		character.tile.style.fgc = ColorUtils.Colors.GREYLIGHT;
		//character.bold = true;
		
		ChunkAddress chunkAddress = mChunkAddress.create(player);
		chunkAddress.worldID = chunk.worldID;
		
		TimedActor actor = mTimedActor.create(player);
		actor.speed = TimedActor.TICK_ENERGY;
		actor.energy = actor.speed;
		//actor.act = DecisionBehaviors.PlayerMove.act;
		//FrogmodaiEngine.worldManager.world.inject(actor.act); // required!!!
		
		Description desc = mDescription.create(player);
		desc.name = "Player";
		
		OnTouch onTouch = mOnTouch.create(player);
		//onTouch.act = TouchBehaviors.PlayerTouch.act;
		//FrogmodaiEngine.worldManager.world.inject(onTouch.act);
		
		//SphereInfo sphereInfo = mSphereInfo.create(player);
		//sphereInfo.radius = 8;
		
		return player;
	}
	
	public int camera(int focus, int x, int y, int w, int h, int t) {
		int cam = FrogmodaiEngine.worldManager.world.create(ArchetypeBuilders.Camera.archetype);
		FrogmodaiEngine.cameraID = cam;
		Position pos = mPosition.create(cam);
		pos.x = x;
		pos.y = y;
		CameraWindow camWindow = mCameraWindow.create(cam);
		camWindow.width = w;
		camWindow.height = h;
		camWindow.focus = focus;
		camWindow.tolerance = t;
		return cam;
	}
	
	public int positionGhost(int x, int y) {
		int ghost = FrogmodaiEngine.worldManager.world.create();
		Position pos = mPosition.create(ghost);
		pos.x = x;
		pos.y = y;
		mIsPositionGhost.create(ghost);
		return ghost;
	}
	
	public int sphere(int x, int y, int _radius, float _speed) {
		int sphere = FrogmodaiEngine.worldManager.world.create();
		Position pos = mPosition.create(sphere);
		pos.x = x;
		pos.y = y;
		SphereInfo sphereInfo = mSphereInfo.create(sphere);
		sphereInfo.radius = _radius;
		sphereInfo.speed = _speed;
		return sphere;
	}

	public int goblin(Chunk chunk, int x, int y) {
		int gob = FrogmodaiEngine.worldManager.world.create(ArchetypeBuilders.Actor.archetype);
		
		Position pos = mPosition.create(gob);
		pos.x = x;
		pos.y = y;
		
		IsFaction faction = mIsFaction.create(gob);
		faction.name = "MONSTERS";
		
		mIsGoblin.create(gob);
		
		Char character = mChar.create(gob);
		character.tile.character = 'g';
		character.tile.style.fgc = ColorUtils.Colors.CYAN;
		
		//character.bold = false;
		
		StatHP statHP = mStatHP.create(gob);
		statHP.setMaxValue(3, true);
		es.registerEvents(statHP);
		
		ChunkAddress chunkAddress = mChunkAddress.create(gob);
		chunkAddress.worldID = chunk.worldID;
		
		TimedActor actor = mTimedActor.create(gob);
		actor.speed = TimedActor.TICK_ENERGY;
		actor.energy = actor.speed;
		//actor.act = DecisionBehaviors.GoblinMove.act;
		//FrogmodaiEngine.worldManager.world.inject(actor.act); // required!!!
		//es.registerEvents(actor.act);
		
		Description desc = mDescription.create(gob);
		desc.name = "Goblin";
		desc.addDescription("A small greebly feller");
		
		OnTouch onTouch = mOnTouch.create(gob);
		//onTouch.act = TouchBehaviors.GoblinTouch.act;
		//FrogmodaiEngine.worldManager.world.inject(onTouch.act);
		//es.registerEvents(onTouch.act);
		
		OnTouched onTouched = mOnTouched.create(gob);
		//onTouched.act = TouchedBehaviors.GoblinTouched.act;
		//FrogmodaiEngine.worldManager.world.inject(onTouched.act);
		//es.registerEvents(onTouched.act);
		
		return gob;
	}
}
