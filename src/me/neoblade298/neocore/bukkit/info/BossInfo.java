package me.neoblade298.neocore.bukkit.info;

import java.util.List;

import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import me.neoblade298.neocore.shared.io.Section;
import me.neoblade298.neocore.shared.util.SharedUtil;

public class BossInfo {
	private String key, display, displayWithLvl, displayWithLvlRounded, tag;
	private int level;
	private List<String> healthComponents;
	private double health = 0;
	
	public BossInfo(Section cfg) {
		this.key = cfg.getName();
		this.tag = "Killed" + this.key;
		this.display = SharedUtil.translateColors("&c" + cfg.getString("display", "DEFAULT"));
		this.level = cfg.getInt("level");
		int levelRounded = level - (level % 5);
		this.healthComponents = cfg.getStringList("health-components");
		this.displayWithLvl = "§6[Lv " + level + "] " + display;
		this.displayWithLvlRounded = "§6[Lv " + levelRounded + "] " + display;
	}

	public String getKey() {
		return key;
	}

	public String getDisplay() {
		return display;
	}
	
	public String getDisplayWithLevel(boolean roundDown) {
		return roundDown ? displayWithLvlRounded : displayWithLvl;
	}

	public int getLevel(boolean roundDown) {
		return roundDown ? level - (level % 5) : level;
	}

	public double getTotalHealth() {
		if (health == 0) {
			MobManager mm = MythicBukkit.inst().getMobManager();
			for (String mob : healthComponents) {
				health += mm.getMythicMob(mob).get().getHealth().get();
			}
		}
		return health;
	}
	
	public String getTag() {
		return tag;
	}
}
