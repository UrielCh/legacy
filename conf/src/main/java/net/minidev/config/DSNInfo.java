package net.minidev.config;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.SmartDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Contain a Database JDBC connexion Option Create a DSN Info Object from a
 * configuration File
 * 
 * Contenaing the dsn.%nom_du_dsn%.url=... jdbc:mysql://127.0.0.1/MyBase
 * dsn.%nom_du_dsn%.username=... dsn.%nom_du_dsn%.password=...
 * dsn.%nom_du_dsn%.option=utf8 auto zlib ...
 * dsn.%nom_du_dsn%.ddl-generation=xxx
 * 
 * @author Uriel Chemouni
 * 
 */
public class DSNInfo {
	Logger logger = Logger.getLogger(DSNInfo.class.getName());

	/**
	 * Log JPA SQL QUERY in FILE.
	 */
	public static String JPA_LOG_FILE = null;

	private ServerType serverType;
	public String name;
	public String driverClassName;
	public String url;
	public String username;
	public String password;
	public String version;
	private String ddl_generation = "none";
	public String option;
	public Integer maxIdle;
	public Integer maxActive;
	private Properties additionnalProperties = new Properties();

	public void addProp(String key, String value) {
		additionnalProperties.put(key, value);
	}

	public String toString() {
		return "DSN:" + getFullUrl();
	}

	public void setDdlNone() {
		this.ddl_generation = "none";
	}

	public void setDdlCreate() {
		this.ddl_generation = "create-tables";
	}

	public void setDdlReset() {
		this.ddl_generation = "drop-and-create-tables";
	}

	public static enum ServerType {
		mysql, mssql, derby, oracle, postgray, sqlite, hsqldb, odbc
	}

	static ConfigurationKey.KeyMeta META1;
	static {
		META1 = new ConfigurationKey.KeyMeta();
		META1.errorMessage = "$(key) must contain a valid jdbc resource link like: \n"
				+ "jdbc:mysql://127.0.0.1/MyBase\n" + "jdbc:sqlite:/MyBase/simple.sqlite\n"
				+ "jdbc:derby:/MyBase/mybase;create=true";
		META1.suggestValue = "jdbc:mysql://127.0.0.1/MyBase";
	}

	static ConfigurationKey.KeyMeta META2;
	static {
		META2 = new ConfigurationKey.KeyMeta();
		META2.errorMessage = "you must provide a valid database password in $(key)";
	}

	static ConfigurationKey.KeyMeta META3;
	static {
		META3 = new ConfigurationKey.KeyMeta();
		META3.errorMessage = "you must provide a valid database username in $(key)";
	}

	/**
	 * see com.mysql.jdbc.ConnectionPropertiesImpl
	 */
	public Properties getConnectionProperties() {
		Properties properties = new Properties();
		if (serverType == ServerType.mysql) {
			if (option.indexOf("utf8") >= 0 || option.indexOf("utf-8") >= 0) {
				properties.put("useUnicode", "true");
				properties.put("characterEncoding", "UTF-8");
			}
			if (option.indexOf("auto") >= 0) {
				properties.put("autoReconnect", "true");
				// properties.put("autoReconnectForPools","true");
				// add internal ping
				properties.put("reconnectAtTxEnd", "true");
			}
			if (option.indexOf("zlib") >= 0 || option.indexOf("zip") >= 0)
				properties.put("useCompression", "true");
			if (option.indexOf("debug") >= 0) {
				properties.put("gatherPerfMetrics", "true");
				properties.put("profileSQL", "true");
			}
			/**
			 * cache les MetaData par connextion (si ConnectorJ > 5.0.5)
			 * 
			 * @see ConnectionPropertiesImpl
			 */
			properties.put("cacheResultSetMetadata", "true");
		}

		if (serverType == ServerType.postgray) {
			if (option.indexOf("utf8") >= 0 || option.indexOf("utf-8") >= 0) {
				properties.put("charSet", "UTF-8");
			}
			if (option.indexOf("debug") >= 0) {
				properties.put("loglevel", "2");
			}
		}
		properties.putAll(additionnalProperties);
		return properties;
	}

	/**
	 * see http://dev.mysql.com/doc/refman/5.0/en/connector-j-reference-configuration-properties.html
	 */
	public String getFullUrl() {
		boolean containsOpt = url.indexOf('?') > 0;
		if (option == null || option.length() == 0)
			return url;

		StringBuilder sb = new StringBuilder(url);

		Properties properties = getConnectionProperties();
		if (properties.size() > 0) {
			sb.append(containsOpt ? '&' : '?');
			for (Object k : properties.keySet()) {
				String key = (String) k;
				Object value = properties.getProperty(key);
				sb.append(key).append("=").append(value).append("&");
			}
		}
		return sb.toString();
	}

	public String getUrl() {
		int p = url.indexOf("?");
		if (p == -1)
			return url;
		return url.substring(0, p);
	}

	/**
	 * ie: jdbc:mysql://127.0.0.1/MyBase
	 * 
	 * ie: jdbc:hsqldb:file:testdb
	 * 
	 * @param url
	 *            a jdbc string
	 */
	public DSNInfo(String url) {
		this.url = url;
		this.config();
	}

	public DSNInfo(File derbyBase) {
		boolean isSQLite = false;
		try {
			RandomAccessFile ra = new RandomAccessFile(derbyBase, "r");
			byte[] data = new byte[16];
			int len = ra.read(data);
			String s = new String(data, 0, len);
			if (s.startsWith("SQLite")) // + " " + r[i+1]
				isSQLite = true;
			ra.close();
		} catch (Exception e) {
		}
		// SQLite format 3 //15
		if (isSQLite) {// derbyBase.getName().endsWith(".sqlite") ||
						// derbyBase.isFile()) {
			url = "jdbc:sqlite:" + derbyBase.getAbsolutePath().replace('\\', '/');
			this.name = "";
			this.password = "";
			this.config();
		} else {
			url = "jdbc:derby:" + derbyBase.getAbsolutePath().replace('\\', '/');
			// if (!derbyBase.exists()) {
			url += ";create=true";
			// }
			derbyBase.getParentFile().mkdirs();
			this.name = "";
			this.password = "";
			this.config();
		}
	}

	public DSNInfo(Settings props, String dsnName) {
		int p = dsnName.lastIndexOf('/');
		if (p >= 0)
			name = dsnName.substring(p + 1);
		else
			name = dsnName;
		String prefix = "dsn." + name;

		ConfigurationKey key;

		key = new ConfigurationKey(prefix + ".url", META1);
		url = props.getExistingProperty(key);
		// System.out.println("Loading Connexion to " + dsnName +
		// " using : addr:" + url);
		key = new ConfigurationKey(prefix + ".username", META3);
		username = props.getExistingProperty(key);

		key = new ConfigurationKey(prefix + ".password", META2);
		password = props.getExistingProperty(key);
		ddl_generation = props.getProperty(prefix + ".ddl-generation", "none");

		option = props.getProperty(prefix + ".option", "").toLowerCase();
		int x;
		x = props.getIntProperty(prefix + ".maxIdle", 0);
		if (x != 0)
			maxIdle = Integer.valueOf(x);
		x = props.getIntProperty(prefix + ".maxActive", 0);
		if (x != 0)
			maxActive = Integer.valueOf(x);
		this.config();
		driverClassName = props.getProperty(prefix + ".driver", driverClassName);
	}

	private void config() {
		if (this.driverClassName == null) {

			if (url.startsWith("jdbc:derby:")) {
				this.driverClassName = "org.apache.derby.jdbc.EmbeddedDriver";
				serverType = ServerType.derby;
			} else if (url.startsWith("jdbc:sqlite:")) {
				this.driverClassName = "org.sqlite.JDBC";
				// this.driverClassName = "SQLite.JDBCDriver";
				serverType = ServerType.sqlite;
			} else if (url.startsWith("jdbc:jtds:sqlserver")) {
				this.driverClassName = "net.sourceforge.jtds.jdbc.Driver";
				serverType = ServerType.mssql;
			} else if (url.startsWith("jdbc:sqlserver:") || url.startsWith("jdbc:microsoft:")) {
				this.driverClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
				serverType = ServerType.mssql;
			} else if (url.startsWith("jdbc:postgresql:")) {
				this.driverClassName = "org.postgresql.Driver";
				serverType = ServerType.postgray;
			} else if (url.startsWith("jdbc:hsqldb:")) {
				this.driverClassName = "org.hsqldb.jdbcDriver";
				serverType = ServerType.hsqldb;
			} else if (url.startsWith("jdbc:odbc:")) {
				this.driverClassName = "sun.jdbc.odbc.JdbcOdbcDriver";
				serverType = ServerType.odbc;
			} else {
				driverClassName = "com.mysql.jdbc.Driver";
				serverType = ServerType.mysql;
			}
		}
		try {
			java.sql.Driver drv = (java.sql.Driver) Class.forName(driverClassName).newInstance();
			version = drv.getMajorVersion() + "." + drv.getMinorVersion();
		} catch (Exception e) {
			version = "0.0";
		}
	}

	DataSource cache_ds = null;

	/**
	 * see http://www.djvoo.net/d/C3P0%20manual
	 */
	public DataSource getAsDataSource() {
		if (cache_ds != null)
			return cache_ds;
		createDataSource();
		return cache_ds;
	}

	private synchronized void createDataSource() {
		if (cache_ds != null)
			return;
		ComboPooledDataSource ds;
		ds = new ComboPooledDataSource();
		ds.setUser(username);
		ds.setPassword(password);
		try {
			ds.setDriverClass(driverClassName);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error loading driver " + driverClassName, e);
		}
		ds.setJdbcUrl(getFullUrl());
		ds.setMinPoolSize(3);
		ds.setAcquireIncrement(2);
		ds.setMaxPoolSize(5);
		// DataSources.pooledDataSource(unpooledDataSource)

		// org.apache.commons.dbcp.BasicDataSource ds;
		// ds = new org.apache.commons.dbcp.BasicDataSource();
		// ds.setUsername(username);
		// ds.setPassword(password);
		// ds.setDriverClassName(driverClassName);
		// ds.setUrl(getFullUrl());
		// if (maxIdle != null)
		// ds.setMaxIdle(maxIdle.intValue());
		// if (maxActive != null)
		// ds.setMaxActive(maxActive.intValue());
		cache_ds = ds;
	}

	public SmartDataSource getAsSmartDataSource() {
		SingleConnectionDataSource ds = new SingleConnectionDataSource();
		ds.setSuppressClose(false);
		ds.setUsername(username);
		ds.setPassword(password);
		ds.setDriverClassName(driverClassName);
		// ds.setConnectionProperties(getConnectionProperties());
		ds.setUrl(getFullUrl());
		return ds;
	}

	/**
	 * see org.eclipse.persistence.config.PersistenceUnitProperties
	 */
	public Map<String, String> getAsTopLinkProperties() {
		Map<String, String> jpa_settings = new HashMap<String, String>();
		// eclipselink.session-name

		// eclipselink.jdbc.user is deprecated, property
		// javax.persistence.jdbc.user

		jpa_settings.put("datanucleus.storeManagerType", "rdbms");

		jpa_settings.put("toplink.jdbc.user", username);
		jpa_settings.put("javax.persistence.jdbc.user", username);
		// jpa_settings.put("eclipselink.jdbc.user", username);
		jpa_settings.put("hibernate.connection.username", username);
		jpa_settings.put("openjpa.ConnectionUserName", username);

		jpa_settings.put("toplink.jdbc.password", password);
		jpa_settings.put("javax.persistence.jdbc.password", password);
		// jpa_settings.put("eclipselink.jdbc.password", password);
		jpa_settings.put("hibernate.connection.password", password);
		jpa_settings.put("openjpa.ConnectionPassword", password);

		jpa_settings.put("toplink.jdbc.driver", driverClassName);
		jpa_settings.put("javax.persistence.jdbc.driver", driverClassName);
		// jpa_settings.put("eclipselink.jdbc.driver", driverClassName);
		jpa_settings.put("hibernate.connection.driver_class", driverClassName);
		jpa_settings.put("openjpa.ConnectionDriverName", driverClassName);

		if (ddl_generation.length() > 0) {
			jpa_settings.put("toplink.ddl-generation", ddl_generation);
			// jpa_settings.put("eclipselink.ddl-generation", ddl_generation);
			jpa_settings.put("hibernate.hbm2ddl.auto", ddl_generation);
			jpa_settings.put("javax.persistence.ddl-generation", ddl_generation);

		}
		switch (this.serverType) {
		case derby:
			jpa_settings.put("target-database", "Derby");
			jpa_settings.put("toplink.target-database", "Derby");
			jpa_settings.put("eclipselink.target-database", "Derby");
			break;
		case mysql:
			jpa_settings.put("target-database", "MySQL4");
			jpa_settings.put("toplink.target-database", "MySQL4");
			jpa_settings.put("eclipselink.target-database", "MySQL4");

			jpa_settings.put("toplink.jdbc.autoReconnect", "true");
			jpa_settings.put("eclipselink.jdbc.autoReconnect", "true");
			break;
		case mssql:
			jpa_settings.put("target-database", "SQLServer");
			jpa_settings.put("toplink.target-database", "SQLServer");
			jpa_settings.put("eclipselink.target-database", "SQLServer");
			break;
		case sqlite:
			jpa_settings.put("target-database", "Auto");
			jpa_settings.put("toplink.target-database", "Auto");
			jpa_settings.put("eclipselink.target-database", "Auto");
			break;
		default:
			break;
		}

		// toplink_db_settings.put
		// jdbc:mysql://SERVER/databasename
		// "jdbc:mysql://" + db_host + "/" + db_name
		String fullUrl = getFullUrl();
		jpa_settings.put("jdbc.url", fullUrl);
		jpa_settings.put("toplink.jdbc.url", fullUrl);
		// jpa_settings.put("eclipselink.jdbc.url", fullUrl);
		jpa_settings.put("javax.persistence.jdbc.url", fullUrl);
		jpa_settings.put("hibernate.connection.url", fullUrl);
		jpa_settings.put("openjpa.ConnectionURL", fullUrl);

		jpa_settings.put("eclipselink.validate-existence", "false");

		jpa_settings.put("eclipselink.weaving", "static");
		// eclipseLink
		// implements org.eclipse.persistence.internal.weaving.
		// PersistenceWeaved, PersistenceEntity, PersistenceObject,
		// FetchGroupTracker, PersistenceWeavedFetchGroups, ChangeTracker,
		// PersistenceWeavedChangeTracking

		// JDO
		// implements javax.jdo.spi.*;
		// Detachable, PersistenceCapable

		if (JPA_LOG_FILE != null) {
			System.err.println("enable JPA LOG in file:" + JPA_LOG_FILE);
			jpa_settings.put("eclipselink.logging.level", "FINE");
			jpa_settings.put("eclipselink.logging.file", JPA_LOG_FILE);
			jpa_settings.put("openjpa.Log", "DefaultLevel=WARN, Tool=INFO");
		}
		// jpa_settings.put("toplink.weaving", "true");
		return jpa_settings;
	}

	public ServerType getServerType() {
		return serverType;
	}
}
