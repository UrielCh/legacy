/**
 * 
 */
package net.minidev.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.util.CommonsCodex;

/**
 * @author Uriel
 */
public class Settings {
	static Logger logger = Logger.getLogger(Settings.class.getName());

	private Configuration props;
	private File propertiesFile_;
	private String encoding;

	public String toString() {
		return "ConfFile : " + propertiesFile_;
	}

	public Settings(File file) {
		this(file, "cp1252");
	}

	public Settings(File file, String encoding) {
		this.propertiesFile_ = file;
		this.encoding = encoding;
		if (file == null || !file.exists()) {
			props = new Configuration();
		} else {
			loadSettings(propertiesFile_);
		}
	}

	public Settings clone() {
		Settings ret = new Settings(null);
		ret.props = props.clone();
		ret.propertiesFile_ = propertiesFile_;
		ret.encoding = encoding;
		return ret;
	}

	private String getConfPath() {
		if (propertiesFile_ == null)
			return "memory";
		return propertiesFile_.getAbsolutePath();
	}

	public Iterator<String> getKeys(String prefix) {
		return props.getKeys(prefix);
	}

	public Iterator<String> getKeys() {
		return props.getKeys();
	}

	public void pushProperty(String key, String value) {
		this.props.addProperty(key, value);
		// this.props.put(key, value);
	}

	public void addConf(File file) throws IOException {
		props.addFile(file);
	}

	public File getPropertiesFile() {
		return propertiesFile_;
	}

	/**
	 * @see Properties#load(java.io.InputStream)
	 */
	private void loadSettings(File settingsfile) {
		if (!settingsfile.exists()) {
			logger.severe("Cannot find file :" + settingsfile);
			props = new Configuration();
			return;
		}
		try {
			props = new Configuration(settingsfile, encoding);
			// props = new PropertiesConfiguration();
			// props.setEncoding(encoding);
			// props.load(settingsfile);
		} catch (Exception e) {
			logger.severe("ConfigurationException operations on file :" + settingsfile + " " + e.getMessage());
		}
	}

	/**
	 * @see Properties#getProperty(String)
	 * @param key
	 * @return
	 */
	public String getProperty(CharSequence key) {
		return props.getString(key.toString());
	}

	public String getProperty(CharSequence key, String defaultvalue) {
		// Object oval = props.get(key);
		Object oval = props.getProperty(key.toString());
		String sval = (oval instanceof String) ? (String) oval : null;
		if (sval == null)
			return defaultvalue;
		return sval;
	}

	@SuppressWarnings("unchecked")
	public <E extends Enum<E>> E getEnumProperty(CharSequence key, E defaultValue) {
		String value = getProperty(key);
		if (value == null)
			return defaultValue;
		Class<E> cls = (Class<E>) defaultValue.getClass();
		E tmp = Enum.valueOf(cls, value);
		if (tmp == null)
			return defaultValue;
		return tmp;
	}

	public <E extends Enum<E>> E getEnumProperty(CharSequence key, Class<E> cls) {
		String value = getProperty(key);
		E tmp = null;
		if (value != null) {
			tmp = Enum.valueOf(cls, value);
		}
		if (tmp == null)
			throw new RuntimeException("Invalide Enum value:" + value + " for key:" + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key) + " Authorized Value:"
					+ Arrays.toString(cls.getEnumConstants()));
		return tmp;
	}

	public byte[] getBytes(String key, byte[] defaultvalue) {
		String value = getProperty(key);
		if (value == null)
			return defaultvalue;
		try {
			return CommonsCodex.decodeHex(value.toCharArray());
		} catch (Exception e) {
			throw new RuntimeException("Invalide byte[] value:" + value + " for key:" + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		}
	}

	public byte[] getBytes(String key) {
		String value = getProperty(key);
		if (key == null)
			throw new RuntimeException("Missing byte[] " + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		try {
			return CommonsCodex.decodeHex(value.toCharArray());
		} catch (Exception e) {
			throw new RuntimeException("Invalide byte[] value:" + value + " for key:" + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		}
	}

	public List<String> getList(String key) {
		List<String> ret = props.getList(key);
		return ret;
	}

	public List<String> getList(String key, String separators) {
		List<String> ret = props.getList(key, separators);
		return ret;
	}

	public List<String> getList(String key, List<String> defaultValue) {
		List<String> ret = props.getList(key);
		// , defaultValue
		if (ret == null || ret.size() == 0)
			return defaultValue;
		return ret;
	}

	public int getInt(String key, int defaultvalue) {
		int retValue = defaultvalue;
		try {
			String strDef = Integer.valueOf(defaultvalue).toString();
			String value = getProperty(key, strDef);
			retValue = toInt(key, value);
		} catch (NumberFormatException nfe) {
			logger.warning("Cannot convert property to int. Property key :" + key + ConfigurationKey.toErrorPrefix(key));
		}
		return retValue;
	}

	public boolean getBoolean(CharSequence key, boolean defaultvalue) {
		String value = getProperty(key, Boolean.toString(defaultvalue));
		if (value.equalsIgnoreCase("1"))
			return true;
		if (value.equalsIgnoreCase("true"))
			return true;
		if (value.equalsIgnoreCase("vrai"))
			return true;
		return false;
	}

	public JSONObject getJSonObject(CharSequence key) {
		return props.getJSonObject(key.toString());
	}

	public JSONArray getJSonArray(CharSequence key) {
		return props.getJSonArray(key.toString());
	}

	/**
	 * @see Properties#setProperty(String, String)
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		props.addProperty(key, value);
	}

	public String getExistingProperty(CharSequence key) {
		String value = getProperty(key);
		if (value == null) {
			throw new RuntimeException("Missing String " + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		}
		return value;
	}

	public int getIntProperty(CharSequence key, int defValue) {
		String value = getProperty(key);
		if (value == null)
			return defValue;
		return toInt(key, value);
	}

	public int getIntProperty(CharSequence key) {
		String value = getProperty(key);
		if (value == null)
			throw new RuntimeException("Missing " + key + " as int value in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		return toInt(key, value);
	}

	public Class<?> getClassProperty(CharSequence key, Class<?> defValue) {
		String value = getProperty(key);
		if (value == null)
			return defValue;
		return toClass(key, value);
	}

	public Class<?> getClassProperty(CharSequence key) {
		String value = getProperty(key);
		if (value == null)
			throw new RuntimeException("Missing " + key + " as Class value in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		return toClass(key, value);
	}

	public long getLongProperty(String key, long defValue) {
		String value = getProperty(key);
		if (value == null)
			return defValue;
		return toLong(key, value);
	}

	public long getLongProperty(CharSequence key) {
		String value = getProperty(key);
		if (value == null)
			throw new RuntimeException("Missing " + key + " as long in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		return toLong(key, value);
	}

	public DSNInfo getDsnInfo(String db_name) {
		int p = db_name.indexOf("/");
		String explicitBase = null;
		if (p > 0) {
			explicitBase = db_name.substring(p+1);
			db_name = db_name.substring(0, p);
		}
		DSNInfo info = new DSNInfo(this, db_name);
		if (explicitBase != null) {
			String url = info.url;
			p = url.indexOf('?');
			if (p == -1)
				p = url.length();
			int p0 = url.lastIndexOf('/', p);
			url = url.substring(0, p0+1) + explicitBase + url.substring(p);
			info.url = url;
			}
		return info;
	}

	public int[] getIntsProperty(CharSequence key) {
		String value = getProperty(key);
		if (value == null)
			throw new RuntimeException("Missing int[] value : " + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		return toInts(key, value);
	}

	private int[] toInts(CharSequence key, String value) {
		String[] tmp = value.split(" ");
		int[] ret = new int[tmp.length];
		try {
			for (int i = 0; i < tmp.length; i++)
				ret[i] = toInt(key, tmp[i]);
		} catch (Exception e) {
			throw new RuntimeException("Invalid int [] value : " + value + " for key : " + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		}
		return ret;
	}

	private Class<?> toClass(CharSequence key, String value) {
		Class<?> ret = null;
		try {
			ret = Class.forName(value);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("ClassNotFoundException for className : " + value + " for key : " + key + " in "
					+ getConfPath() + ConfigurationKey.toErrorPrefix(key));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage() + " for className : " + value + " for key : " + key + " in "
					+ getConfPath() + ConfigurationKey.toErrorPrefix(key));
		}
		return ret;
	}

	public boolean getBoolProperty(CharSequence key, boolean defValue) {
		String value = getProperty(key);
		if (value == null)
			return defValue;
		return toBool(key, value);
	}

	public boolean getBoolProperty(CharSequence key) {
		String value = getProperty(key);
		if (value == null)
			throw new RuntimeException("Missing " + key + " in " + getConfPath() + ConfigurationKey.toErrorPrefix(key));
		return toBool(key, value);
	}

	private boolean toBool(CharSequence key, String value) {
		value = value.toLowerCase();
		if ("true".equals(value) || ("on".equals(value)) || ("1".equals(value)))
			return true;
		if ("false".equals(value) || ("off".equals(value)) || ("0".equals(value)))
			return false;
		throw new RuntimeException("Invalid boolean value : " + value + " for key : " + key + " in " + getConfPath()
				+ ConfigurationKey.toErrorPrefix(key));
	}

	public File getFileProperty(CharSequence key) {
		String value = getProperty(key);
		if (value == null)
			throw new RuntimeException("Missing " + key + " in " + getConfPath() + ConfigurationKey.toErrorPrefix(key));
		return toFile(key, value);
	}

	private File toFile(CharSequence key, String value) {
		File file = new File(value);
		if (!file.exists())
			throw new RuntimeException("Invalid path value : " + value + " for key : " + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		return file;
	}

	private int toInt(CharSequence key, String value) {
		int ret = 0;
		try {
			value = value.toLowerCase();

			int multiplicator = 1;
			if (value.endsWith("k"))
				multiplicator = 1024;
			else if (value.endsWith("m"))
				multiplicator = 1024 * 1024;
			else if (value.endsWith("g")) {
				multiplicator = 1024 * 1024 * 1024;
			}
			if (multiplicator > 1)
				value = value.substring(0, value.length() - 1);
			ret = Integer.parseInt(value) * multiplicator;
		} catch (Exception e) {
			throw new RuntimeException("Invalid int value : " + value + " for key : " + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		}
		return ret;
	}

	private long toLong(CharSequence key, String value) {
		long ret = 0;
		try {
			value = value.toLowerCase();

			long multiplicator = 1;
			if (value.endsWith("k"))
				multiplicator = 1024L;
			else if (value.endsWith("m"))
				multiplicator = 1024L * 1024L;
			else if (value.endsWith("g"))
				multiplicator = 1024L * 1024L * 1024L;
			else if (value.endsWith("t"))
				multiplicator = 1024L * 1024L * 1024L * 1024L;
			else if (value.endsWith("p"))
				multiplicator = 1024L * 1024L * 1024L * 1024L * 1024L;
			if (multiplicator > 1)
				value = value.substring(0, value.length() - 1);
			ret = Long.parseLong(value) * multiplicator;
		} catch (Exception e) {
			throw new RuntimeException("Invalid long value : " + value + " for key : " + key + " in " + getConfPath()
					+ ConfigurationKey.toErrorPrefix(key));
		}
		return ret;
	}
}
