package me.neoblade298.neocore.shared.io;

import java.sql.Connection;

public class ConnectionDetails {
	private String user;
	private Connection connection;
	private long timestamp;
	public ConnectionDetails(String user, Connection connection) {
		this.user = user;
		this.connection = connection;
		timestamp = System.currentTimeMillis();
	}
	public String getUser() {
		return user;
	}
	public Connection getConnection() {
		return connection;
	}
	public long getTimeCreated() {
		return timestamp;
	}
}
