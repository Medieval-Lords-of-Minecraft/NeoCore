package me.neoblade298.neocore.shared.commands;

/**
 * An {@link Arg} entered in the flag style {@code -p <value>}: a fixed flag token
 * (e.g. {@code -p}) followed by a value token that can be tab completed using this
 * arg's tab options. Flags are optional and order-independent, so they may appear
 * anywhere after the subcommand.
 * <p>
 * The leading dash is added automatically, so {@code new FlagArg("r", "reason")}
 * produces the flag {@code -r}.
 */
public class FlagArg extends Arg {
	private final String flag;

	public FlagArg(String flag, String display) {
		super(display, false, ArgType.OPTIONS);
		this.flag = normalize(flag);
	}

	public FlagArg(String flag, String display, ArgType type) {
		super(display, false, type);
		this.flag = normalize(flag);
	}

	// Ensures the flag has exactly one leading dash, whether or not the caller
	// supplied one (e.g. both "r" and "-r" become "-r").
	static String normalize(String flag) {
		String trimmed = flag;
		while (trimmed.startsWith("-")) {
			trimmed = trimmed.substring(1);
		}
		return "-" + trimmed;
	}

	/**
	 * @return the flag token that precedes the value, e.g. {@code -p}.
	 */
	public String getFlag() {
		return flag;
	}
}
