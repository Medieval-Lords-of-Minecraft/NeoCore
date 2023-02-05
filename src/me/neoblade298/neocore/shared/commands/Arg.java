package me.neoblade298.neocore.shared.commands;

import java.util.List;

public class Arg {
	private boolean required;
	private String display;
	private List<String> tabOptions;
	private ArgType type;
	
	public Arg(String display) {
		this(display, true, ArgType.OPTIONS);
	}
	
	public Arg(String display, boolean required) {
		this(display, required, ArgType.OPTIONS);
	}
	
	public Arg(String display, boolean required, ArgType type) {
		this.display = display;
		this.required = required;
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public String getDisplay() {
		return display;
	}
	
	public void setTabComplete(List<String> tabOptions) {
		this.tabOptions = tabOptions;
	}
	
	public List<String> getTabOptions() {
		return tabOptions;
	}
	
	
	public ArgType getType() {
		return type;
	}
}
