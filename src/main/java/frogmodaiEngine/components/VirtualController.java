package frogmodaiEngine.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import com.artemis.Component;

//import frogmodaiGame.Command;

public class VirtualController extends Component {
	/*public Queue<Command> actionList;
	
	public VirtualController() {
		actionList = new LinkedList<Command>();
	}
	
	public void clear() {
		actionList.clear();
	}
	
	public void addAction(Command command) {
		actionList.add(command);
	}
	
	public Command poll() {
		return actionList.poll();
	}
	
	public Command peek() {
		return actionList.peek();
	}*/
}

//This needs to be changed to a system of proposed moves
//Entities act by pushing a proposed move to their queue.
//After processing all ticks for the time system, actions are carried out in systems.
//Systems pop actions off the stack and execute them, and update the world state.
//What is the structure of an intention?
//It's a single action