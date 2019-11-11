package frogmodaiEngine;

import processing.core.PApplet;

public class PScreen {
	PApplet _p;
	public PTerminal terminal;
	PTile fillCharacter;
	PTile strokeCharacter;
	
	public PScreen(PApplet __p, PTerminal _terminal) {
		_p = __p;
		terminal = _terminal;
		fillCharacter = new PTile(' ', _p.color(0,0,0), _p.color(0,0,0));
		strokeCharacter = new PTile(' ', _p.color(0,0,0), _p.color(0,0,0));
	}
	
	public void fill(PTile c) {
		fillCharacter = c;
	}
	
	public void noFill() {
		fillCharacter = null;
	}
	
	public void stroke(PTile c) {
		strokeCharacter = c;
	}
	
	public void noStroke() {
		strokeCharacter = null;
	}
	
	//set character at (fall through to terminal)
	public void set(int x, int y, char character, int fgc, int bgc) {
		set(x, y, new PTile(character, fgc, bgc));
	}
	
	public void set(int x, int y, PTile c) {
		terminal.set(x, y, c);
	}
	
	//clear screen to color (background)
	public void background(int c) {
		for (int y = 0; y < terminal.resY; y++) {
			for (int x = 0; x < terminal.resX; x++) {
				set(x,y, ' ', _p.color(0,0,0), c);
			}
		}
	}
	
	//draw/fill rectangle (maybe make it more like processing with fill and stroke?)
	public void rect(int x0, int y0, int w, int h) {
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if ((x == 0 || x == w-1 || y == 0 || y == h-1) && strokeCharacter != null) {
					set(x0+x, y0+y, strokeCharacter);
				} else if (fillCharacter != null) {
					set(x0+x, y0+y, fillCharacter);
				}
			}
		}
	}
	
	//draw line
	public void line(int x0, int y0, int x1, int y1) {
		int[] points = findLine(x0,y0,x1,y1);
		for (int i = 0; i < points.length; i+=2) {
			int x = points[i];
			int y = points[i+1];
			if (x == -9999 || y == -9999) break;
			set(x, y, strokeCharacter);
		}
	}
	
	//print string
	public void print(String str, int x, int y) {
		int x0 = 0;
		for (char c : str.toCharArray()) {
			set(x+x0, y, new PTile(c, strokeCharacter.style.fgc, strokeCharacter.style.bgc));
			x0++;
		}
	}
	
	//draw image/other screen
	public void image(PScreen other, int x0, int y0) { //TEST ME
		int w = other.terminal.resX;
		int h = other.terminal.resY;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int thisI = (y0+y) * terminal.resX + (x0+x);
				int otherI = (y) * other.terminal.resX + (x);
				if (terminal.inBounds(x0+x, y0+y) && other.terminal.inBounds(x, y)) {
					set(x0+x, y0+y, other.terminal.get(x, y));
				}
			}
		}
	}
	
	private int[] findLine(int x0, int y0, int x1, int y1) {
		int length = (int) Math.ceil(Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1)));
		// System.out.println(length);
		if (length == 0)
			return null;
		int[] line = new int[(length) * 2 + 4]; // allocate extra space
		// for (int i = 0; i < line.length; i++)
		// line[i] = -1;
		int c = 0;

		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int e2;

		while (true) { // TODO: adjust to tile.neighbor based
			if (c < line.length) {
				line[c] = x0;
				line[c + 1] = y0;
				// System.out.println(String.format("%d,%d", x0, y0));
				c += 2;
				// if (c >= line.length) break;
			}
			// line.add(XYToi(x0, y0));

			if (x0 == x1 && y0 == y1)
				break;

			e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x0 = x0 + sx;
			}

			if (e2 < dx) {
				err = err + dx;
				y0 = y0 + sy;
			}
		}

		if (c < line.length) { // Mark points beyond end, if they exist (should there be a -1?)
			line[c] = -9999;
			line[c + 1] = -9999;
		}
		// System.out.println("");
		return line;
	}
}
