package me.neoblade298.neocore.shared.util;

public class SQLQueryBuilder {
	private String str;
	private boolean firstComma = true;
	private boolean hasConditions = false;
	public SQLQueryBuilder(SQLAction action, String db) {
		str = action + " INTO " + db + " VALUES(";
	}
	
	public SQLQueryBuilder addString(String str) {
		handleComma();
		str += "'" + str + "'";
		return this;
	}
	
	public SQLQueryBuilder addValue(int i) {
		handleComma();
		str += i;
		return this;
	}
	
	public SQLQueryBuilder addValue(double d) {
		handleComma();
		str += d;
		return this;
	}
	
	public SQLQueryBuilder addValue(long l) {
		handleComma();
		str += l;
		return this;
	}
	
	public SQLQueryBuilder addInt(String s) {
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
	
	public SQLQueryBuilder addCondition(String condition) {
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
