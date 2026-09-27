package me.neoblade298.neocore.velocity.commands.builtin;

import java.util.Optional;
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

public class CmdSend implements SimpleCommand {
	private static final Component usage = Component.text("Usage: /send [player] [server]", NamedTextColor.RED);
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("send")
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
		Optional<Player> player = VelocityCore.proxy().getPlayer(args[0]);
		if (player.isEmpty()) {
			Util.msg(inv.source(), "<red>That player isn't online!");
			return;
		}
		Optional<RegisteredServer> server = VelocityCore.proxy().getServer(args[1]);
		if (server.isEmpty()) {
			Util.msg(inv.source(), "<red>That server doesn't exist!");
			return;
		}
		player.get().createConnectionRequest(server.get()).connect().whenComplete((result, error) -> {
			if (error != null) {
				VelocityCore.logger().log(Level.WARNING, "Failed to send " + player.get().getUsername()
						+ " to " + server.get().getServerInfo().getName(), error);
				Util.msg(inv.source(), "<red>Could not send that player.");
			}
			else if (result.isSuccessful()) {
				Util.msg(inv.source(), "Send successful!");
			}
			else {
				Util.msg(inv.source(), "<red>Could not send that player: " + result.getStatus());
			}
		});
	}
	
	@Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }

}
