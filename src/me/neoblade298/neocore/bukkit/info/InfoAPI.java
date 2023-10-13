package me.neoblade298.neocore.bukkit.info;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Bukkit;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.io.FileLoader;

public class InfoAPI {
	private static HashMap<String, BossInfo> bossInfo = new HashMap<String, BossInfo>();
	private static final FileLoader bossLoader;
	
	static {
		bossLoader = (cfg, file) -> {
			if (!Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) return;
			for (String key : cfg.getKeys()) {
				BossInfo bi = new BossInfo(cfg.getSection(key));
				bossInfo.put(bi.getKey(), bi);
			}
		};
	}
	
	public static BossInfo getBossInfo(String boss) {
		return bossInfo.get(boss);
	}
	
	public static void reload() {
		bossInfo.clear();
		NeoCore.loadFiles(new File(NeoCore.inst().getDataFolder() + "/info/bosses.yml"), bossLoader);
	}
}
