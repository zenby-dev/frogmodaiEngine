package frogmodaiEngine.systems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.components.*;
import frogmodaiEngine.events.ActorDied;
import frogmodaiEngine.events.ActorTakeTurn;
import frogmodaiEngine.events.ChangeStat;
import frogmodaiEngine.events.HPAtZero;
import frogmodaiEngine.events.MoveAttempt;
import frogmodaiEngine.events.ScreenRefreshRequest;
import frogmodaiEngine.events.TryToHit;
import frogmodaiEngine.events.HPAtZero.After;
import frogmodaiEngine.events.HPAtZero.During;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class GoblinSystem extends BaseSystem {

	ComponentMapper<IsGoblin> mIsGoblin;
	ComponentMapper<IsFaction> mIsFaction;
	ComponentMapper<TimedActor> mTimedActor;
	ComponentMapper<Sight> mSight;
	ComponentMapper<Tile> mTile;
	ComponentMapper<IsPlayer> mIsPlayer;
	ComponentMapper<Position> mPosition;

	EventSystem es;

	public GoblinSystem() {
		super(); // ??

	}

	@Override
	protected void processSystem() {

	}

	@Subscribe(ignoreCancelledEvents = true)
	public void TryToHitDuringListener(TryToHit.During event) {
		if (!mIsGoblin.has(event.target))
			return;

		//FrogmodaiEngine.sendMessage("The goblin squeals as you punch it!");
		FrogmodaiEngine.sendMessage("You give the goblin a pat and it squeaks happily.");

		//ChangeStat.run("HP", event.target, -1);
	}

	public void moveRelative(int e, int relx, int rely) { //Walks in a direction vector
		int dx = relx == 0 ? 0 : Math.abs(relx)/relx;
		int dy = rely == 0 ? 0 : Math.abs(rely)/rely;
		if (!(dx == 0 && dy == 0)) {
			MoveAttempt.run(e, dx, dy);
		}
	}

	public boolean moveRandom(int e) {
		int r = FrogmodaiEngine.random.nextInt(9);
		if (r == 0)
			MoveAttempt.run(e, 1, 0);
		// virtualController.addAction(new MoveCommand(1, 0));
		else if (r == 1)
			MoveAttempt.run(e, -1, 0);
		// virtualController.addAction(new MoveCommand(-1, 0));
		else if (r == 2)
			MoveAttempt.run(e, 0, -1);
		// virtualController.addAction(new MoveCommand(0, -1));
		else if (r == 3)
			MoveAttempt.run(e, 0, 1);
		// virtualController.addAction(new MoveCommand(0, 1));
		else if (r == 4) {
			MoveAttempt.run(e, -1, 1);
			// virtualController.addAction(new MoveCommand(-1, 1));
		} else if (r == 5) {
			MoveAttempt.run(e, 1, 1);
			// virtualController.addAction(new MoveCommand(1, 1));
		} else if (r == 6) {
			MoveAttempt.run(e, -1, -1);
			// virtualController.addAction(new MoveCommand(-1, -1));
		} else if (r == 7) {
			MoveAttempt.run(e, 1, -1);
			// virtualController.addAction(new MoveCommand(1, -1));
		} else {
			return false;
		}
		return true;
	}

	@Subscribe
	public void ActorTakeTurnListener(ActorTakeTurn event) {
		if (!mIsGoblin.has(event.entity))
			return;

		IsGoblin goblin = mIsGoblin.get(event.entity);
		Sight sight = mSight.get(event.entity);
		Position gobPos = mPosition.get(event.entity);

		//KeyStroke keystroke = FrogmodaiEngine.keystroke;
		int e = event.entity;
		TimedActor timedActor = mTimedActor.create(e);
		int MOVE_COST = -1; // The default is to wait another cycle
		// Until pre-requisites are met
		// TODO: Other actors having limited number of cycles to make a decision???

		// virtualController.moveX = 0.0f;
		// virtualController.moveY = 0.0f;
		if (goblin.aiState == 2) goblin.aiState = 1;
		int relx = 0;
		int rely = 0;
		for (RelativePosition rel : sight.visibleTiles.values()) {
			Tile tile = mTile.get(rel.e);
			for (Integer vis : tile.entitiesHere) {
				if (mIsPlayer.has(vis)) {
					//
					
					/// TOOODOOOO::::
					///
					
					//
					//
					//     actually be able to figure out relative positions of objects
					//   without it being slow bullshit
					//
					goblin.aiState = 2;
					relx = rel.x - gobPos.x; //get direction vector
					rely = rel.y - gobPos.y;
				}
			}
		}
		
		if (goblin.aiState == 2) {
			MOVE_COST = (int) (timedActor.speed * 1.0f);
			moveRelative(e, relx, rely);
		} else if (goblin.aiState == 0) {
			MOVE_COST = (int) (timedActor.speed * 1.0f);
			if (FrogmodaiEngine.random.nextInt(10) < 3)
				goblin.aiState = 1; //Start wandering
		} else if (goblin.aiState == 1) {
			boolean moving = moveRandom(e);
			if (!moving) {
				MOVE_COST = 1;
			} else {
				MOVE_COST = (int) (timedActor.speed * 1.0f); // goblin should move ridiculously fast
			}
			if (FrogmodaiEngine.random.nextInt(10) < 1)
				goblin.aiState = 0; //Start standing idly
		}

		event.actionCost += MOVE_COST;
		event.passing = false;
	}

}