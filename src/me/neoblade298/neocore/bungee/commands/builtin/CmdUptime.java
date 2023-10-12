package me.neoblade298.neocore.bungee.commands.builtin;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;

import net.kyori.adventure.text.Component;

public class CmdUptime implements SimpleCommand {
	private long startTime;
	public CmdUptime() {
		startTime = System.currentTimeMillis();
	}
	
	public static CommandMeta meta(CommandManager mngr, Object plugin) {
        CommandMeta meta = mngr.metaBuilder("uptime")
            .plugin(plugin)
            .build();
        
        return meta;
	}

	@Override
	public void execute(Invocation inv) {
		long now = System.currentTimeMillis();
		int diff = (int) (now - startTime);
		int days = diff / 86400000;
		diff = diff % 86400000;
		int hours = diff / 3600000;
		diff = diff % 3600000;
		int minutes = diff / 60000;
		diff = diff % 60000;
		int seconds = diff / 1000;
		String uptime = "Uptime: " + days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds";
		inv.source().sendMessage(Component.text(uptime));
	}
	
	@Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("neocore.staff");
    }
}
