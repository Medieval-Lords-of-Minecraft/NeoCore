package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;

import me.neoblade298.neocore.bungee.util.Util;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CmdBroadcast implements SimpleCommand {
	private static final Component usage = Component.text("/bc [broadcast msg]", NamedTextColor.RED);

	@Override
	public void execute(Invocation inv) {
		if (inv.arguments().length == 0) {
			Util.msg(inv.source(), usage);
		}
		else {
			Util.broadcast(Component.text(SharedUtil.connectArgs(inv.arguments())));
		}
	}
	
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }
	
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("bc")
            .plugin(plugin)
            .build();
        
        return meta;
	}
}
