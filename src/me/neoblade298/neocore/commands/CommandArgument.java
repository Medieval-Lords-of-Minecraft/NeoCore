package me.neoblade298.neocore.commands;

public class CommandArgument {
	private boolean required;
	private String display;
	
	public CommandArgument(String display) {
		this(display, true);
	}
	
	public CommandArgument(String display, boolean required) {
		this.display = display;
		this.required = required;
	}

	public boolean isRequired() {
		return required;
	}

	public String getDisplay() {
		return display;
	}
}
