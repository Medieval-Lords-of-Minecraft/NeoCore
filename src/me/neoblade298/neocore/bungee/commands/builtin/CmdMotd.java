package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdMotd implements SimpleCommand {
	private static final Component add = Component.text("Successfully added to MOTD", NamedTextColor.GRAY),
			remove = Component.text("Successfully removed from MOTD", NamedTextColor.GRAY),
			usage = Component.text("- /motd add [msg]\n- /motd remove [index from 0]", NamedTextColor.GRAY);
	
	@Override
	public void execute(Invocation inv) {
		CommandSource src = inv.source();
		String[] args = inv.arguments();
		if (args.length == 0 || !src.hasPermission("neocore.staff")) {
			BungeeCore.sendMotd(src);
		}
		else {
			if (args[0].equalsIgnoreCase("add") && args.length > 1) {
				BungeeCore.addAnnouncement(src, SharedUtil.connectArgs(args, 1));
				Util.msg(src, add);
			}
			else if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
				BungeeCore.removeAnnouncement(src, Integer.parseInt(args[1]));
				Util.msg(src, remove);
			}
			else {
				Util.msg(src, usage);
			}
		}
	}
	
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("motd")
            .plugin(plugin)
            .build();
        
        return meta;
	}
}
