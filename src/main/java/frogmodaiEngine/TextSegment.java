package frogmodaiEngine;

public class TextSegment {
	public String text;
	public PTileStyle style;
	//public SGR sgr;
	
	public TextSegment(String s) {
		text = s;
		style = new PTileStyle(ColorUtils.Colors.WHITE, ColorUtils.Colors.BLACK);
	}
	
	public TextSegment(String s, PTileStyle _style) {
		text = s;
		style = new PTileStyle(_style);
	}
	
	public TextSegment(String s, int fg) {
		text = s;
		style = new PTileStyle(fg, ColorUtils.Colors.BLACK);
	}
	
	public TextSegment(String s, int fg, int bg) {
		text = s;
		style = new PTileStyle(fg, bg);
	}
}
