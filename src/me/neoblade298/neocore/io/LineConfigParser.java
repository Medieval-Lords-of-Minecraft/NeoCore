package me.neoblade298.neocore.io;

public interface LineConfigParser<A> {
	public A create(LineConfig cfg);
	public String getKey();
}
