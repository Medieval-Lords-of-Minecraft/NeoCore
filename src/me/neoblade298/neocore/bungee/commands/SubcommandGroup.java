package me.neoblade298.neocore.bungee.commands;

import java.util.Arrays;
import java.util.List;

import com.velocitypowered.api.command.CommandSource;

import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.format.TextColor;

/**
 * A subcommand that is itself a command tree. Register it in a parent
 * {@link SubcommandManager} like any other {@link Subcommand}, then register
 * children (including further {@link SubcommandGroup}s) into it via
 * {@link #register(Subcommand)}. This enables nesting like
 * {@code /cmd subcmd1 leaf <args>} to arbitrary depth, with each level keeping
 * its own permissions, command list, colors, and tab completion.
 */
public class SubcommandGroup extends Subcommand {
	private final SubcommandManager child;

	/**
	 * @param key        the group's key under its parent (e.g. "subcmd1")
	 * @param desc       description shown in command lists
	 * @param perm       permission for this group; also the fallback permission
	 *                   for children that don't define their own
	 * @param parentBase the full command path of the parent (e.g. "cmd"), used so
	 *                   displays read "/cmd subcmd1 ..."
	 * @param color      default color for this subtree's command list
	 */
	public SubcommandGroup(String key, String desc, String perm, String parentBase, TextColor color) {
		super(key, desc, perm, SubcommandRunner.BOTH);
		this.child = new SubcommandManager(parentBase + " " + key, perm, color);
		// The group forwards everything to its children, so its own arg bounds must be
		// unbounded or the parent manager's arg-count check would reject valid input.
		args.setMin(-1);
		args.setMax(-1);
		overrideTabHandler();
	}

	public void register(Subcommand cmd) {
		child.register(cmd);
	}

	public void registerCommandList(String key) {
		child.registerCommandList(key);
	}

	public void registerCommandList(String key, String perm, TextColor color) {
		child.registerCommandList(key, perm, color);
	}

	public SubcommandManager getManager() {
		return child;
	}

	@Override
	public void run(CommandSource s, String[] args) {
		// args already have this group's key stripped by the parent's reduceArgs.
		child.dispatch(s, args);
	}

	@Override
	public List<String> getTabOptions(CommandSource s, String[] args) {
		// The parent passes the full args including this group's key at [0]; drop it so
		// the child manager sees its children at index 0.
		return child.tabComplete(s, Arrays.copyOfRange(args, 1, args.length));
	}
}
