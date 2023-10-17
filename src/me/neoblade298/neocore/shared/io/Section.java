package me.neoblade298.neocore.shared.io;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Section {
	protected String key;
	protected Map<String, Object> map;
	protected Config parent;
	
	public Section(String key, Map<Object, Object> temp, Config parent) {
		this(key, temp);
		this.parent = parent;
	}
	
	public Section(String key, Map<Object, Object> temp) {
		this.key = key;
		this.map = new HashMap<String, Object>();
		
		// Annoying, but no way for snakeyaml to read all keys as strings; it detects ints
		temp.forEach((k, v) -> map.put(k.toString(), v));
	}
	
	public Set<String> getKeys() {
		if (map == null) return Collections.emptySet();
		return map.keySet();
	}
	
	public int getInt(String key) {
		if (map == null) return 0;
		if (!map.containsKey(key)) {
			return 0;
		}
		return map.get(key) instanceof Integer ? (int) map.get(key) : ((Double) map.get(key)).intValue();
	}
	
	public int getInt(String key, int def) {
		if (map == null) return 0;
		if (!map.containsKey(key)) {
			return def;
		}
		return map.get(key) instanceof Integer ? (int) map.get(key) : ((Double) map.get(key)).intValue();
	}
	
	public double getDouble(String key) {
		if (map == null) return 0;
		if (!map.containsKey(key)) {
			return 0;
		}
		return map.get(key) instanceof Double ? (double) map.get(key) : ((Integer) map.get(key)).doubleValue();
	}
	
	public double getDouble(String key, double def) {
		if (map == null) return 0;
		if (!map.containsKey(key)) {
			return def;
		}
		return map.get(key) instanceof Double ? (double) map.get(key) : ((Integer) map.get(key)).doubleValue();
	}
	
	public String getString(String key) {
		if (map == null) return null;
		if (!map.containsKey(key)) {
			return null;
		}
		return (String) map.get(key);
	}
	
	public String getString(String key, String def) {
		if (map == null) return null;
		if (!map.containsKey(key)) {
			return def;
		}
		return (String) map.getOrDefault(key, def);
	}
	
	public boolean contains(String key) {
		if (map == null) return false;
		return map.containsKey(key);
	}
	
	public boolean isType(String key, Class<?> clazz) {
		if (map == null) return false;
		if (!map.containsKey(key)) return false;
		return clazz.isInstance(map.get(key));
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getStringList(String key) {
		if (map == null) return null;
		if (!map.containsKey(key)) {
			return null;
		}
		return (List<String>) map.get(key);
	}
	
	@SuppressWarnings("unchecked")
	public Section getSection(String key) {
		if (map == null) return null;
		if (map.get(key) == null) {
			return null;
		}
		return new Section(key, (Map<Object, Object>) map.get(key), this.parent);
	}
	
	public String getName() {
		return key;
	}
	
	public void set(String key, Object obj) {
		if (map == null) {
			map = new HashMap<String, Object>();
		}
		map.put(key, obj);
	}
	
	public Config getConfig() {
		return parent;
	}
	
	public Object get(String key) {
		return map.get(key);
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
}
