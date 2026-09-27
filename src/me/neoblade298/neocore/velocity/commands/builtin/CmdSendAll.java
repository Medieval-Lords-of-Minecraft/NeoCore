package me.neoblade298.neocore.velocity.commands.builtin;

import java.util.logging.Level;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.neoblade298.neocore.velocity.VelocityCore;
import me.neoblade298.neocore.velocity.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdSendAll implements SimpleCommand {
	private static final Component usage = Component.text("Not enough arguments! /sendall [server-from] [server-to]", NamedTextColor.RED);
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("sendall")
            .plugin(plugin)
            .build();
        
        return meta;
	}
	
	@Override
	public void execute(Invocation inv) {
		String[] args = inv.arguments();
		if (args.length != 2) {
			Util.msg(inv.source(), usage);
			return;
		}
		RegisteredServer source = VelocityCore.proxy().getServer(args[0]).orElse(null);
		RegisteredServer destination = VelocityCore.proxy().getServer(args[1]).orElse(null);
		if (source == null || destination == null) {
			Util.msg(inv.source(), Component.text("One or both servers do not exist.", NamedTextColor.RED));
			return;
		}
		for (Player p : source.getPlayersConnected()) {
			p.createConnectionRequest(destination).connect().whenComplete((result, error) -> {
				if (error != null) {
					VelocityCore.logger().log(Level.WARNING, "[NeoCore] Failed to send " + p.getUsername() + " to "
							+ destination.getServerInfo().getName(), error);
				}
				else if (!result.isSuccessful()) {
					VelocityCore.logger().warning("[NeoCore] Failed to send " + p.getUsername() + " to "
							+ destination.getServerInfo().getName() + ": " + result.getStatus());
				}
			});
		}
	}
	
	@Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }

}
