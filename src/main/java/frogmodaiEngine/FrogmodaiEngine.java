package frogmodaiEngine;

import java.util.Random;

import frogmodaiEngine.WorldManager;
import frogmodaiEngine.events.KeyboardInput;
import frogmodaiEngine.events.ProcessTurnCycle;
import frogmodaiEngine.events.ProcessWorld;
import frogmodaiEngine.Paragraph;
import frogmodaiEngine.TextSegment;
import frogmodaiEngine.systems.DescriptiveTextSystem;
import net.mostlyoriginal.api.event.common.EventSystem;
import net.mostlyoriginal.api.event.common.Subscribe;
import frogmodaiEngine.ArchetypeBuilders;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;
//import pluralist.PluraLib;
//import pluralist.generators.Textile;
//import pluralist.postprocessing.Dither;

public class FrogmodaiEngine extends PApplet {

	PShader lineShader;
	PShape cube;
	float angle = 0.0f;
	int FRAME = 0;

	// Textile textile;
	// Dither dither;
	PGraphics buffer;
	PTerminal terminal;
	public PScreen screen;

	public static WorldManager worldManager;
	public static iVec2 terminalSize;
	// public static KeyStroke keystroke;
	public static int playerID = -1;
	public static int cameraID = -1;
	static int loopSpeed = 10;
	public static Random random;
	public static int screenWidth = 64 * 2;
	public static int screenHeight = 32;
	
	public float keyHeldAt = -1.0f;
	public int keyRepeatDelay = 4;
	public float keyRepeatLength = 0.15f;
	public boolean keyFirstFired = false;
	public boolean keyRepeatFired = false;
	
	public EventSystem es;

	public void settings() {
		size(512*2, 512, P2D);
		noSmooth();
	}

	public void setup() {
		((PGraphicsOpenGL) g).textureSampling(2);
		// initPluraLib();

		// textile = new Textile(this);
		// dither = new Dither(this);

		buffer = createGraphics(width, height, P2D);
		terminal = new PTerminal(this, 64, 32);
		screen = new PScreen(this, terminal);
		random = new Random();
		
		// Initiate the world
		worldManager = new WorldManager(this, screen);
		//worldManager.registerEvents(this);
		
		// Initiate Archetypes
		ArchetypeBuilders.initArchetypes();
		
		// Load Chunks
		worldManager.start();
		processWorld();
		processTurnCycle();

		//mainLoop();

		draw();
	}

	public void draw() {
		// updateTime();

		renderLoop();
	}
	
	public void doRedraw() {
		if (worldManager.refreshNeeded()) {
			doRender();
		}
	}
	
	public void processWorld() { //?????????
		//logEventEmit("FrogmodaiEngine", "ProcessWorld");
		worldManager.ProcessWorldListener(new ProcessWorld());
	}
	
	public void processTurnCycle() { //??????
		logEventEmit("FrogmodaiEngine", "ProcessTurnCycle");
		worldManager.ProcessTurnCycleListener(new ProcessTurnCycle());
	}

	public void renderLoop() {
		//This is the standard loop in processing
		//It should happen real-time, always
		//It controls things that should always be updated, even between game events
		//Like animations and shaders and other stuff
		
		background(0);
		
		if (keyHeldAt > 0.0f && (seconds()-keyHeldAt > keyRepeatLength*(keyRepeatDelay+1))) {
			keyHeldAt = seconds()-keyRepeatLength*keyRepeatDelay;
			//log(seconds()-keyHeldAt + ", " + keyRepeatLength*keyRepeatDelay);
			keyRepeatFired = false;
		}
		
		//log(seconds()-keyHeldAt + ", " + keyRepeatLength*keyRepeatDelay + ", " + !keyRepeatFired);
		
		//worldManager.process();
		if ((keyHeldAt > 0.0f && !keyFirstFired) || 
				(keyHeldAt > 0.0f && seconds()-keyHeldAt >= keyRepeatLength*keyRepeatDelay && !keyRepeatFired)) {
			keyFirstFired = true;
			keyRepeatFired = true;
			//log("boop");
			logEventEmit("FrogmodaiEngine", "PlayerKeyboardInput");
			es.dispatch(new KeyboardInput(key, keyCode));
		}
		
		/*if (FRAME % 8 == 0) {
			es.dispatch(new ProcessTurnCycle());
		}*/
		
		processWorld();
		//es.dispatch(new ProcessWorld()); //Constantly processed systems

		// Redraw screen
		doRedraw();

		/*if (FRAME % 8 == 0) {
			doRender();
		}*/

		pushMatrix();
		scale(1);
		image(terminal.buffer, 0, 0);
		popMatrix();

		// image(textile.buffer,0,0);
		// image(dither.buffer,0,0);
		FRAME++;
	}
	
	@Subscribe
	public void PlayerKeyboardInputListener(KeyboardInput event) {
		logEventReceive("FrogmodaiEngine", "PlayerKeyboardInput");
		processTurnCycle();
		//es.dispatch(new ProcessTurnCycle());
	}
	
	public int terminalMouseX() {
		return terminal.screenToTerminalX(mouseX);
	}
	
	public int terminalMouseY() {
		return terminal.screenToTerminalY(mouseY);
	}
	
	//TODO: Screen-to-Camera coordinates???
	//Picking tiles by mouse
	//Getting debug info on clicked tile

	void doRender() {
		int x = terminal.screenToTerminalX(mouseX);
		int y = terminal.screenToTerminalY(mouseY);

		// terminal.set(x, y, (char)random(128), randomColor(), randomColor());
		// screen.background(color(0, 0, 0));
		/*
		 * int w = (int) random(terminal.resX / 1.3f); int h = (int)
		 * random(terminal.resY / 1.3f); int x0 = (int) random(terminal.resX - w + 1);
		 * int y0 = (int) random(terminal.resY - h + 1); screen.fill(new
		 * PTextCharacter('/', randomColor(), randomColor())); screen.stroke(new
		 * PTextCharacter((char)random(128), randomColor(), randomColor()));
		 * screen.rect(x0, y0, w, h);
		 */

		int x0 = (int) random(terminal.resX);
		int y0 = (int) random(terminal.resY);
		// int x1 = (int) random(terminal.resX);
		// int y1 = (int) random(terminal.resY);
		// screen.fill(new PTextCharacter('/', randomColor(), randomColor()));
		//screen.stroke(new PTile(' ', randomColor(), randomColor()));
		// screen.print("Howdy", x0, y0);

		terminal.render();
	}

	public int randomColor() {
		colorMode(HSB, 255);
		int c = color(random(255), 255.0f, 255.0f);
		colorMode(RGB, 255);
		return c;
	}

	public void keyPressed() {
		//log("pressed");
		keyHeldAt = seconds();
		keyFirstFired = false;
		keyRepeatFired = false;
	}
	
	public void keyReleased() {
		//log("released");
		keyHeldAt = -1.0f;
		keyFirstFired = false;
		keyRepeatFired = false;
	}

	// DOING THIS FOR EASE OF PROGRAMMING BULLSHIT
	public static void sendMessage(Paragraph p) {
		worldManager.world.getSystem(DescriptiveTextSystem.class).addParagraph(p);
	}

	public static void sendMessage(TextSegment t) {
		Paragraph para = new Paragraph();
		para.add(t);
		worldManager.world.getSystem(DescriptiveTextSystem.class).addParagraph(para);
	}

	public static void sendMessage(String s) {
		Paragraph para = new Paragraph();
		para.add(s);
		worldManager.world.getSystem(DescriptiveTextSystem.class).addParagraph(para);
	}
	
	public static void delete(int e) {
		worldManager.world.delete(e);
	}
	
	public static void log(String str) {
		System.out.println(str);
	}
	
	public static void logEventEmit(String systemName, String eventName) {
		System.out.printf("%s <<< %s\n", eventName, systemName);
	}
	
	public static void logEventReceive(String systemName, String eventName) {
		System.out.printf("%s >>> %s\n", eventName, systemName);
	}
	
	public float seconds() {
		return millis()/1000.0f;
	}

	///////////////////////////////
	///////////////////////////////
	///////////////////////////////

	public static void main(String _args[]) {
		PApplet.main(new String[] { frogmodaiEngine.FrogmodaiEngine.class.getName() });
	}
}
