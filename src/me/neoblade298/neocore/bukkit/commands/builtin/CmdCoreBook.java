package me.neoblade298.neocore.bukkit.commands.builtin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.book.BookRegistry;
import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.shared.commands.Arg;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;

public class CmdCoreBook extends Subcommand {
	public CmdCoreBook(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.add(new Arg("Book"), new Arg("Chapter", false));
		overrideTabHandler();
	}

	@Override
	public void run(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		if (args.length == 1) BookRegistry.openTableOfContents(player, args[0]);
		else BookRegistry.openChapter(player, args[0], args[1]);
	}

	@Override
	public List<String> getTabOptions(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		String current = args[args.length - 1].toLowerCase();
		if (args.length == 2) {
			return BookRegistry.getBookIds(player).stream()
					.filter(id -> id.startsWith(current)).toList();
		}
		if (args.length == 3) {
			return BookRegistry.getChapterIds(player, args[1]).stream()
					.filter(id -> id.startsWith(current)).toList();
		}
		return List.of();
	}
}