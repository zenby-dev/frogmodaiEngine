package frogmodaiEngine.components;

import java.util.Random;

import com.artemis.Component;

import frogmodaiEngine.ColorUtils;
import frogmodaiEngine.PTile;

//0 = Black
//1 = Red
//2 = Green
//3 = Yellow
//4 = Blue
//5 = Magenta
//6 = Cyan
//7 = White
//8 = Default

public class Char extends Component {
	// byte type;
	public PTile tile;
	// short properties;
	// FFObject tileObject;

	public Char(char _character, byte _fgc, byte _bgc) {
		tile = new PTile(_character, _fgc, _bgc);
	}

	public Char() { // default is green period on black background (grass)
		// type = 0;
		// properties = 0;
		//fgc = 2;
		//bgc = 0;
		Random r = new Random();
		//character = r.nextInt(10) > 3 ? '.' : ',';
		//bold = false;
		// tileObject = new FFObject();
		tile = new PTile(r.nextInt(10) > 3 ? '.' : ',', ColorUtils.randomHSB(), ColorUtils.Colors.BLACK);
	}

	public PTile getTextCharacter() {
		return new PTile(tile);
	}
	
	/*public PTile getTextCharacter(int f, int b, boolean bo) {
		if (bo) {
			return new PTile(character, f, b, SGR.BOLD);
		} else {
			return new PTile(character, TextColor.ANSI.values()[f], TextColor.ANSI.values()[b]);
		}
	}
	
	public PTile getTextCharacter(boolean bo) {
		if (bo) {
			return new PTile(character, TextColor.ANSI.values()[fgc], TextColor.ANSI.values()[bgc], SGR.BOLD);
		} else {
			return new PTile(character, TextColor.ANSI.values()[fgc], TextColor.ANSI.values()[bgc]);
		}
	}*/
}
