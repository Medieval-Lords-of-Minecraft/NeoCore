package me.neoblade298.neocore.shared.util;

import java.awt.Color;

public class GradientSection {
	private double pos;
	private Color color;
	
	public GradientSection(double pos, Color color) {
		this.pos = pos;
		this.color = color;
	}
	
	public double getPosition() {
		return pos;
	}
	
	public Color getColor() {
		return color;
	}
}
