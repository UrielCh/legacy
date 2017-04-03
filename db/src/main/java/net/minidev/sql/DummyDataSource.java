package net.minidev.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SmartDataSource;

class DummyDataSource implements DataSource, SmartDataSource {
	Connection cnx;

	public DummyDataSource(Connection cnx) {
		this.cnx = cnx;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return cnx;
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return cnx;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean shouldClose(Connection con) {
		return false;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
}
