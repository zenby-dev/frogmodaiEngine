package frogmodaiEngine.components;

import com.artemis.Component;

public class Position extends Component{
	public int x;
	public int y;
	
	public Position(int _x, int _y) {
		x = _x;
		y = _y;
	}

	public Position() {
		x = 0;
		y = 0;
	}

	public Position set(int _x, int _y) {
		x = _x;
		y = _y;
		return this;
	}
	
	public boolean withinDistance(Position p, float d) { //squared
		return (x-p.x)*(x-p.x) + (y-p.y)*(y-p.y) < d*d;
	}
}
