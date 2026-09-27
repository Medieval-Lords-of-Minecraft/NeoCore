package me.neoblade298.neocore.velocity.commands.builtin;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;

import me.neoblade298.neocore.velocity.VelocityCore;
import me.neoblade298.neocore.velocity.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdTp implements SimpleCommand {
	private static final Component usage = Component.text("Usage: /tp [player]", NamedTextColor.RED);
	private static final Component notOnline = Component.text("This player is not online!", NamedTextColor.RED);
	@Override
	public void execute(Invocation inv) {
		if ((inv.source() instanceof Player)) {
			Player src = (Player) inv.source();
			if (inv.arguments().length == 0) {
				src.sendMessage(usage);
			}
			else {
				Optional<Player> trg = VelocityCore.proxy().getPlayer(inv.arguments()[0]);
				if (trg.isEmpty()) {
					Util.msg(src, notOnline);
					return;
				}
				executeTeleport(inv.source(), src, trg.get());
			}
		}
	}
	
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }
	
	public static void executeTeleport(CommandSource sender, Player src, Player trg) {
		Optional<ServerConnection> sourceConnection = src.getCurrentServer();
		Optional<ServerConnection> targetConnection = trg.getCurrentServer();
		if (sourceConnection.isEmpty() || targetConnection.isEmpty()) {
			Util.msg(sender, Component.text("One of those players is not connected to a server.", NamedTextColor.RED));
			return;
		}
		if (targetConnection.get().getServerInfo().getName()
				.equals(sourceConnection.get().getServerInfo().getName())) {
			// Directly tp without connecting
			sendTeleportMsg(src, trg, targetConnection.get(), true);
		}
		else {
			src.createConnectionRequest(targetConnection.get().getServer()).connect().whenComplete((result, error) -> {
				if (error != null) {
					VelocityCore.logger().log(Level.WARNING, "Failed to connect " + src.getUsername() + " for teleport", error);
					Util.msg(sender, Component.text("Could not connect " + src.getUsername() + " to "
							+ targetConnection.get().getServerInfo().getName() + ".", NamedTextColor.RED));
				}
				else if (result.isSuccessful()) {
					sendTeleportMsg(src, trg, targetConnection.get(), false);
				}
				else {
					Util.msg(sender, Component.text("Could not connect " + src.getUsername() + " to "
							+ targetConnection.get().getServerInfo().getName() + ".", NamedTextColor.RED));
				}
			});
		}
	}
	
	public static void sendTeleportMsg(Player src, Player trg, ServerConnection destination, boolean instant) {
		String[] msgs = new String[] { (instant ? "neocore-tp-instant" : "neocore-tp"),
				src.getUniqueId().toString(), trg.getUniqueId().toString()};
		VelocityCore.sendPluginMessage(List.of(destination.getServer()), msgs);
	}
	
    @Override
    public List<String> suggest(final Invocation inv) {
    	if (inv.arguments().length == 0) return List.of();
		String match = inv.arguments()[0].toLowerCase();
		return VelocityCore.proxy().getAllPlayers().stream()
				.map(Player::getUsername)
				.filter(name -> name.toLowerCase().startsWith(match))
				.toList();
    }
    
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("tp")
            .plugin(plugin)
            .build();
        
        return meta;
	}
}
