package me.neoblade298.neocore.shared.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SQLInsertBuilder {
	private final SQLAction action;
	private final String table;
	private final LinkedHashMap<String, Object> currentRow = new LinkedHashMap<>();
	private final List<Map<String, Object>> rows = new ArrayList<>();
	private List<String> columnOrder = null;

	public SQLInsertBuilder(SQLAction action, String table) {
		this.action = action;
		this.table = table;
	}

	public SQLInsertBuilder addValue(String column, String value) {
		currentRow.put(column, value);
		return this;
	}

	public SQLInsertBuilder addValue(String column, int value) {
		currentRow.put(column, value);
		return this;
	}

	public SQLInsertBuilder addValue(String column, long value) {
		currentRow.put(column, value);
		return this;
	}

	public SQLInsertBuilder addValue(String column, double value) {
		currentRow.put(column, value);
		return this;
	}

	public SQLInsertBuilder addValue(String column, boolean value) {
		currentRow.put(column, value);
		return this;
	}

	public SQLInsertBuilder addNull(String column, int sqlType) {
		currentRow.put(column, new NullValue(sqlType));
		return this;
	}

	/**
	 * Finalizes the current row and adds it to the batch.
	 * Call this between rows when inserting multiple rows.
	 */
	public SQLInsertBuilder addRow() {
		if (currentRow.isEmpty()) return this;
		if (columnOrder == null) {
			columnOrder = new ArrayList<>(currentRow.keySet());
		}
		rows.add(new LinkedHashMap<>(currentRow));
		currentRow.clear();
		return this;
	}

	/**
	 * Builds and returns a PreparedStatement with all batched rows ready for executeBatch().
	 * If addRow() was not called, the current pending row is auto-finalized.
	 */
	public PreparedStatement build(Connection con) throws SQLException {
		// Auto-finalize pending row
		if (!currentRow.isEmpty()) {
			addRow();
		}
		if (rows.isEmpty()) {
			throw new SQLException("SQLInsertBuilder: No rows to insert");
		}

		String sql = buildSQL();
		PreparedStatement ps = con.prepareStatement(sql);

		for (Map<String, Object> row : rows) {
			int index = 1;
			for (String col : columnOrder) {
				Object value = row.get(col);
				if (value instanceof NullValue) {
					ps.setNull(index, ((NullValue) value).sqlType);
				} else if (value instanceof String) {
					ps.setString(index, (String) value);
				} else if (value instanceof Integer) {
					ps.setInt(index, (Integer) value);
				} else if (value instanceof Long) {
					ps.setLong(index, (Long) value);
				} else if (value instanceof Double) {
					ps.setDouble(index, (Double) value);
				} else if (value instanceof Boolean) {
					ps.setBoolean(index, (Boolean) value);
				} else {
					ps.setObject(index, value);
				}
				index++;
			}
			ps.addBatch();
		}

		return ps;
	}

	private String buildSQL() {
		StringBuilder sb = new StringBuilder();
		sb.append(action.name()).append(" INTO ").append(table).append(" (");

		boolean first = true;
		for (String col : columnOrder) {
			if (!first) sb.append(", ");
			sb.append("`").append(col).append("`");
			first = false;
		}

		sb.append(") VALUES (");
		for (int i = 0; i < columnOrder.size(); i++) {
			if (i > 0) sb.append(", ");
			sb.append("?");
		}
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Returns the number of rows currently batched (including any pending row).
	 */
	public int getRowCount() {
		return rows.size() + (currentRow.isEmpty() ? 0 : 1);
	}

	private static class NullValue {
		final int sqlType;
		NullValue(int sqlType) {
			this.sqlType = sqlType;
		}
	}

	public enum SQLAction {
		INSERT, REPLACE;
	}
}
