package frogmodaiEngine;

public class PTileStyle {
	public int fgc;
	public int bgc;
	public boolean flipped = false;
	public int rotation = 0;
	
	public PTileStyle(int _fgc, int _bgc) {
		fgc = _fgc;
		bgc = _bgc;
	}
	
	public PTileStyle(int _fgc, int _bgc, boolean _flipped) {
		fgc = _fgc;
		bgc = _bgc;
		flipped = _flipped;
	}
	
	public PTileStyle(int _fgc, int _bgc, boolean _flipped, int _rotation) {
		fgc = _fgc;
		bgc = _bgc;
		flipped = _flipped;
		rotation = _rotation;
	}
	
	public PTileStyle(PTileStyle t) {
		fgc = t.fgc;
		bgc = t.bgc;
		flipped = t.flipped;
		rotation = t.rotation;
	}
}
