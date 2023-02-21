package me.neoblade298.neocore.shared.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public class Gradient {
	private String id;
	private ArrayList<GradientSection> sections = new ArrayList<GradientSection>(); // MUST be correct order
	
	public Gradient(String id, List<String> sections) {
		this.id = id;
		for (String section : sections) {
			String[] args = section.split(":");
			this.sections.add(new GradientSection(Double.parseDouble(args[0]), Color.decode(args[1])));
		}
	}
	
	public String getId() {
		return id;
	}
	
	public String apply(String text) {
		Iterator<GradientSection> iter = sections.iterator();
		GradientSection from, to = iter.next();
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		while (iter.hasNext()) {
			from = to;
			to = iter.next();
			
			int startPos = (int) (from.getPosition() * text.length());
			int endPos = (int) (to.getPosition() * text.length());
			append(text.substring(startPos, endPos), sb, from.getColor(), to.getColor(), first);
			
			if (endPos - startPos > 1) first = false;
			else first = true;
		}
		return sb.toString();
	}

	// Applies full start to finish gradient if includeFirst,
	// Else, assumes the start color has already been added
	public void append(String text, StringBuilder sb, Color from, Color to, boolean includeFirst) {
		char[] arr = text.toCharArray();
		
		int time = includeFirst ? 0 : 1, end;
		
		if (arr.length == 1) { // if include first, show last color; else show first color
			end = 1;
		}
		else if (arr.length == 2) {
			end = time + 1; // Include last color in gradient (usually don't since next section will add it)
		}
		else {
			end = includeFirst ? arr.length : arr.length + 1;
		}
		
		for (int i = 0; i < arr.length; i++) {
			sb.append(blendColors(from, to, (double) time++ / (double) end));
			sb.append(arr[i]);
		}
	}
	
	// t for time
	private ChatColor blendColors(Color from, Color to, double t) {
        int r = (int) (from.getRed() * (1 - t) + to.getRed() * t);
        int g = (int) (from.getGreen() * (1 - t) + to.getGreen() * t);
        int b = (int) (from.getBlue() * (1 - t) + to.getBlue() * t);
        return ChatColor.of(new Color(r, g, b));
	}
}
