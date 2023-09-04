package me.neoblade298.neocore.shared.util;

public class SQLInsertBuilder {
	private String str;
	private boolean firstValue = true;
	public SQLInsertBuilder(SQLAction action, String db) {
		str = action + " INTO " + db + " VALUES(";
	}
	
	public SQLInsertBuilder addString(String str) {
		handleComma();
		this.str += "'" + str + "'";
		return this;
	}
	
	public SQLInsertBuilder addValue(int i) {
		handleComma();
		str += i;
		return this;
	}
	
	public SQLInsertBuilder addValue(double d) {
		handleComma();
		str += d;
		return this;
	}
	
	public SQLInsertBuilder addValue(long l) {
		handleComma();
		str += l;
		return this;
	}
	
	public SQLInsertBuilder addValue(String s) {
		handleComma();
		str += s;
		return this;
	}
	
	private void handleComma() {
		if (!firstValue) {
			str += ",";
		}
		firstValue = false;
	}
	
	public String build() {
		return str + ");";
	}
	
	public enum SQLAction {
		INSERT, REPLACE;
	}
}
