package me.neoblade298.neocore.velocity.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import java.util.logging.Level;

import me.neoblade298.neocore.velocity.VelocityCore;
import me.neoblade298.neocore.velocity.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdHub implements SimpleCommand {
	
	public void execute(Invocation inv) {
		if (!(inv.source() instanceof Player player)) {
			Util.msg(inv.source(), Component.text("Only players can use this command.", NamedTextColor.RED));
			return;
		}
		RegisteredServer hub = VelocityCore.proxy().getServer(VelocityCore.hubServer()).orElse(null);
		if (hub == null) {
			Util.msg(player, Component.text("The hub server is not configured.", NamedTextColor.RED));
			return;
		}
		player.createConnectionRequest(hub).connect().whenComplete((result, error) -> {
			if (error != null) {
				VelocityCore.logger().log(Level.WARNING, "Failed to connect " + player.getUsername() + " to the hub", error);
				Util.msg(player, Component.text("Could not connect you to the hub.", NamedTextColor.RED));
			}
			else if (!result.isSuccessful()) {
				Util.msg(player, Component.text("Could not connect you to the hub.", NamedTextColor.RED));
			}
		});
	}
	
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.player");
    }
    
	
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("hub")
            .plugin(plugin)
            .build();
        
        return meta;
	}
}
