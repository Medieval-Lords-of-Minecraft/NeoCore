package me.neoblade298.neocore.bukkit.commands.builtin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.book.BookRegistry;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.CommandArguments;
import me.neoblade298.neocore.shared.commands.FlagArg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdCoreBook extends Subcommand {
	private static final Component PLAYER_REQUIRED = Component.text("Console must specify a player with -p.", NamedTextColor.RED);
	private static final Component PLAYER_NOT_FOUND = Component.text("That player isn't online!", NamedTextColor.RED);
	private static final Component MISSING_PERMISSION = Component.text("You can't open books for another player!", NamedTextColor.RED);
	private static final Component USAGE = Component.text("Usage: /ncore book <book> [chapter] [-p <player>]", NamedTextColor.RED);

	public CmdCoreBook(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("Book"), new Arg("Chapter", false), new FlagArg("p", "Player"));
		overrideTabHandler();
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		String[] positionalArgs = this.args.getPositionalArgs(args);
		if (positionalArgs.length < 1 || positionalArgs.length > 2) {
			Util.msg(sender, USAGE);
			return;
		}

		String targetName = CommandArguments.getFlagValue(args, "p");
		if (CommandArguments.hasFlag(args, "p") && targetName == null) {
			Util.msg(sender, PLAYER_NOT_FOUND);
			return;
		}

		Player player = targetName == null && sender instanceof Player ? (Player) sender : Bukkit.getPlayer(targetName);
		if (player == null) {
			Util.msg(sender, targetName == null ? PLAYER_REQUIRED : PLAYER_NOT_FOUND);
			return;
		}
		if (!player.equals(sender) && !sender.hasPermission("neocore.book.others")) {
			Util.msg(sender, MISSING_PERMISSION);
			return;
		}

		if (positionalArgs.length == 1) BookRegistry.openTableOfContents(player, positionalArgs[0]);
		else BookRegistry.openChapter(player, positionalArgs[0], positionalArgs[1]);
	}

	@Override
	public List<String> getTabOptions(CommandSender sender, String[] args) {
		String current = args[args.length - 1].toLowerCase();
		if (args.length >= 3 && args[args.length - 2].equalsIgnoreCase("-p")) {
			List<String> playerNames = new ArrayList<String>();
			Bukkit.getOnlinePlayers().stream().map(Player::getName)
					.filter(name -> name.toLowerCase().startsWith(current)).forEach(playerNames::add);
			return playerNames;
		}

		List<String> positionalArgs = new ArrayList<String>();
		boolean playerFlagUsed = false;
		for (int i = 1; i < args.length - 1; i++) {
			if (args[i].equalsIgnoreCase("-p")) {
				playerFlagUsed = true;
				i++;
			}
			else positionalArgs.add(args[i]);
		}

		String targetName = CommandArguments.getFlagValue(args, "p");
		Player player = targetName == null && sender instanceof Player ? (Player) sender : Bukkit.getPlayer(targetName);
		List<String> options = new ArrayList<String>();
		if (player != null && positionalArgs.isEmpty()) {
			options.addAll(BookRegistry.getBookIds(player).stream()
					.filter(id -> id.startsWith(current)).toList());
		}
		else if (player != null && positionalArgs.size() == 1) {
			options.addAll(BookRegistry.getChapterIds(player, positionalArgs.get(0)).stream()
					.filter(id -> id.startsWith(current)).toList());
		}
		if (!playerFlagUsed && sender.hasPermission("neocore.book.others") && "-p".startsWith(current)) {
			options.add("-p");
		}
		return options;
	}
}