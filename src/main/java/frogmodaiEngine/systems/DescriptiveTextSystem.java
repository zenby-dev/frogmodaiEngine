package frogmodaiEngine.systems;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

import com.artemis.Aspect;
import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.World;
import com.artemis.annotations.EntityId;
import com.artemis.systems.IteratingSystem;

import frogmodaiEngine.PScreen;
import frogmodaiEngine.PTerminal;
import frogmodaiEngine.PTile;
import frogmodaiEngine.Chunk;
import frogmodaiEngine.ColorUtils;
import frogmodaiEngine.FrogmodaiEngine;
import frogmodaiEngine.Paragraph;
import frogmodaiEngine.TextSegment;
import frogmodaiEngine.iVec2;
import frogmodaiEngine.components.*;

public class DescriptiveTextSystem extends IteratingSystem { // This is for terrain only
	PScreen screen;
	@EntityId
	public int perspective = -1;
	boolean fullRedraw = false;
	PTile emptyCharacter;
	double PI = 3.14159265;

	public boolean drewThisFrame = false;

	PScreen buffer;
	iVec2 bufferPosition;
	iVec2 bufferSize;

	ArrayDeque<Paragraph> paragraphs;
	int maxParagraphs = 40;
	
	FrogmodaiEngine _p;

	public DescriptiveTextSystem(FrogmodaiEngine __p, PScreen _screen) { // Matches camera, not tiles, for performance
		super(Aspect.all(Position.class, CameraWindow.class));
		_p = __p;
		
		bufferPosition = new iVec2(FrogmodaiEngine.screenWidth / 2, 0);
		bufferSize = new iVec2(FrogmodaiEngine.screenWidth / 2, FrogmodaiEngine.screenHeight);

		screen = _screen;
		//System.out.printf("%d, %d\n", FrogmodaiEngine.screenWidth / 2, FrogmodaiEngine.screenHeight);
		//buffer = new ScreenBuffer(bufferSize, new TextCharacter(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK));
		//emptyCharacter = new TextCharacter(' ', TextColor.ANSI.BLACK, TextColor.ANSI.BLACK);
		buffer = new PScreen(_p, new PTerminal(_p, 32, 32));
		
		paragraphs = new ArrayDeque<Paragraph>();

		triggerRedraw();
	}

	public void addParagraph(Paragraph p) {
		paragraphs.addLast(p);
		if (paragraphs.size() > maxParagraphs) {
			paragraphs.pollFirst();
		}
		triggerRedraw();
	}

	public void triggerRedraw() {
		fullRedraw = true;
	}

	@Override
	protected void process(int e) { // this happens with high frequency
		drewThisFrame = false;

		// fullRedraw = true;
		if (fullRedraw) {
			clearBuffer();

			// DO DRAWING STUFF HERE
			drawBorder();
			drawParagraphs();

			// Renders this buffer to the screen
			/*TextGraphics tg = screen.newTextGraphics();
			tg.fillRectangle(bufferPosition, bufferSize, emptyCharacter);
			tg.drawImage(new iVec2(FrogmodaiEngine.screenWidth / 2, 0), buffer);*/
			drawToScreen();

			fullRedraw = false;
			drewThisFrame = true;
		}

	}
	
	public void drawToScreen() {
		screen.fill(emptyCharacter);
		screen.noStroke();
		screen.rect(screen.terminal.resX/2, 0, screen.terminal.resX/2, screen.terminal.resY);
		screen.image(buffer, 32, 0);
	}
	
	private void setCharacter(int x, int y, PTile tile) {
		buffer.set(x, y, tile);
	}

	private void clearBuffer() {
		//buffer.newTextGraphics().fillRectangle(new TerminalPosition(0, 0), buffer.getSize(), emptyCharacter);
	}

	// buffer.setCharacterAt(screenPos.x, screenPos.y,
	// character.getTextCharacter());

	private void drawBorder() {
		/*TextGraphics tg = buffer.newTextGraphics();
		tg.drawRectangle(new TerminalPosition(0, 0), bufferSize,
				new TextCharacter('*', ANSI.YELLOW, ANSI.BLUE, SGR.BOLD));*/
		buffer.stroke(new PTile('*', ColorUtils.Colors.YELLOW, ColorUtils.Colors.BLUE));
		buffer.noFill();
		buffer.rect(0, 0, buffer.terminal.resX, buffer.terminal.resY);
	}

	private void drawParagraphs() {
		int py = buffer.terminal.resY - 2;
		Paragraph[] ps = new Paragraph[paragraphs.size()];
		paragraphs.toArray(ps);
		for (int i = paragraphs.size() - 1; i >= 0; i--) {
			Paragraph para = ps[i];
			int numLines = wrappedNumLines(para);
			py -= numLines;
			drawWrappedLines(py, para);
			py--;
			if (py < 1) break;
		}
	}
	// buffer.setCharacterAt(1, py, new TextCharacter('@', ANSI.CYAN, ANSI.GREEN));

	private void drawWrappedLines(int py, Paragraph p) {
		int width = buffer.terminal.resX - 2;
		int x = 0;
		int y = 0;
		for (TextSegment ts : p.segments) {
			// x += ts.text.length();
			String text = ts.text.toString();
			if (x + text.length() > width) {
				while (x + text.length() > width) {
					// Starting from the beginning of the string,
					// use indexOf(' ') to chop off words from the original string
					// until the next one would make it too long.
					// Draw that segment and repeat.
					StringBuilder str1 = new StringBuilder();
					String str2 = text.toString();
					int i = 0;
					int nx = x;
					int nextIndex = str2.indexOf(' ');
					while (nx + nextIndex+1 < width) {
						nx += nextIndex+1;
						str1.append(str2.subSequence(i, nextIndex+1));
						str2 = str2.substring(nextIndex+1);
						nextIndex = str2.indexOf(' ');
						if (nextIndex == -1) nextIndex = str2.length()-1;
						// x -= width;
						// y++;
					}
					TextSegment nts = new TextSegment(str1.toString(), ts.style);
					if (py+y > 0)
						drawSegment(1 + x, py + y, nts);
					text = str2.toString();
					y++;
					x = 0;
				}
			} 
			if (x + text.length() < width) {
				if (py+y > 0)
					drawSegment(1 + x, py + y, new TextSegment(text, ts.style));
				x += text.length();
			}
		}
	}

	private void drawSegment(int sx, int sy, TextSegment ts) {
		for (int i = 0; i < ts.text.length(); i++) {
			char c = ts.text.charAt(i);
			// System.out.printf("%d %d %c\n", sx+i, sy, c);
			/*if (ts.sgr == null) {
				buffer.setCharacterAt(sx + i, sy, new TextCharacter(c, ts.foreground, ts.background));
			} else {
				buffer.setCharacterAt(sx + i, sy, new TextCharacter(c, ts.foreground, ts.background, ts.sgr));
			}*/
			setCharacter(sx + i, sy, new PTile(c, ts.style));
		}
	}

	private int wrappedNumLines(Paragraph p) {
		int width = buffer.terminal.resX - 2;
		int x = 0;
		int y = 0;
		for (TextSegment ts : p.segments) {
			// x += ts.text.length();
			String text = ts.text.toString();
			if (x + text.length() > width) {
				while (x + text.length() > width) {
					// Starting from the beginning of the string,
					// use indexOf(' ') to chop off words from the original string
					// until the next one would make it too long.
					// Draw that segment and repeat.
					StringBuilder str1 = new StringBuilder();
					String str2 = text.toString();
					int i = 0;
					int nx = x;
					int nextIndex = str2.indexOf(' ');
					while (nx + nextIndex+1 < width) {
						nx += nextIndex+1;
						str1.append(str2.subSequence(i, nextIndex+1));
						str2 = str2.substring(nextIndex+1);
						nextIndex = str2.indexOf(' ');
						if (nextIndex == -1) nextIndex = str2.length()-1;
						// x -= width;
						// y++;
					}
					//TextSegment nts = new TextSegment(str1.toString(), ts.foreground, ts.background, ts.sgr);
					//drawSegment(1 + x, py + y, nts);
					text = str2.toString();
					y++;
					x = 0;
				}
			} 
			if (x + text.length() < width) {
				//drawSegment(1 + x, py + y, new TextSegment(text, ts.foreground, ts.background, ts.sgr));
				x += text.length();
			}
		}
		return y;
	}

}
