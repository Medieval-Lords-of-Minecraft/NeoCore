package me.neoblade298.neocore.bukkit.commandsets;

import java.io.File;
import java.util.HashMap;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.io.FileLoader;

public class CommandSetManager {
	private static HashMap<String, CommandSet> sets = new HashMap<String, CommandSet>();
	
	private static final FileLoader setLoader;
	
	static {
		setLoader = (cfg, file) -> {
			for (String key : cfg.getKeys()) {
				sets.put(key.toUpperCase(), new CommandSet(key, cfg.getSection(key)));
			}
		};
	}
	
	public static void reload() {
		NeoCore.loadFiles(new File(NeoCore.inst().getDataFolder(), "commandsets"), setLoader);
	}
	
	public static void runSet(String set, String[] args) {
		sets.get(set.toUpperCase()).run(args);
	}
}
