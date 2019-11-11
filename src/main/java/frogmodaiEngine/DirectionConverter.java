package frogmodaiEngine;

import frogmodaiEngine.components.Position;

public class DirectionConverter {
	public static Position toPosition(int d) {
		switch(d) {
		case 0:
			return new Position(-1, -1);
		case 1:
			return new Position(0, -1);
		case 2:
			return new Position(1, -1);
		case 3:
			return new Position(-1, 0);
		case 4:
			return new Position(1, 0);
		case 5:
			return new Position(-1, 1);
		case 6:
			return new Position(0, 1);
		case 7:
			return new Position(1, 1);
		}
		return new Position(0, 0);
	}
	
	public static int toInt(Position pos) {
		if (pos.x == -1 && pos.y == -1) return 0;
		if (pos.x == 0 && pos.y == -1) return 1;
		if (pos.x == 1 && pos.y == -1) return 2;
		if (pos.x == -1 && pos.y == 0) return 3;
		if (pos.x == 1 && pos.y == 0) return 4;
		if (pos.x == -1 && pos.y == 1) return 5;
		if (pos.x == 0 && pos.y == 1) return 6;
		if (pos.x == 1 && pos.y == 1) return 7;
		return -1;
	}
}
