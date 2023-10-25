package me.neoblade298.neocore.bungee.commands.builtin;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

// Go back and make the other commands follow SimpleCommand @overrides
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
				Optional<Player> trg = BungeeCore.proxy().getPlayer(inv.arguments()[0]);
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
		if (trg.getCurrentServer().get().getServerInfo().getName().equals(src.getCurrentServer().get().getServerInfo().getName())) {
			// Directly tp without connecting
			sendTeleportMsg(src, trg, true);
		}
		else {
			CompletableFuture<Boolean> cf = src.createConnectionRequest(trg.getCurrentServer().get().getServer()).connectWithIndication();
			
			try {
				if (cf.get()) {
					sendTeleportMsg(src, trg, false);
				}
				else {
					BungeeCore.logger().warning("[NeoCore] Failed to send teleport message for " + src.getUsername() +
							" to " + trg.getUsername() + ", connect failed");
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void sendTeleportMsg(Player src, Player trg, boolean instant) {
		String[] msgs = new String[] { (instant ? "neocore-tp-instant" : "neocore-tp"),
				src.getUniqueId().toString(), trg.getUniqueId().toString()};
		BungeeCore.sendPluginMessage(Arrays.asList(trg.getCurrentServer().get().getServer()), msgs);
	}
	
    @Override
    public List<String> suggest(final Invocation inv) {
    	if (inv.arguments().length == 0) return List.of();
		String match = inv.arguments()[0].toLowerCase();
		return BungeeCore.players.stream()
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
