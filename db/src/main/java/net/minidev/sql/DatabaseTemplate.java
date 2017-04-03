package net.minidev.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.SmartDataSource;

import net.minidev.util.LangUtils;

/**
 * base de template generic SQL
 * 
 * contient toutes les operation generic utilises en SQL
 * 
 * @see DatabaseAccess
 * 
 * @author Uriel Chemouni
 */
public final class DatabaseTemplate {
	public final static boolean PEDANTIC_MODE = false;
	public final static int QUERY_TIMEOUT = 0;

	private final static int[] NO_INT = new int[0];
	private final static long[] NO_LONG = new long[0];
	private final static String[] NO_STRING = new String[0];
	private final static Object[] NO_OBJECT = new Object[0];

	// private final static Logger LOGGER =
	// Logger.getLogger(name)(DatabaseTemplate.class);

	private DatabaseTemplate() {
	}

	private static void handleError(String msg, Exception e) {
		// throw new JpaException(msg, e);
		throw new RuntimeException(msg, e);
	}

	private static void handleError(Exception e) {
		// throw new JpaException(e);
		throw new RuntimeException(e);
	}

	private static void logError(String msg) {
	}

	public static DataSource cast(Connection cnx) {
		return new DummyDataSource(cnx);
	}

	/**
	 * Select FOUND_ROWS from a statment
	 * 
	 * @param stmt
	 * @return -1 if no found rows founed
	 */
	static public int retrieveFoundRows(Statement stmt) {
		int result = -1;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("SELECT FOUND_ROWS()");
			if (rs.next())
				result = rs.getInt(1);
			close(rs);
		} catch (Exception e) {
			close(rs);
			handleError("Failed Executing SELECT FOUND_ROWS():" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * return an int value or -1 if value is missing
	 */
	static public int retrieveInt(DataSource ds, String query) {
		Number n = (Number) retrieveSingleObject(ds, query);
		if (n == null)
			return -1;
		return n.intValue();
	}

	/**
	 * return an int value or -1 if value is missing
	 */
	static public Integer retrieveInteger(DataSource ds, String query) {
		Number n = (Number) retrieveSingleObject(ds, query);
		if (n == null)
			return null;
		if (n instanceof Integer)
			return (Integer) n;
		return n.intValue();
	}

	/**
	 * return an long value or null if value is missing
	 */
	static public Long retrieveLong(DataSource ds, String query) {
		Number n = (Number) retrieveSingleObject(ds, query);
		if (n == null)
			return null;
		if (n instanceof Long)
			return (Long) n;
		return n.longValue();
	}

	static public Date retrieveDate(DataSource ds, String query) {
		return (Date) retrieveSingleObject(ds, query);
	}

	static public Date[] retrieveDates(DataSource ds, String query) {
		Object[] objs = retrieveObjects(ds, query);
		if (objs.length == 0)
			return new Date[0];
		Date[] result = new Date[objs.length];
		for (int i = 0; i < objs.length; i++) {
			result[i] = (Date) objs[i];
		}
		return result;
	}

	static public Object retrieveSingleObject(DataSource ds, String query) {
		Connection cnx = null;
		Statement stmt = null;
		ResultSet rs = null;
		Object result = null;
		try {
			cnx = ds.getConnection();
			stmt = cnx.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next())
				result = rs.getObject(1);
			close(ds, cnx, stmt, rs);
		} catch (Exception e) {
			close(ds, cnx, stmt, rs);
			handleError(e);
		}
		return result;
	}

	static public String retrieveString(DataSource ds, String query) {
		Object n = retrieveSingleObject(ds, query);
		if (n == null)
			return null;
		return n.toString();
	}

	/**
	 * Get All data and model
	 * 
	 * @param ds
	 *            the Data Source
	 * @param query
	 *            a valid SQL Query
	 */
	static public ArrayList<LinkedHashMap<String, Object>> retrieveDebug(DataSource ds, String query) {
		ArrayList<LinkedHashMap<String, Object>> out = new ArrayList<LinkedHashMap<String, Object>>();
		Connection cnx = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnx = ds.getConnection();
			stmt = cnx.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData meta = rs.getMetaData();
			int colcount = meta.getColumnCount();
			int colidMax = colcount + 1;
			while (rs.next()) {
				LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
				for (int j = 1; j < colidMax; j++) {
					Object v = rs.getObject(j);
					map.put(meta.getColumnLabel(j), v);
				}
				out.add(map);
			}
			close(ds, cnx, stmt, rs);
		} catch (Exception e) {
			close(ds, cnx, stmt, rs);
			handleError(e);
		}
		return out;
	}

	/**
	 * Get All rows and column, returned by a query
	 * 
	 * @param ds
	 *            the Data Source
	 * @param query
	 *            a valid SQL Query
	 */
	static public Object[] retrieveObjects(DataSource ds, String query) {
		Object[] objects = NO_OBJECT;
		Connection cnx = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnx = ds.getConnection();
			stmt = cnx.createStatement();
			rs = stmt.executeQuery(query);
			int colcount = rs.getMetaData().getColumnCount();
			int colidMax = colcount + 1;

			int pos = 0;
			while (rs.next()) {
				if (pos + colcount >= objects.length) {
					int newSize = colcount * 3 + objects.length * 2;
					objects = LangUtils.realloc(objects, newSize);
				}

				for (int j = 1; j < colidMax; j++) {
					Object v = rs.getObject(j);
					objects[pos++] = v;
				}
			}
			// realloc table
			objects = LangUtils.realloc(objects, pos);
			close(ds, cnx, stmt, rs);
		} catch (Exception e) {
			close(ds, cnx, stmt, rs);
			handleError(e);
		}
		return objects;
	}

	static public String[] retrieveStrings(DataSource ds, String query) {
		Object[] objs = retrieveObjects(ds, query);
		if (objs.length == 0)
			return NO_STRING;
		String[] result = new String[objs.length];
		for (int i = 0; i < objs.length; i++) {
			Object o = objs[i];
			if (o != null)
				result[i] = o.toString();
		}
		return result;
	}

	static public <T extends Enum<T>> T[] retrieveEnums(DataSource ds, Class<T> type, String query) {
		T[] strs = LangUtils.alloc(type, 0);
		Connection cnx = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			int i = 0;
			cnx = ds.getConnection();
			stmt = cnx.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				String v = rs.getString(1);
				if (i >= strs.length)
					strs = LangUtils.realloc(strs, 10 + strs.length * 2);
				strs[i++] = (T) Enum.valueOf(type, v);
			}
			// realloc table
			strs = LangUtils.realloc(strs, i);
			close(ds, cnx, stmt, rs);
		} catch (Exception e) {
			close(ds, cnx, stmt, rs);
			handleError(e);
		}
		return strs;
	}

	/**
	 * Return all data contains in query result as int[];
	 */
	static public int[] retrieveInts(DataSource ds, String query) {
		Object[] objs = retrieveObjects(ds, query);
		if (objs.length == 0)
			return NO_INT;
		int[] result = new int[objs.length];
		for (int i = 0; i < objs.length; i++) {
			Object o = objs[i];
			if (o != null)
				result[i] = ((Number) o).intValue();
		}
		return result;
	}

	/**
	 * Return all data contains in query result as long[];
	 */
	static public long[] retrievelongs(DataSource ds, String query) {
		Object[] objs = retrieveObjects(ds, query);
		if (objs.length == 0)
			return NO_LONG;
		long[] result = new long[objs.length];
		for (int i = 0; i < objs.length; i++) {
			Object o = objs[i];
			if (o != null)
				result[i] = ((Number) o).longValue();
		}
		return result;
	}

	/**
	 * Constructor from default dataSource
	 */
	static public int doUpdateSilent(DataSource ds, String query) {
		try {
			return doUpdate(ds, query);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * Constructor from default dataSource
	 */
	static public int doUpdate(DataSource ds, String query) {
		Connection cnx = null;
		int result = 0;
		Statement stmt = null;
		try {
			cnx = ds.getConnection();
			stmt = cnx.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setQueryTimeout(QUERY_TIMEOUT);
			result = stmt.executeUpdate(query);
			close(ds, cnx, stmt, null);
		} catch (SQLException e) {
			System.err.println(e);
			System.err.println(query);
			// e.printStackTrace();
			close(ds, cnx, stmt, null);
			if (e.getErrorCode() == 1062)
				logError("duplicate Key within :" + query);
			else
				logError("doUpdate Fail on :" + query);
			handleError(query, e);
		}
		return result;
	}

	static public int doInsertAutoIncrement(DataSource ds, String query, KeyHolder keyHolder) {
		Connection cnx = null;
		int result = -1;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			cnx = ds.getConnection();
			stmt = cnx.createStatement();
			stmt.setQueryTimeout(QUERY_TIMEOUT);
			result = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

			rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				keyHolder.key = rs.getInt(1);
			}
			close(ds, cnx, stmt, rs);
		} catch (Exception e) {
			close(ds, cnx, stmt, rs);
			handleError(e);
		}
		return result;
	}

	/**
	 * close: resultset, statment and connexion.
	 * 
	 * @param rs
	 */
	static public void fullClose(DataSource ds, ResultSet rs) {
		if (rs == null) {
			if (PEDANTIC_MODE)
				throw new NullPointerException("Internal Error, Ressource Leek !!");
			return;
		}
		try {
			Statement stmt = rs.getStatement();
			Connection cnx = stmt.getConnection();
			close(ds, cnx, stmt, rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void close(DataSource ds, Connection cnx, Statement stmt, ResultSet rs) {
		close(rs);
		close(stmt);
		close(ds, cnx);
	}

	public static void close(DataSource ds, Connection cnx) {
		try {
			if (!(ds instanceof SmartDataSource) || ((SmartDataSource) (ds)).shouldClose(cnx))
				close(cnx);
		} catch (Exception e2) {
		}
	}

	private static void close(ResultSet rs) {
		if (rs == null)
			return;
		try {
			rs.close();
		} catch (Exception e) {
		}
	}

	private static void close(Statement stmt) {
		if (stmt == null)
			return;
		try {
			stmt.close();
		} catch (Exception e) {
		}
	}

	private static void close(Connection cnx) {
		if (cnx == null)
			return;
		try {
			cnx.close();
		} catch (Exception e) {
		}
	}

}
