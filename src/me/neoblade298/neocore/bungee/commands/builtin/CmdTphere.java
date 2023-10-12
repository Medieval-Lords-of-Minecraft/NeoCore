package me.neoblade298.neocore.bungee.commands.builtin;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.bungee.BungeeCore;
import me.neoblade298.neocore.bungee.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdTphere implements SimpleCommand {
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("tphere")
            .plugin(plugin)
            .build();
        
        return meta;
	}
	
	@Override
	public void execute(Invocation inv) {
		if ((inv.source() instanceof Player)) {
			Player trg = (Player) inv.source();
			if (inv.arguments().length == 0) {
				inv.source().sendMessage(Component.text("Usage: /tphere [player]").color(NamedTextColor.RED));
			}
			else {
				Optional<Player> src = BungeeCore.proxy().getPlayer(inv.arguments()[0]);
				if (src.isEmpty()) {
					Util.msg(trg, "&cThis player is not online!");
					return;
				}
				CmdTp.executeTeleport(inv.source(), src.get(), trg);
			}
		}
	}
	
    @Override
    public List<String> suggest(final Invocation inv) {
    	if (inv.arguments().length == 0) return List.of();
		String match = inv.arguments()[0].toLowerCase();
		return BungeeCore.players.stream()
				.filter(name -> name.toLowerCase().startsWith(match))
				.toList();
    }
}
