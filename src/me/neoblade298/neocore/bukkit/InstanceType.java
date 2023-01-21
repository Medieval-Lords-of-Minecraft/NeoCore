package me.neoblade298.neocore.bukkit;

public enum InstanceType {
	TOWNY(false),
	QUESTS(true),
	DEV(true),
	SESSIONS(true),
	HUB(false),
	CREATIVE(false),
	OTHER(false);
	
	private final boolean usesSkillAPI;
	private InstanceType(boolean usesSkillAPI) {
		this.usesSkillAPI = usesSkillAPI;
	}
	
	public boolean usesSkillAPI() {
		return this.usesSkillAPI;
	}
}
