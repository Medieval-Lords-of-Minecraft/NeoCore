package me.neoblade298.neocore.shared.io;

import java.sql.Statement;

public interface MultiStatementExecutor {
	public void use(Statement[] stmts);
}
