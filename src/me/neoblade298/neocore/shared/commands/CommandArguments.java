package me.neoblade298.neocore.shared.commands;

import java.util.ArrayList;
import java.util.List;

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

			if (arg instanceof FlagArg) {
				// Flags occupy two tokens: the flag itself and its value, e.g. -p <value>.
				display += "(" + ((FlagArg) arg).getFlag() + " <" + arg.getDisplay() + ">)";
				max++;
			}
			else if (arg.isRequired()) {
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

	public int size() {
		return args.size();
	}

	public static Arg getCurrentArg(String[] args, CommandArguments cargs) {
		// args[0] is the subcommand, so real arguments start at index 1 and the
		// argument currently being typed is args[args.length - 1].
		if (args.length < 2) return null;
		if (cargs.size() == 0) return null;

		// Command arguments are always entered left-to-right and a value can never be
		// skipped in the middle, so the argument currently being typed maps directly to
		// the definition at that position. Everything before it is already filled,
		// regardless of whether each definition is required or optional. This keeps tab
		// completion consistent even when optional arguments are present.
		int argIndex = args.length - 2;
		if (argIndex >= cargs.size()) return null;
		return cargs.getArguments().get(argIndex);
	}

	/**
	 * Resolves the tab completions for the argument currently being typed, handling
	 * both positional and flag-style ({@code -p <value>}) arguments.
	 *
	 * @param args the raw command args, where {@code args[0]} is the subcommand and
	 *             {@code args[args.length - 1]} is the token being typed.
	 * @return the matching, prefix-filtered suggestions (never {@code null}).
	 */
	public List<String> getTabCompletions(String[] args) {
		ArrayList<String> out = new ArrayList<String>();
		if (args.length < 2 || this.args.isEmpty()) return out;
		String current = args[args.length - 1].toLowerCase();

		// If the token right before the cursor is a flag, complete that flag's value.
		if (args.length >= 3) {
			FlagArg flag = getFlag(args[args.length - 2]);
			if (flag != null) {
				addMatches(out, flag.getTabOptions(), current);
				return out;
			}
		}

		// Otherwise offer the current positional argument's options plus any unused flags.
		Arg positional = getCurrentPositionalArg(args);
		if (positional != null && !(positional instanceof FlagArg)) {
			addMatches(out, positional.getTabOptions(), current);
		}
		for (Arg arg : this.args) {
			if (arg instanceof FlagArg) {
				String flag = ((FlagArg) arg).getFlag();
				if (!isFlagUsed(args, flag) && flag.toLowerCase().startsWith(current)) {
					out.add(flag);
				}
			}
		}
		return out;
	}

	// Maps the token being typed to its positional arg definition, skipping over any
	// flag (-p) and its value token so flags don't shift positional indices.
	private Arg getCurrentPositionalArg(String[] args) {
		int positionalCount = 0;
		for (int i = 1; i <= args.length - 2; i++) {
			if (getFlag(args[i]) != null) {
				i++; // also skip the flag's value token
			}
			else {
				positionalCount++;
			}
		}

		int seen = 0;
		for (Arg arg : this.args) {
			if (arg instanceof FlagArg) continue;
			if (seen == positionalCount) return arg;
			seen++;
		}
		return null;
	}

	private FlagArg getFlag(String token) {
		if (token == null) return null;
		for (Arg arg : this.args) {
			if (arg instanceof FlagArg && ((FlagArg) arg).getFlag().equalsIgnoreCase(token)) {
				return (FlagArg) arg;
			}
		}
		return null;
	}

	private static boolean isFlagUsed(String[] args, String flag) {
		// args[args.length - 1] is the token still being typed, so it doesn't count.
		for (int i = 1; i <= args.length - 2; i++) {
			if (args[i].equalsIgnoreCase(flag)) return true;
		}
		return false;
	}

	private static void addMatches(List<String> out, List<String> options, String current) {
		if (options == null) return;
		for (String opt : options) {
			if (opt.toLowerCase().startsWith(current)) out.add(opt);
		}
	}

	// --- Runtime helpers -----------------------------------------------------
	// The methods below operate on the argument array passed to Subcommand#run,
	// where index 0 is the first argument (the subcommand has already been
	// removed). Because flags are matched by name, they may appear at any
	// position in the command.

	/**
	 * @return whether the given flag (e.g. {@code "r"} or {@code "-r"}) is present
	 *         anywhere in the run arguments.
	 */
	public static boolean hasFlag(String[] runArgs, String flag) {
		String f = FlagArg.normalize(flag);
		for (String arg : runArgs) {
			if (arg.equalsIgnoreCase(f)) return true;
		}
		return false;
	}

	/**
	 * @return the value token immediately following the given flag, or {@code null}
	 *         if the flag is absent or has no following value.
	 */
	public static String getFlagValue(String[] runArgs, String flag) {
		String f = FlagArg.normalize(flag);
		for (int i = 0; i < runArgs.length - 1; i++) {
			if (runArgs[i].equalsIgnoreCase(f)) return runArgs[i + 1];
		}
		return null;
	}

	/**
	 * Returns the run arguments with every registered flag and its following value
	 * removed, leaving only the positional arguments in order. Use this so index
	 * based reading is unaffected by flags appearing anywhere in the command.
	 */
	public String[] getPositionalArgs(String[] runArgs) {
		ArrayList<String> out = new ArrayList<String>();
		for (int i = 0; i < runArgs.length; i++) {
			if (isRegisteredFlag(runArgs[i])) {
				i++; // skip the flag's value token
			}
			else {
				out.add(runArgs[i]);
			}
		}
		return out.toArray(new String[0]);
	}

	private boolean isRegisteredFlag(String token) {
		for (Arg arg : this.args) {
			if (arg instanceof FlagArg && ((FlagArg) arg).getFlag().equalsIgnoreCase(token)) {
				return true;
			}
		}
		return false;
	}
}
