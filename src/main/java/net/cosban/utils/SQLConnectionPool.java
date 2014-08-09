package net.cosban.utils;

import java.io.Closeable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SQLConnectionPool implements Closeable {
	private final static int		poolSize	= 10;
	private final static long		lifetime	= 300000;

	private final String			url, user, password;

	private ArrayList<IConnection>	connections;
	private CloserTask				closerTask;

	private final Lock				lock		= new ReentrantLock();

	public SQLConnectionPool(String url, String user, String password) throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		this.url = url;
		this.user = user;
		this.password = password;
		connections = new ArrayList<IConnection>(poolSize);
		closerTask = new CloserTask();
	}

	public CloserTask getCloser() {
		return closerTask;
	}

	@Override
	public void close() {
		lock.lock();
		final Iterator<IConnection> conns = connections.iterator();
		while (conns.hasNext()) {
			final IConnection c = conns.next();
			connections.remove(c);
			c.terminate();
		}
		lock.unlock();
	}

	public Connection getConnection() throws SQLException {
		lock.lock();
		try {
			final Iterator<IConnection> iter = connections.iterator();
			while (iter.hasNext()) {
				final IConnection conn = iter.next();
				if (conn.lease()) {
					if (conn.isValid()) {
						return conn;
					}
					connections.remove(conn);
					conn.terminate();
				}
			}
			final IConnection conn = new IConnection(DriverManager.getConnection(url, user, password));
			conn.lease();
			if (!conn.isValid()) {
				conn.terminate();
				throw new SQLException("Failed to validate a brand new connection");
			}
			connections.add(conn);
			return conn;
		} finally {
			lock.unlock();
		}
	}

	private void closeConnections() {
		lock.lock();
		final long stale = System.currentTimeMillis() - lifetime;
		final Iterator<IConnection> conns = connections.iterator();
		while (conns.hasNext()) {
			final IConnection c = conns.next();
			if (c.isInUse() && stale > c.getLastUsage() && !c.isValid()) {
				conns.remove();
			}
		}
		lock.unlock();
	}

	private class CloserTask implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(lifetime);
				} catch (final InterruptedException e) {}
				closeConnections();
			}
		}
	}

	private class IConnection implements Connection {

		private final Connection	c;
		private boolean				isInUse;
		private long				lastUsage;
		private int					endLife;

		private String				schema;

		IConnection(Connection c) {
			this.c = c;
			this.isInUse = false;
			this.lastUsage = 0;
			this.endLife = 30;
			this.schema = "default";
		}

		public void abort(Executor executor) throws SQLException {

		}

		@Override
		public void clearWarnings() throws SQLException {
			c.clearWarnings();
		}

		@Override
		public void close() {
			this.isInUse = false;
			try {
				if (!c.getAutoCommit()) {
					c.setAutoCommit(true);
				}
			} catch (SQLException e) {
				connections.remove(this);
				terminate();
			}

		}

		@Override
		public void commit() throws SQLException {
			c.commit();
		}

		@Override
		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
			return c.createArrayOf(typeName, elements);
		}

		@Override
		public Blob createBlob() throws SQLException {
			return c.createBlob();
		}

		@Override
		public Clob createClob() throws SQLException {
			return c.createClob();
		}

		@Override
		public NClob createNClob() throws SQLException {
			return c.createNClob();
		}

		@Override
		public SQLXML createSQLXML() throws SQLException {
			return c.createSQLXML();
		}

		@Override
		public Statement createStatement() throws SQLException {
			return c.createStatement();
		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return c.createStatement(resultSetType, resultSetConcurrency);
		}

		@Override
		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return c.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
			return c.createStruct(typeName, attributes);
		}

		@Override
		public boolean getAutoCommit() throws SQLException {
			return c.getAutoCommit();
		}

		@Override
		public String getCatalog() throws SQLException {
			return c.getCatalog();
		}

		@Override
		public Properties getClientInfo() throws SQLException {
			return c.getClientInfo();
		}

		@Override
		public String getClientInfo(String name) throws SQLException {
			return c.getClientInfo(name);
		}

		@Override
		public int getHoldability() throws SQLException {
			return c.getHoldability();
		}

		private long getLastUsage() {
			return lastUsage;
		}

		@Override
		public DatabaseMetaData getMetaData() throws SQLException {
			return c.getMetaData();
		}

		@Override
		public int getNetworkTimeout() throws SQLException {
			return endLife;
		}

		@Override
		public String getSchema() throws SQLException {
			return schema;
		}

		@Override
		public int getTransactionIsolation() throws SQLException {
			return c.getTransactionIsolation();
		}

		@Override
		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return c.getTypeMap();
		}

		@Override
		public SQLWarning getWarnings() throws SQLException {
			return c.getWarnings();
		}

		@Override
		public boolean isClosed() throws SQLException {
			return c.isClosed();
		}

		private boolean isInUse() {
			return isInUse;
		}

		@Override
		public boolean isReadOnly() throws SQLException {
			return c.isReadOnly();
		}

		boolean isValid() {
			try {
				return c.isValid(1);
			} catch (final SQLException ex) {
				return false;
			}
		}

		@Override
		public boolean isValid(int timeout) throws SQLException {
			return c.isValid(timeout);
		}

		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return c.isWrapperFor(iface);
		}

		@Override
		public String nativeSQL(String sql) throws SQLException {
			return c.nativeSQL(sql);
		}

		@Override
		public CallableStatement prepareCall(String sql) throws SQLException {
			return c.prepareCall(sql);
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
				throws SQLException {
			return c.prepareCall(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			return c.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		@Override
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			return c.prepareStatement(sql);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			return c.prepareStatement(sql, autoGeneratedKeys);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
				throws SQLException {
			return c.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
				int resultSetHoldability) throws SQLException {
			return c.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			return c.prepareStatement(sql, columnIndexes);
		}

		@Override
		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			return c.prepareStatement(sql, columnNames);
		}

		@Override
		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			c.releaseSavepoint(savepoint);
		}

		@Override
		public void rollback() throws SQLException {
			c.rollback();
		}

		@Override
		public void rollback(Savepoint savepoint) throws SQLException {
			c.rollback();
		}

		@Override
		public void setAutoCommit(boolean autoCommit) throws SQLException {
			c.setAutoCommit(autoCommit);
		}

		@Override
		public void setCatalog(String catalog) throws SQLException {
			c.setCatalog(catalog);

		}

		@Override
		public void setClientInfo(Properties properties) throws SQLClientInfoException {
			c.setClientInfo(properties);
		}

		@Override
		public void setClientInfo(String name, String value) throws SQLClientInfoException {
			c.setClientInfo(name, value);
		}

		@Override
		public void setHoldability(int holdability) throws SQLException {
			c.setHoldability(holdability);
		}

		@Override
		public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
			endLife = milliseconds;
		}

		@Override
		public void setReadOnly(boolean readOnly) throws SQLException {
			c.setReadOnly(readOnly);
		}

		@Override
		public Savepoint setSavepoint() throws SQLException {
			return c.setSavepoint();
		}

		@Override
		public Savepoint setSavepoint(String name) throws SQLException {
			return c.setSavepoint(name);
		}

		@Override
		public void setSchema(String schema) throws SQLException {
			this.schema = schema;
		}

		@Override
		public void setTransactionIsolation(int level) throws SQLException {
			c.setTransactionIsolation(level);
		}

		@Override
		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			c.setTypeMap(map);
		}

		synchronized boolean lease() {
			if (isInUse) {
				return false;
			}
			isInUse = true;
			lastUsage = System.currentTimeMillis();
			return true;
		}

		void terminate() {
			try {
				c.close();
			} catch (final SQLException ex) {}
		}

		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return c.unwrap(iface);
		}
	}
}
