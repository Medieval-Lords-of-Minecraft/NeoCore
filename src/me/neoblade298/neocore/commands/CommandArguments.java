package me.neoblade298.neocore.commands;

import java.util.ArrayList;
import java.util.Arrays;

public class CommandArguments {
	private ArrayList<CommandArgument> args = new ArrayList<CommandArgument>();
	private int min = 0, max = 0;
	private String display = "";
	
	public CommandArguments(CommandArgument... args) {
		this(Arrays.asList(args));
	}
	
	public CommandArguments(Iterable<CommandArgument> args) {
		for (CommandArgument arg : args) {
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
	}
	
	public CommandArguments() {
		
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
	
	public ArrayList<CommandArgument> getArguments() {
		return args;
	}
}
