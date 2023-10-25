package me.neoblade298.neocore.bungee.commands;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.CommandArguments;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class SubcommandManager implements SimpleCommand {
	private CommandOverhead overhead;
	private static Component wrongUser = Component.text("You are the wrong type of user for this command!", NamedTextColor.RED);
	
	public static CommandMeta meta(CommandManager mngr, String base, Object plugin) {
        CommandMeta meta = mngr.metaBuilder(base)
            .plugin(plugin)
            .build();
        return meta;
	}
	public SubcommandManager(String base, String perm, TextColor color, CommandManager mngr, Object plugin) {
		this(base, perm, color, mngr, plugin, new String[0]);
	}
	public SubcommandManager(String base, String perm, TextColor color, CommandManager mngr, Object plugin, String[] aliases) {
		overhead = new CommandOverhead(base, perm, color);
		mngr.register(meta(mngr, base, plugin), this);
	}
	
	@Override
	public void execute(Invocation inv) {
		String[] args = inv.arguments();
		CommandSource s = inv.source();
		Subcommand sc = overhead.parseForCommand(args);
		if (sc == null) return;

		args = overhead.reduceArgs(args, sc);
		if (check(sc, s, args)) {
			sc.run(s, args);
		}
	}
	
	private boolean check(Subcommand cmd, CommandSource s, boolean silent) {
		// If cmd permission exists, it overrides list permission
		String activePerm = cmd.getPermission() != null ? cmd.getPermission() : overhead.getPermission();
		
		if (activePerm != null && !s.hasPermission(activePerm)) {
			if (!silent) Util.msg(s, Component.text("You're missing the permission: " + activePerm, NamedTextColor.RED));
			return false;
		}

		if ((cmd.getRunner() == SubcommandRunner.PLAYER_ONLY && !(s instanceof Player)) ||
				(cmd.getRunner() == SubcommandRunner.CONSOLE_ONLY && !(s == BungeeCore.proxy().getConsoleCommandSource()))) {
			if (!silent) Util.msg(s, wrongUser);
			return false;
		}
		return true;
	}
	
	private boolean check(Subcommand cmd, CommandSource s, String[] args) {
		if (!check(cmd, s, false)) return false;
		
		CommandArguments cargs = cmd.getArgs();
		if (args.length < cargs.getMin() && cargs.getMin() != -1) {
			Util.msg(s, Component.text("This command requires at least " + cargs.getMin() + " args but received " + args.length + ".", NamedTextColor.RED));
			Util.msg(s, overhead.getCommandLine(cmd));
			return false;
		}

		if (args.length > cargs.getMax() && cargs.getMax() != -1) {
			Util.msg(s, Component.text("This command requires at most " + cargs.getMax() + " args but received " + args.length + ".", NamedTextColor.RED));
			Util.msg(s, overhead.getCommandLine(cmd));
			return false;
		}
		
		return true;
	}
	
	public void registerCommandList(String key, String perm, TextColor color) {
		overhead.getHandlers().put(key.toLowerCase(), new CmdList(key, overhead.getBase(), perm, overhead.getPermission(), overhead.getHandlers(), overhead.getAliases(), overhead.getColor(), color));
	}
	
	public void registerCommandList(String key) {
		overhead.getHandlers().put(key.toLowerCase(),
				new CmdList(key, overhead.getBase(), null, overhead.getPermission(), overhead.getHandlers(), overhead.getAliases(), overhead.getColor()));
	}
	
	public void register(Subcommand cmd) {
		overhead.register(cmd);
	}
	
	public Subcommand getCommand(String key) {
		return overhead.getHandlers().get(key.toLowerCase());
	}
	
	public Set<String> getKeys() {
		return overhead.getHandlers().keySet();
	}

	@Override
    public List<String> suggest(final Invocation inv) {
		CommandSource s = inv.source();
		String[] args = inv.arguments();
		if (!(s instanceof Player)) return Collections.emptyList(); // Only player senders can use tab complete
		
		if (overhead.getPermission() != null && !s.hasPermission(overhead.getPermission())) return Collections.emptyList();
		
		if (args.length == 1) {
			// Get all commands that can be run by user
			return overhead.getHandlers().values().stream()
					.filter(cmd -> check(cmd, s, true) && !cmd.isHidden() && cmd.getKey().length() > 0)
					.map(cmd -> cmd.getKey())
					.toList();
		}
		else {
			if (args[0].isBlank() || StringUtils.isNumeric(args[0])) return Collections.emptyList();
			
			// Only look for a subcommand if the first arg is not a number and not blank
			Subcommand cmd = overhead.getHandlers().get(args[0].toLowerCase());
			if (cmd == null || cmd.isHidden() || !cmd.isTabEnabled()) return Collections.emptyList();
			
			CommandArguments ca = cmd.getArgs();
			Arg arg = CommandArguments.getCurrentArg(args, ca);
			return arg.getTabOptions() != null ? arg.getTabOptions() : Collections.emptyList();
		}
	}
}
