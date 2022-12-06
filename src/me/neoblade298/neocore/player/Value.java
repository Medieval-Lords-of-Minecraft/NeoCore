package me.neoblade298.neocore.player;

public class Value {
	private Object v;
	private long expiration;
	
	public Value(Object v, long expiration) {
		this.v = v;
		this.expiration = expiration;
	}
	
	public Value(Object v) {
		this.v = v;
		this.expiration = -1;
	}

	public Object getValue() {
		return v;
	}

	public void setValue(Object v) {
		this.v = v;
	}

	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	
	public boolean isExpired() {
		return expiration != -1 && expiration > System.currentTimeMillis();
	}
}
