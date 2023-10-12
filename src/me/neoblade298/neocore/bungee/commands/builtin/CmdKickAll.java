package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.SimpleCommand.Invocation;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdKickAll implements SimpleCommand {
	public void execute(Invocation inv) {
		for (Player p : BungeeCore.proxy().getAllPlayers()) {
			p.disconnect(Component.text("Server is going down for maintenance!").color(NamedTextColor.RED));
		}
	}
	
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("kickall")
            .plugin(plugin)
            .build();
        
        return meta;
	}
	
	@Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }
}
