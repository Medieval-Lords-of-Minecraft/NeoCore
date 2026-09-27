package me.neoblade298.neocore.velocity.commands.builtin;

import java.util.List;
import java.util.Optional;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.velocity.VelocityCore;
import me.neoblade298.neocore.velocity.util.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdTphere implements SimpleCommand {
	private static final Component usage = Component.text("Usage: /tphere [player]", NamedTextColor.RED);
	private static final Component notOnline = Component.text("This player is not online!", NamedTextColor.RED);
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("tphere")
            .plugin(plugin)
            .build();
        
        return meta;
	}
	
	@Override
	public void execute(Invocation inv) {
		if (!(inv.source() instanceof Player target)) {
			Util.msg(inv.source(), Component.text("Only players can use this command.", NamedTextColor.RED));
			return;
		}
		if (inv.arguments().length != 1) {
			inv.source().sendMessage(usage);
			return;
		}
		Optional<Player> source = VelocityCore.proxy().getPlayer(inv.arguments()[0]);
		if (source.isEmpty()) {
			Util.msg(target, notOnline);
			return;
		}
		CmdTp.executeTeleport(inv.source(), source.get(), target);
	}

	@Override
	public boolean hasPermission(final Invocation invocation) {
		return invocation.source().hasPermission("neocore.staff");
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
}
