package me.neoblade298.neocore.io;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public interface FileLoader {
	public void load(YamlConfiguration cfg, File file);
}
