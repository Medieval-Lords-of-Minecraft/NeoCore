package me.neoblade298.neocore.shared.util;

public class SQLInsertBuilder {
	private String str;
	private boolean firstComma = true;
	private boolean hasConditions = false;
	public SQLInsertBuilder(SQLAction action, String db) {
		str = action + " INTO " + db + " VALUES(";
	}
	
	public SQLInsertBuilder addString(String str) {
		handleComma();
		str += "'" + str + "'";
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
	
	public SQLInsertBuilder addInt(String s) {
		handleComma();
		str += s;
		return this;
	}
	
	private void handleComma() {
		if (firstComma) {
			str += ",";
			firstComma = false;
		}
	}
	
	public SQLInsertBuilder addCondition(String condition) {
		if (!hasConditions) {
			str += ") WHERE ";
		}
		else {
			str += " AND ";
		}
		str += condition;
		return this;
	}
	
	public String build() {
		if (!hasConditions) {
			str += ")";
		}
		return str + ";";
	}
	
	public enum SQLAction {
		INSERT, REPLACE;
	}
}
