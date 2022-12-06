package me.neoblade298.neocore.commandsets;

import java.io.File;
import java.util.HashMap;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.io.FileLoader;

public class CommandSetManager {
	private static HashMap<String, CommandSet> sets = new HashMap<String, CommandSet>();
	
	private static final FileLoader setLoader;
	
	static {
		setLoader = (cfg, file) -> {
			for (String key : cfg.getKeys(false)) {
				sets.put(key.toUpperCase(), new CommandSet(key, cfg.getConfigurationSection(key)));
			}
		};
	}
	
	public static void reload() {
		try {
			NeoCore.loadFiles(new File(NeoCore.inst().getDataFolder(), "commandsets"), setLoader);
		} catch (NeoIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void runSet(String set, String[] args) {
		sets.get(set.toUpperCase()).run(args);
	}
}
