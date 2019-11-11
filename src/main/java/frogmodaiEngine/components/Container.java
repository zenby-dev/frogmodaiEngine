package frogmodaiEngine.components;

import java.util.ArrayList;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

public class Container extends Component {
	@EntityId public ArrayList<Integer> list;
	
	public Container() {
		list = new ArrayList<Integer>();
	}
	
	public void addObject(int e) {
		list.add(e);
	}
	
	public void removeObject(int e) {
		list.remove((Integer)e);
	}
}
