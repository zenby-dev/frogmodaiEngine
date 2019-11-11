package frogmodaiEngine;

import java.io.BufferedReader;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;

public class PTerminal {
	PApplet _p;

	PShader shader;
	public PGraphics buffer;
	private PFont font;
	private float fontSize = 16.0f;

	public int resX;
	public int resY;

	PTile[] characters;
	
	float bitEncR = 1.0f;
	float bitEncG = 255.0f;
	float bitEncB = 65025.0f;
	float bitEncA = 16581375.0f;
	float bitDecR = 1.0f/bitEncR;
	float bitDecG = 1.0f/bitEncG;
	float bitDecB = 1.0f/bitEncB;
	float bitDecA = 1.0f/bitEncA;

	public PTerminal(PApplet __p, int _resX, int _resY) {
		_p = __p;
		resX = _resX;
		resY = _resY;

		String[] lines = _p.loadStrings("PTerminal.glsl");
		for (int i = 0; i < lines.length; i++) {
			lines[i] = shaderReplace(lines[i]);
			//System.out.println(lines[i]);
		}
		
		shader = _p.loadShader("Empty.glsl");
		shader.setFragmentShader(lines);
		
		buffer = _p.createGraphics((int)(fontSize*resX), (int)(fontSize*resY), _p.P2D);
		font = _p.createFont("Px437/Px437_TandyOld_TV.ttf", fontSize);

		characters = new PTile[resX * resY];
		for (int i = 0; i < characters.length; i++) {
			characters[i] = new PTile(' ', _p.color(0, 0, 0), _p.color(0, 0, 0));
		}
		
		//TODO: Rewrite to render once an atlas for the font
		//Then, write a shader that takes the data arrays as input, and the font atlas
		//EZ PZ
	}
	
	private String shaderReplace(String _line) {
		String line = _line.toString();
		line = line.replaceAll("(resX)", Integer.toString(resX));
		line = line.replaceAll("(resY)", Integer.toString(resY));
		line = line.replaceAll("(res)", Integer.toString(resX*resY));
		line = line.replaceAll("(varying)", "in");
		line = line.replaceAll("(texture2D)", "texture");
		return line;
	}
	
	public void updateShaderVariables() {
		//shader.set("resX", resX);
		//shader.set("resY", resY);
		/*int[] fgR = new int[resX*resY];
		int[] fgG = new int[resX*resY];
		int[] fgB = new int[resX*resY];
		int[] bgR = new int[resX*resY];
		int[] bgG = new int[resX*resY];
		int[] bgB = new int[resX*resY];
		int[] chars = new int[resX*resY];
		for (int i = 0; i < resX*resY; i++) {
			chars[i] = (int)characters[i];
			fgR[i] = (int) _p.red(foregroundColors[i]);
			fgG[i] = (int) _p.green(foregroundColors[i]);
			fgB[i] = (int) _p.blue(foregroundColors[i]);
			bgR[i] = (int) _p.red(backgroundColors[i]);
			bgG[i] = (int) _p.green(backgroundColors[i]);
			bgB[i] = (int) _p.blue(backgroundColors[i]);
		}
		shader.set("fgR", fgR);
		shader.set("fgG", fgG);
		shader.set("fgB", fgB);
		shader.set("bgR", bgR);
		shader.set("bgG", bgG);
		shader.set("bgB", bgB);*/
		
		
		float[] fg = new float[resX*resY];
		int[] bg = new int[resX*resY*3];
		int[] chars = new int[resX*resY];
		for (int i = 0; i < resX*resY; i+=3) {
			PTile tc = characters[i];
			chars[i] = (int)tc.character;
			//backgroundColors[i] = ((FrogmodaiEngine)_p).randomColor();
			//fgR[i] = (int) _p.red(foregroundColors[i]);
			//fgG[i] = (int) _p.green(foregroundColors[i]);
			//fgB[i] = (int) _p.blue(foregroundColors[i]);
			//bg[i] = (int) _p.red(backgroundColors[i]);
			//bg[i+1] = (int) _p.green(backgroundColors[i]);
			//bg[i+2] = (int) _p.blue(backgroundColors[i]);
			//System.out.println(backgroundColors[i]);
		/*	fg[i] = DecodeFloatRGBA(_p.red(foregroundColors[i])/255.0f,
					_p.green(foregroundColors[i])/255.0f,
					_p.blue(foregroundColors[i])/255.0f,
					_p.alpha(foregroundColors[i])/255.0f);
			System.out.println(fg[i]);
			bg[i] = DecodeFloatRGBA(_p.red(backgroundColors[i])/255.0f,
					_p.green(backgroundColors[i])/255.0f,
					_p.blue(backgroundColors[i])/255.0f,
					_p.alpha(backgroundColors[i])/255.0f);*/
		}
		//shader.set("foregroundColors", fg);
		//shader.set("backgroundColors", bg);
		
		//shader.set("foregroundColors", foregroundColors);
		//shader.set("backgroundColors", backgroundColors);
		//shader.set("characters", chars);
		//shader.set("flippedH", flippedH);
		//shader.set("flippedV", flippedV);
		//shader.set("rotation", rotation);
	}
	
	float DecodeFloatRGBA(float r, float g, float b, float a) {
	    return r*bitDecR + g*bitDecG + b*bitDecB + a*bitDecA;
	}
	
	public void renderASDFGHJKL() {
		buffer.beginDraw();
		updateShaderVariables();
		buffer.filter(shader);
		buffer.endDraw();
	}

	public void render() {
		buffer.beginDraw();
		buffer.textFont(font);
		buffer.noStroke();
		for (int y = 0; y < resY; y++) {
			for (int x = 0; x < resX; x++) {
				int i = y * resX + x;
				PTile tc = characters[i];
				int fgc = tc.style.fgc;
				int bgc = tc.style.bgc;
				char character = tc.character;
				boolean change = tc.changed;
				int rotation = tc.style.rotation; //(int)_p.random(4);
				boolean flipped = tc.style.flipped;
				if (change) {
					buffer.pushMatrix();
					
					buffer.translate(terminalToScreenX(x), terminalToScreenY(y));

					if (flipped) {
						buffer.translate(terminalToScreenX(1), 0);
						buffer.scale(-1, 1);
					}
					//rotate
					buffer.translate(terminalToScreenX(0.5f), terminalToScreenY(0.5f));
					buffer.rotate(rotation*_p.PI/2);
					buffer.translate(-terminalToScreenX(0.5f), -terminalToScreenY(0.5f));

					buffer.fill(bgc);
					buffer.rect(0, 0, buffer.width / resX, buffer.height / resY);

					buffer.fill(fgc);
					buffer.text(character, 0, fontSize - 2);
					//TODO: use profiler to compare font text to cached images

					buffer.popMatrix();
					tc.changed = false;
				}
			}
		}
		buffer.endDraw();
	}

	public float terminalToScreenX(float x) {
		return buffer.width / resX * x;
	}
	
	public float terminalToScreenY(float y) {
		return buffer.height / resY * y;
	}
	
	public int screenToTerminalX(float x) {
		return (int) ((x / buffer.width) * resX);
	}

	public int screenToTerminalY(float y) {
		return (int) ((y / buffer.height) * resY);
	}

	public boolean inBounds(int x, int y) {
		return x >= 0 && x < resX && y >= 0 && y < resY;
	}

	public void set(int x, int y, char character, int fgc, int bgc) {
		if (!inBounds(x, y))
			return;
		int i = y * resX + x;
		PTile tc = characters[i];
		tc.character = character;
		tc.style.fgc = fgc;
		tc.style.bgc = bgc;
		tc.changed = true;
	}

	public void set(int x, int y, PTile c) {
		if (!inBounds(x, y))
			return;
		int i = y * resX + x;
		characters[i] = new PTile(c);
		characters[i].changed = true;
	}
	
	public void set(int x, int y, PTileStyle style) {
		if (!inBounds(x, y))
			return;
		int i = y * resX + x;
		characters[i].style = style;
		characters[i].changed = true;
	}
	
	public PTile get(int x, int y) {
		if (!inBounds(x, y))
			return null;
		int i = y * resX + x;
		return characters[i];
	}
}
