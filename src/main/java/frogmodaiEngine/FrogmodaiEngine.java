package frogmodaiEngine;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PShape;
import processing.opengl.PShader;
//import pluralist.PluraLib;
//import pluralist.generators.Textile;
//import pluralist.postprocessing.Dither;

public class FrogmodaiEngine extends PApplet {

	PShader lineShader;
	PShape cube;
	float angle = 0.0f;
	int FRAME = 0;
	
	//Textile textile;
	//Dither dither;
	PGraphics buffer;
	
	public void settings() {
		size(512, 512, P2D);
		noSmooth();
	}

	public void setup() {
		//initPluraLib();
		
		//textile = new Textile(this);
		//dither = new Dither(this);
		
		buffer = createGraphics(width, height, P2D);
		
		doRender();
	}

	public void draw() {
		//updateTime();

		mainLoop();
	}

	public void mainLoop() {
		background(0);
		
		if (FRAME%8==0) {
			doRender();
		}

		//image(textile.buffer,0,0);
		//image(dither.buffer,0,0);
		FRAME++;
	}
	
	void doRender() {
		
	}
	
	public void keyPressed() {
		doRender();
	}

	/*public static void main(String _args[]) {
		PApplet.main(new String[] { frogmodaiEngine.FrogmodaiEngine.class.getName() });
	}*/
}
