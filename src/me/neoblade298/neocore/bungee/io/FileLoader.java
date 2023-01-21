package me.neoblade298.neocore.bungee.io;

import java.io.File;

import net.md_5.bungee.config.Configuration;

public interface FileLoader {
	public void load(Configuration cfg, File file);
}
