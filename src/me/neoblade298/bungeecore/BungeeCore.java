package me.neoblade298.bungeecore;

import me.neoblade298.bungeecore.commands.*;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCore extends Plugin
{
    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new CmdTeleport());
        getProxy().registerChannel("neocore:bungee");
    }
}
