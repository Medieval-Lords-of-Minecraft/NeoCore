package me.neoblade298.neocore.bukkit.commands.builtin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.neoblade298.neocore.bukkit.commands.Subcommand;
import me.neoblade298.neocore.bukkit.util.Util;
import me.neoblade298.neocore.shared.commands.SubcommandRunner;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class CmdCoreBroadcast extends Subcommand {
	private static Component prefix;
	public CmdCoreBroadcast(String key, String desc, String perm, SubcommandRunner runner) {
		super(key, desc, perm, runner);
		args.setOverride("[broadcast]");
		
		prefix = Component.text("[", NamedTextColor.DARK_RED)
				.append(Component.text("MLMC", NamedTextColor.RED, TextDecoration.BOLD))
				.append(Component.text("] ", NamedTextColor.DARK_RED));
	}

	@Override
	public void run(CommandSender s, String[] args) {
		if (args.length == 0) {
			Util.msg(s, "&cYou need a message to broadcast!");
		}
		else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendMessage(prefix.append(MiniMessage.miniMessage().deserialize(SharedUtil.connectArgs(args))));
			}
		}
	}
}
