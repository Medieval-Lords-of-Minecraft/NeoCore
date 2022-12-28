package me.neoblade298.bungeecore;

import java.util.HashMap;
import java.util.UUID;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCore extends Plugin
{
    @Override
    public void onEnable() {
        // You should not put an enable message in your plugin.
        // BungeeCord already does so
        getLogger().info("Yay! It loads!");
    }
}
