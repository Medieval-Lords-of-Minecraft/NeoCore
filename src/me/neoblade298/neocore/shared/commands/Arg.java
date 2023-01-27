package me.neoblade298.neocore.shared.commands;

public class Arg {
	private boolean required;
	private String display;
	
	public Arg(String display) {
		this(display, true);
	}
	
	public Arg(String display, boolean required) {
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
