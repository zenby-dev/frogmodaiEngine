package frogmodaiEngine;

public class PTile { //Not to be confused with the Tile component, which is a map tile... probaly should refactor
	public char character;
	public PTileStyle style;
	boolean changed = true;
	
	public PTile(char _character, int _fgc, int _bgc) {
		character = _character;
		style = new PTileStyle(_fgc, _bgc);
	}
	
	public PTile(char _character, int _fgc, int _bgc, boolean _flipped, int _rotation) {
		character = _character;
		style = new PTileStyle(_fgc, _bgc, _flipped, _rotation);
	}
	
	public PTile(PTile tc) {
		character = tc.character;
		style = new PTileStyle(tc.style);
	}

	public PTile(char _character, PTileStyle _style) {
		character = _character;
		style = new PTileStyle(_style);
	}
}
