package me.neoblade298.neocore.bukkit.io;

import java.io.File;

import me.neoblade298.neocore.shared.io.Config;

public interface FileLoader {
	public void load(Config cfg, File file);
}
