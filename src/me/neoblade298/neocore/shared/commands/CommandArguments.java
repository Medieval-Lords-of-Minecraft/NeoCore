package me.neoblade298.neocore.shared.commands;

import java.util.ArrayList;

public class CommandArguments {
	private ArrayList<Arg> args = new ArrayList<Arg>();
	private int min = 0, max = 0;
	private String display = "";
	
	public CommandArguments(Arg... args) {
		add(args);
	}
	
	public CommandArguments add(Arg... args) {
		for (Arg arg : args) {
			this.args.add(arg);
			
			if (display.length() > 0) {
				display += " ";
			}
			
			if (arg.isRequired()) {
				display += "[" + arg.getDisplay() + "]";
				min++;
			}
			else {
				display += "{" + arg.getDisplay() + "}";
			}
			max++;
		}
		return this;
	}
	
	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public ArrayList<Arg> getArguments() {
		return args;
	}
	
	public void setOverride(String override) {
		this.display = override;
		min = -1;
		max = -1;
	}
	
	public void setMin(int min) {
		this.min = min;
	}
	
	public void setMax(int max) { 
		this.max = max;
	}
}
