package frogmodaiEngine.systems;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.artemis.Aspect;
import com.artemis.Aspect.Builder;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.components.*;
import frogmodaiEngine.events.ChangeStat;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;

public class StatSystem extends IteratingSystem {

	EventSystem es;
	
	ComponentMapper<Stat> mStat;
	
	public StatSystem() {
		super(Aspect.one(Stat.class)); //??

	}

	@Override
	protected void process(int e) {
		
	}
	
	//How am I supposed to handle subclasses of Stat???????
	//AAAA!!!
	
	//I don't want a monolithic stat component if different creatures have different stats???
	//But i don't know if i can get all components that are subclasses of Stat in a list???
	//And I don't want a system for every single stat!!!
	//Iterating over every component in an entity looking for a subclass of Stat with the right name sounds ugly
	
	//A master StatSystem class and subclass and add a system for each individual stat???
	//I guess most stats won't do much of the same stuff
	//Also the addage "prefer more systems over larger systems/favor small systems over less systems"
	
	//I guess just build up this one and use it as a template
	
	
	@Subscribe(ignoreCancelledEvents = true)
	public void BeforeChangeStatListener(ChangeStat.Before event) {
		Stat stat = mStat.get(event.entity);
		System.out.println(stat.currentValue);
	}

	@Subscribe(ignoreCancelledEvents = true)
	public void DuringChangeStatListener(ChangeStat.During event) {
		Stat stat = mStat.get(event.entity);
		//FrogmodaiEngine.sendMessage("OW!");
		stat.changeValue(event.amount);
	}

	@Subscribe
	public void AfterChangeStatListener(ChangeStat.After event) {
		Stat stat = mStat.get(event.entity);
		System.out.println(stat.currentValue);
	}
}
