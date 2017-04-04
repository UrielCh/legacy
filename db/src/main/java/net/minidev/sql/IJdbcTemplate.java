package net.minidev.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.ObjectUtils;

/**
 * contain an extanded subset of used SpringJDBCTemplate.
 * 
 * @see org.springframework.jdbc.core.simple.SimpleJdbcTemplate
 * @see org.springframework.jdbc.core.simple.SimpleJdbcCall
 * 
 * @author uriel
 */
public class IJdbcTemplate {
	private final NamedParameterJdbcTemplate namedParameterJdbcOperations;
	private HashMap<Class<?>, RowMapper<?>> myRowMapper;
	private DataSource ds;

	public DataSource getDataSource() {
		return ds;
	}

	public IJdbcTemplate(DataSource dataSource) {
		this.ds = dataSource;
		this.namedParameterJdbcOperations = new NamedParameterJdbcTemplate(dataSource);
		myRowMapper = new HashMap<Class<?>, RowMapper<?>>();
	}

	public <T> void registerRowMapper(Class<T> clazz, RowMapper<T> rm) {
		this.myRowMapper.put(clazz, rm);
	}

	// final KeyHolder generatedKeyHolder
	public int update(String sql, Object... args) throws DataAccessException {
		return (ObjectUtils.isEmpty(args) ? getJdbcOperations().update(sql) : getJdbcOperations().update(sql,
				getArguments(args)));
	}

	public int updateNoTr(String sql) {
		Connection cnx = null;
		Statement stmt = null;
		try {
			cnx = ds.getConnection();
			stmt = cnx.createStatement();
			return stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage() + " " + sql, e);
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e2) {
				}
			if (cnx != null)
				DatabaseTemplate.close(ds, cnx);
		}
	}

	public int update(String sql, KeyHolder keyHolder, Object... args) throws DataAccessException {
		// List<SqlParameter> params = new ArrayList<SqlParameter>(args.length);
		int[] paramType = new int[args.length];
		// Types
		int p = 0;
		for (Object obj : args) {
			if (obj == null)
				obj = "";
			int type = StatementCreatorUtils.javaTypeToSqlParameterType(obj.getClass());
			paramType[p++] = type;
			// SqlParameterValue value = new SqlParameterValue(type, obj);
			// params.add(value);
		}
		PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sql, paramType);
		pscf.setReturnGeneratedKeys(true);
		PreparedStatementCreator psc = pscf.newPreparedStatementCreator(getArguments(args));
		int ret = getJdbcOperations().update(psc, keyHolder);
		return ret;
	}

	// PreparedStatementCreator

	public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
		return doExecuteBatchUpdate(sql, batchArgs, new int[0]);
	}

	public int queryForInt(String sql, Object... args) throws DataAccessException {
		return ObjectUtils.isEmpty(args) ? getJdbcOperations().queryForObject(sql, Integer.TYPE) : getJdbcOperations().queryForObject(sql, Integer.TYPE,
				getArguments(args));
	}

	public String queryForString(String sql, Object... args) throws DataAccessException {
		return ObjectUtils.isEmpty(args) ? getJdbcOperations().queryForObject(sql, String.class) : getJdbcOperations()
				.queryForObject(sql, getArguments(args), String.class);
	}

	/**
	 * oldname queryForObject Return a Simple Object (Integer / Long / Date ...)
	 */
	public <T> T queryForPrimitive(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
		return ObjectUtils.isEmpty(args) ? getJdbcOperations().queryForObject(sql, requiredType) : getJdbcOperations()
				.queryForObject(sql, getArguments(args), requiredType);
	}

	/**
	 * ParameterizedRowMapper -&gt; RowMapper in spring 3
	 */
	public <T> T queryForObject(String sql, RowMapper<T> rm, Object... args) throws DataAccessException {
		return ObjectUtils.isEmpty(args) ? getJdbcOperations().queryForObject(sql, rm) : getJdbcOperations()
				.queryForObject(sql, getArguments(args), rm);
	}

	/**
	 * Return a list of Integer, Long, Strings, Date...
	 * ParameterizedSingleColumnRowMapper -&gt; SingleColumnRowMapper in spring 3
	 */
	public <T> List<T> queryForPrimitives(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
		SingleColumnRowMapper<T> rm = new SingleColumnRowMapper<T>(requiredType);
		return ObjectUtils.isEmpty(args) ? getJdbcOperations().query(sql, rm) : getJdbcOperations().query(sql,
				getArguments(args), rm);
	}

	@SuppressWarnings("unchecked")
	private <T> RowMapper<T> getRowMapper(Class<T> mappedClass) {
		RowMapper<T> rm;
		rm = (RowMapper<T>) myRowMapper.get(mappedClass);
		if (rm == null)
			rm = new BeanPropertyRowMapper<T>(mappedClass);
		// rm = ParameterizedBeanPropertyRowMapper.newInstance(mappedClass);
		return rm;
	}

	/**
	 * Return complex structure mapped from multi collumns resultset. old name:
	 * queryForObjectsComplex
	 */
	public <T> List<T> queryForObjects(String sql, Class<T> mappedClass, Object... args) throws DataAccessException {
		RowMapper<T> rm = getRowMapper(mappedClass);
		return ObjectUtils.isEmpty(args) ? getJdbcOperations().query(sql, rm) : getJdbcOperations().query(sql,
				getArguments(args), rm);
	}

	public List<Map<String, Object>> queryForList(String sql, Object... args) throws DataAccessException {
		return (ObjectUtils.isEmpty(args) ? getJdbcOperations().queryForList(sql) : getJdbcOperations().queryForList(
				sql, getArguments(args)));
	}

	/**
	 * oldname queryForObjectComplex Return a Complex Object
	 */
	public <T> T queryForObject(String sql, Class<T> requiredType, Object... args) throws DataAccessException {
		try {
			RowMapper<T> rm = getRowMapper(requiredType); // Todo : ADD FastMap
			return (ObjectUtils.isEmpty(args) ? getJdbcOperations().queryForObject(sql, rm) : getJdbcOperations()
					.queryForObject(sql, getArguments(args), rm));
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T queryForObjectNatif(String sql, Class<T> requiredType) throws RuntimeException {
		return (T) queryNative(sql, requiredType, true);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> queryForObjectsNatif(String sql, Class<T> requiredType) throws RuntimeException {
		return (List<T>) queryNative(sql, requiredType, false);
	}

	/**
	 * return an Object or a list of object
	 */
	private <T> Object queryNative(String sql, Class<T> requiredType, boolean singleResult) throws RuntimeException {
		Connection cnx = null;
		Statement stmt = null;
		ResultSet rs = null;
		RuntimeException ex = null;
		try {
			// Todo : ADD FastMap
			RowMapper<T> rm = getRowMapper(requiredType);
			cnx = ds.getConnection();
			stmt = cnx.createStatement();
			rs = stmt.executeQuery(sql);

			// case 0 result Found
			if (!rs.next()) {
				if (singleResult)
					return null;
				return new ArrayList<T>(0);
			}

			T result = rm.mapRow(rs, 0);
			if (!rs.next()) {
				// case only 1 result Found
				if (singleResult)
					return result;
				else {
					ArrayList<T> list = new ArrayList<T>(1);
					list.add(result);
					return list;
				}
			}

			if (singleResult)
				ex = new RuntimeException("Ony one Result Expected.");
			else {
				ArrayList<T> list = new ArrayList<T>();
				list.add(result);
				int pos = 1;
				
				do {
					list.add(rm.mapRow(rs, pos++));
				} while (rs.next());
				return list;
			}
		} catch (SQLException e) {
			ex = new RuntimeException(e);
		} catch (RuntimeException e) {
			ex = e;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e1) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e1) {
				}

			if (cnx != null)
				DatabaseTemplate.close(ds, cnx);
		}
		throw ex;
	}

	public <T> List<T> query(String sql, RowMapper<T> rm, Object... args) throws DataAccessException {
		return (List<T>) (ObjectUtils.isEmpty(args) ? getJdbcOperations().query(sql, rm) : getJdbcOperations().query(
				sql, getArguments(args), rm));
	}

	/**
	 * Expose the Spring NamedParameterJdbcTemplate to allow invocation of less
	 * commonly used methods.
	 */
	public NamedParameterJdbcTemplate getNamedParameterJdbcOperations() {
		return this.namedParameterJdbcOperations;
	}

	/**
	 * Expose the classic Spring JdbcTemplate to allow invocation of less
	 * commonly used methods.
	 */
	public JdbcOperations getJdbcOperations() {
		return this.namedParameterJdbcOperations.getJdbcOperations();
	}

	/**
	 * Considers an Object array passed into a varargs parameter as collection
	 * of arguments rather than as single argument.
	 */
	private Object[] getArguments(Object[] varArgs) {
		if (varArgs.length == 1 && varArgs[0] instanceof Object[]) {
			return (Object[]) varArgs[0];
		} else {
			return varArgs;
		}
	}

	private int[] doExecuteBatchUpdate(String sql, final List<Object[]> batchValues, final int[] columnTypes) {
		return getJdbcOperations().batchUpdate(sql, new BatchPreparedStatementSetter() {

			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Object[] values = batchValues.get(i);
				doSetStatementParameters(values, ps, columnTypes);
			}

			public int getBatchSize() {
				return batchValues.size();
			}
		});
	}

	private void doSetStatementParameters(Object[] values, PreparedStatement ps, int[] columnTypes) throws SQLException {
		int colIndex = 0;
		for (Object value : values) {
			colIndex++;
			if (value instanceof SqlParameterValue) {
				SqlParameterValue paramValue = (SqlParameterValue) value;
				StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
			} else {
				int colType;
				if (columnTypes == null || columnTypes.length < colIndex) {
					colType = SqlTypeValue.TYPE_UNKNOWN;
				} else {
					colType = columnTypes[colIndex - 1];
				}
				StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
			}
		}
	}
}
