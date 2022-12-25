package me.neoblade298.neocore.io;

import org.bukkit.plugin.java.JavaPlugin;

public class IOComponentWrapper {
	private String key;
	private int priority;
	private JavaPlugin plugin;
	private IOComponent component;
	private String db;
	private boolean disabled = false;
	public IOComponentWrapper(IOComponent component, String key, int priority, JavaPlugin plugin) {
		this.component = component;
		this.key = key;
		this.priority = priority;
		this.plugin = plugin;
	}
	public IOComponent getComponent() {
		return component;
	}
	public String getKey() {
		return key;
	}
	public int getPriority() {
		return priority;
	}
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public void setDatabase(String db) {
		this.db = db;
	}
	public String getDatabase() {
		return db;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public boolean isDisabled() {
		return disabled;
	}
}
