package frogmodaiEngine;

import java.util.ArrayList;

public class Paragraph {
	public ArrayList<TextSegment> segments;
	
	public Paragraph() {
		segments = new ArrayList<TextSegment>();
	}
	
	public void add(String s) {
		add(new TextSegment(s));
	}
	
	public void add(String s, int fg) {
		add(new TextSegment(s, fg));
	}
	
	public void add(String s, int fg, int bg) {
		add(new TextSegment(s, fg, bg));
	}
	
	/*public void add(String s, int fg, int bg, SGR _sgr) {
		add(new TextSegment(s, fg, bg, _sgr));
	}*/

	public void add(TextSegment textSegment) {
		segments.add(textSegment);
	}
}
