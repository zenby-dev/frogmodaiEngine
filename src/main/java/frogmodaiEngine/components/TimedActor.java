package frogmodaiEngine.components;

import java.util.function.*;

import com.artemis.Component;

public class TimedActor extends Component {
	public static int TICK_ENERGY = 100;
	public int energy;
	public int speed;
	//public Function<Integer,Integer> act; //might have to change this to be a function
	//TODO: How to serialize this?
	//Have a reference in this component, and another component that grabs from a list based on reference?
	public boolean isFrozen = false;
}
