package me.neoblade298.neocore.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class CachedObject<T> {
	private T obj;
	private LocalDateTime cachedTime;
	
	public CachedObject(T obj) {
		this.obj = obj;
		cachedTime = LocalDateTime.now();
	}
	
	public T get() {
		return obj;
	}
	
	public boolean cachedLongerThan(Duration d) {
		return Duration.between(cachedTime, LocalDateTime.now()).compareTo(d) > 0;
	}
	
	public void setCacheTime(LocalDateTime newTime) {
		cachedTime = newTime;
	}
}
