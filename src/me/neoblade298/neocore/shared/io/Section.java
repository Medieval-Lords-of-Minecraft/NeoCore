package me.neoblade298.neocore.shared.io;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Section {
	protected String key;
	protected Map<String, Object> map;
	private Config parent;
	
	public Section(String key, Map<String, Object> map) {
		this.key = key;
		this.map = map;
	}
	
	public Set<String> getKeys() {
		return map.keySet();
	}
	
	public int getInt(String key) {
		return (int) map.get(key);
	}
	
	public int getInt(String key, int def) {
		return (int) map.get(key);
	}
	
	public double getDouble(String key) {
		return (double) map.get(key);
	}
	
	public double getDouble(String key, double def) {
		return (double) map.get(key);
	}
	
	public String getString(String key, String def) {
		return (String) map.get(key);
	}
	
	public String getString(String key) {
		return (String) map.get(key);
	}
	
	public boolean contains(String key) {
		return map.containsKey(key);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getStringList(String key) {
		return (List<String>) map.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public Section getSection(String key) {
		return new Section(key, (Map<String, Object>) map.get(key));
	}
	
	public String getName() {
		return key;
	}
	
	public void set(String key, Object obj) {
		map.put(key, obj);
	}
	
	public Config getConfig() {
		return parent;
	}
}
