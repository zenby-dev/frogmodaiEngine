package frogmodaiEngine;

import java.awt.Color;
import java.awt.color.*;

public class ColorUtils {
	public static PTile emptyTile = new PTile(' ', Colors.BLACK, Colors.BLACK);
	
	public static int red(int c) {
		return (c & 0xFF000000) >> 24;
	}
	
	public static int green(int c) {
		return (c & 0x00FF0000) >> 16;
	}
	
	public static int blue(int c) {
		return (c & 0x0000FF00) >> 8;
	}
	
	public static int color(int r, int g, int b) {
		return (b << 0) | (g << 8) | (r << 16) | (255 << 24);
	}
	
	public static int color(int r, int g, int b, int a) {
		return (b << 0) | (g << 8) | (r << 16) | (a << 24);
	}
	
	public static int randomHSB() {
		return HSBtoRGB(FrogmodaiEngine.random.nextInt(255), 255, 255);
	}
	
	public static int RGBtoHSB(int r, int g, int b) {
		float[] hsb = Color.RGBtoHSB(r, g, b, null);
		return color((int)hsb[0]*255, (int)hsb[1]*255, (int)hsb[2]*255);
	}
	
	public static int RGBtoHSB(int c) {
		return RGBtoHSB(red(c), blue(c), green(c));
	}
	
	public static int HSBtoRGB(int h, int s, int b) {
		return Color.HSBtoRGB(h/255.0f, s/255.0f, b/255.0f) << 8;
	}
	
	public static class Colors {
		public static int BLACK = color(0,0,0);
		public static int WHITE = color(255,255,255);
		public static int GREY = color(128,128,128);
		public static int GREYDARK = color(64,64,64);
		public static int GREYLIGHT = color(128+64,128+64,128+64);
		public static int RED = color(255,0,0);
		public static int GREEN = color(0,255,0);
		public static int BLUE = color(0,0,255);
		public static int YELLOW = color(255,255,0);
		public static int CYAN = color(0,255,255);
		public static int MAGENTA = color(255,0,255);
	}
}
