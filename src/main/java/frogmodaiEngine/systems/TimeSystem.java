package frogmodaiEngine.systems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.components.*;
import frogmodaiEngine.events.ActorTakeTurn;
import net.mostlyoriginal.api.event.common.EventSystem;

public class TimeSystem extends IteratingSystem {

	ComponentMapper<TimedActor> mTimedActor;
	ComponentMapper<IsDead> mIsDead;
	
	EventSystem es;
	
	int ticks = 0;
	
	public LinkedList<Integer> queue;
	int currentActor = -1;
	boolean currentActorRemoved = false;
	int lockCount = 0;
	public int actedCount = 0;
	
	public TimeSystem() {
		super(Aspect.all(TimedActor.class));
		queue = new LinkedList<Integer>();
	}
	
	public boolean tick(int index) { //WHOLE SYSTEM
		if (lockCount > 0)
			return false;
		
		int a;
		try {
			a = queue.getFirst();
		} catch(NoSuchElementException e) {
			return false;
		}
		
		//System.out.printf("%d %d\n", index, a);
		
		TimedActor actor = mTimedActor.create(a);
		while (actor.energy > 0 && !actor.isFrozen && !mIsDead.has(a)) {
			currentActor = a;
			ActorTakeTurn event = new ActorTakeTurn(a);
			es.dispatch(event);
			int actionCost = event.actionCost;
			if (event.passing) actionCost = actor.energy;
			//int actionCost = actor.act.apply(a); // THIS WHERE ACTOR DO
			currentActor = -1;
			
			if (currentActorRemoved) {
				currentActorRemoved = false;
				return true;
			}
			
			if (actionCost < 0) {
				if (actionCost < -1) //let -1 still not pass time (HACKY LOL)
					actor.energy -= Math.abs(actionCost); //So that actions that break out still use energy
				return false;
			}
			
			actor.energy -= actionCost;
			
			if (lockCount > 0)
				return false;
		}
		
		if (!actor.isFrozen)
			actor.energy += actor.speed;
		
		queue.add(queue.pop());
		
		ticks++;
		return true;
	}
	
	public void lock() {
		lockCount++;
	}
	
	public void unlock() {
		if (lockCount == 0)
			return;
		lockCount--;
	}

	@Override
	protected void process(int e) { //PER ENTITY
		//Not actually needed???
	}
	
	@Override
	protected void inserted(int e) {
		queue.add(e);
	}
	
	@Override
	protected void removed(int e) {
		queue.remove((Integer) e);
		
		if (currentActor == e)
			currentActorRemoved = true;
	}
	
	public int getNumActors() {
		return queue.size();
	}
	
	int getTicks() {
		return ticks;
	}

}
