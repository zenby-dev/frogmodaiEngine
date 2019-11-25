package frogmodaiEngine.systems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.awt.event.KeyEvent;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.Chunk;
import frogmodaiEngine.ColorUtils;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.PScreen;
import frogmodaiEngine.PTile;
//import frogmodaiEngine.commands.DropCommand;
//import frogmodaiEngine.commands.PickupCommand;
import frogmodaiEngine.components.*;
import frogmodaiEngine.events.ActorDied;
import frogmodaiEngine.events.ActorTakeTurn;
import frogmodaiEngine.events.ChangeStat;
import frogmodaiEngine.events.HPAtZero;
import frogmodaiEngine.events.MoveAttempt;
import frogmodaiEngine.events.PlayerTookTurn;
import frogmodaiEngine.events.PostTileRendering;
import frogmodaiEngine.events.ScreenRefreshRequest;
import frogmodaiEngine.events.TryToHit;
import frogmodaiEngine.events.HPAtZero.After;
import frogmodaiEngine.events.HPAtZero.During;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class PlayerSystem extends BaseSystem {

	ComponentMapper<IsPlayer> mIsPlayer;
	ComponentMapper<IsFaction> mIsFaction;
	ComponentMapper<TimedActor> mTimedActor;
	ComponentMapper<ChunkAddress> mChunkAddress;
	ComponentMapper<Position> mPosition;

	EventSystem es;
	
	FrogmodaiEngine _p;

	public PlayerSystem(FrogmodaiEngine __p) {
		super(); // ??
		_p = __p;
	}

	@Override
	protected void processSystem() {
		
	}
	
	public void astarTest() {
		int _player = FrogmodaiEngine.playerID;
		ChunkAddress ca = mChunkAddress.get(_player);
		Chunk chunk = FrogmodaiEngine.worldManager.getChunk(ca.worldID);
		Position pos = mPosition.get(_player);
		ArrayList<Integer> path = chunk.findPath(pos.x, pos.y, 5, 5);
		
		PScreen screen = _p.screen;
		Position camPos = mPosition.get(FrogmodaiEngine.cameraID);
		
		if (path != null && camPos != null) {
			for (int e : path) {
				Position tilePos = mPosition.get(e);
				int screenX = tilePos.x - camPos.x;
				int screenY = tilePos.y - camPos.y;
				//System.out.println(screenX + " " + screenY);
				screen.set(screenX, screenY, new PTile('X', ColorUtils.Colors.YELLOW, ColorUtils.Colors.BLUE));
			}
		}
	}
	
	@Subscribe 
	public void PostTileRenderingListener(PostTileRendering event) {
		//astarTest();
	}
	
	@Subscribe
	public void ActorTakeTurnDuringListener(ActorTakeTurn.During event) {
		if (!mIsPlayer.has(event.entity))
			return;
		
		FrogmodaiEngine.logEventReceive("PlayerSystem", "ActorTakeTurn.During");
		
		//KeyStroke keystroke = FrogmodaiEngine.keystroke;
		int e = event.entity;
		TimedActor timedActor = mTimedActor.create(e);
		int MOVE_COST = -1; //The default is to wait another cycle
		//Until pre-requisites are met
		//TODO: Other actors having limited number of cycles to make a decision???

		// virtualController.moveX = 0.0f;
		// virtualController.moveY = 0.0f;

		//if (keystroke != null) {
		if (_p.keyPressed) {
			
			FrogmodaiEngine.logEventEmit("PlayerSystem", "MoveAttempt"); //Just fire on every key for now
			
			//KeyType keytype = keystroke.getKeyType();
			boolean coded = _p.key == _p.CODED;
			boolean moving = true;
			if (coded && _p.keyCode == _p.RIGHT)
				MoveAttempt.run(e, 1, 0);
				//virtualController.addAction(new MoveCommand(1, 0));
			else if (coded && _p.keyCode == _p.LEFT)
				MoveAttempt.run(e, -1, 0);
				//virtualController.addAction(new MoveCommand(-1, 0));
			else if (coded && _p.keyCode == _p.UP)
				MoveAttempt.run(e, 0, -1);
				//virtualController.addAction(new MoveCommand(0, -1));
			else if (coded && _p.keyCode == _p.DOWN)
				MoveAttempt.run(e, 0, 1);
				//virtualController.addAction(new MoveCommand(0, 1));
			else if (!coded && _p.key == '1') {
				MoveAttempt.run(e, -1, 1);
				//virtualController.addAction(new MoveCommand(-1, 1));
			} else if (!coded && _p.key == '3') {
				MoveAttempt.run(e, 1, 1);
				//virtualController.addAction(new MoveCommand(1, 1));
			} else if (!coded && _p.key == '7') {
				MoveAttempt.run(e, -1, -1);
				//virtualController.addAction(new MoveCommand(-1, -1));
			} else if (!coded && _p.key == '9') {
				MoveAttempt.run(e, 1, -1);
				//virtualController.addAction(new MoveCommand(1, -1));
			} else {
				moving = false;
			}
			if (moving) {
				
				MOVE_COST = (int) (timedActor.speed * 1.0f); // moving should take a majority of your energy
				//I guess just send a draw request every time the player's turn ends???
				FrogmodaiEngine.logEventEmit("PlayerSystem", "ScreenRefreshRequest");
				es.dispatch(new ScreenRefreshRequest());
			}
			
			if (!coded) {
				char c = _p.key;//keystroke.getCharacter();
				if (c == 'p') {
					//virtualController.addAction(new PickupCommand());
					MOVE_COST = (int) (timedActor.speed * -0.1f);
					//Pause to open menu
					//Except I don't want to add the menu yet, just checking the local tile for objects
				}
				if (c == 'd') {
					//virtualController.addAction(new DropCommand());
					MOVE_COST = (int) (timedActor.speed * -0.1f);
					//Pause to open menu
					//Except I don't want to add the menu yet, just checking the local tile for objects
				}
			}
			
			FrogmodaiEngine.logEventEmit("PlayerSystem", "PlayerTookTurn");
			es.dispatch(new PlayerTookTurn());
			
			//FrogmodaiEngine.keystroke = null; // KEYSTROKES SHOULD NOT COUNT MORE THAN ONCE
			//_p.key = KeyEvent.VK_UNDEFINED;
		}

		event.actionCost += MOVE_COST;
		event.passing = false;
	}

	@Subscribe
	public void OnTouchListener(frogmodaiEngine.events.OnTouch event) {
		//System.out.println(event.entity + ", " + mIsPlayer.has(event.entity) + ", " + event.neighbor + ", " + mIsPlayer.has(event.neighbor));
		if (!mIsPlayer.has(event.entity))
			return;
		
		FrogmodaiEngine.logEventReceive("PlayerSystem", "OnTouch");
		
		if (mIsFaction.has(event.neighbor)) {
			IsFaction faction = mIsFaction.get(event.neighbor);
			if (faction.name.equals("MONSTERS")) {
				// falcon PUNCH (try to hit)
				TryToHit.run(event.entity, event.neighbor);
			}
		}
	}

}

/*
 * ChangeStat.Before before = new ChangeStat.Before("HP", e, -1);
 * es.dispatch(before); if (!before.isCancelled()) { ChangeStat.During during =
 * new ChangeStat.During("HP", e, -1); es.dispatch(during); if
 * (!during.isCancelled()) { ChangeStat.After after = new ChangeStat.After("HP",
 * e, -1); es.dispatch(after); } }
 */