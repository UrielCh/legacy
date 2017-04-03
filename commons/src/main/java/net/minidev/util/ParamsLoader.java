package net.minidev.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import net.minidev.fomat.DateUtils;

/**
 * Simple command line args parser
 * 
 * @author Uriel Chemouni
 * 
 */
public class ParamsLoader {
	Hashtable<String, Properties> paramsClient = new Hashtable<String, Properties>();
	Properties properties;

	public ParamsLoader() {
		properties = new Properties();
	}

	public ParamsLoader(String[] args) {
		properties = new Properties();

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			String next = null;
			if (i < args.length - 1)
				next = args[i + 1];
			if (arg.equals("--"))
				break;

			if (!arg.startsWith("-")) {
				System.err.println("can do anny think with arg:" + arg);
				continue;
			}
			arg = arg.substring(1);
			if (arg.startsWith("-"))
				arg = arg.substring(1);

			int equal = arg.indexOf("=");
			if (equal == -1) {
				// pas de eq
				if (next != null && !next.startsWith("-")) {
					properties.put(arg.toLowerCase(), next);
					i++;
					continue;
				}
				properties.put(arg.toLowerCase(), "");
				continue;
			}
			String value = arg.substring(equal + 1);
			String key = arg.substring(0, equal).toLowerCase();
			properties.put(key, value);
		}
	}

	/**
	 * @param <E>
	 * 
	 * @param param
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public void subLoadConf(String param) throws IOException {
		String confFile = get(param);
		if (confFile == null)
			return;
		parseConfFile(new File(confFile));
	}

	public void parseConfFile(File file) throws IOException {
		Properties props = new Properties();
		Reader reader = new FileReader(file);
		props.load(reader);
		reader.close();

		for (Object k : props.keySet()) {
			properties.put(k.toString().toLowerCase(), props.get(k));
		}
	}

	public void dispProperties() {
		properties.list(System.out);
	}

	public boolean contains(String... keys) {
		for (String key : keys)
			if (properties.containsKey(key))
				return true;
		return false;
	}

	private String getProp(String key) {
		return properties.getProperty(key.toLowerCase());
	}

	/**
	 * Get params from Object Properties
	 * 
	 * @param keys
	 * @return
	 */
	public String get(String... keys) {
		for (String key : keys) {
			String value = getProp(key);
			if (value != null)
				return value;
		}
		return null;
	}

	public String[] getList(String... keys) {
		String values = get(keys);
		if (values == null)
			return new String[0];
		return values.split("[ ,;]+");
	}

	/**
	 * use the last param as default value
	 */
	public String getDefault(String... keys) {
		if (keys.length < 2)
			throw new IllegalArgumentException("invalid parameter count.");
		for (int i = 0; i < keys.length - 1; i++) {
			String value = getProp(keys[i]);
			if (value != null)
				return value;
		}
		return keys[keys.length - 1];
	}

	public Date getDateFr(String... keys) throws ParseException {
		for (String key : keys) {
			String value = getProp(key);
			if (value != null) {
				try {
					return DateUtils.parseDateFR4Digit(value);
				} catch (NumberFormatException e) {
					throw new RuntimeException("Can not parse " + value + " as int value for param " + key);
				}
			}
		}
		return null;
	}

	public Integer getInt(String... keys) {
		for (String key : keys) {
			String value = getProp(key);
			if (value != null) {
				try {
					return Integer.valueOf(value);
				} catch (NumberFormatException e) {
					throw new RuntimeException("Can not parse " + value + " as int value for param " + key);
				}
			}
		}
		return null;
	}

	public Boolean getBoolean(String... keys) {
		for (String key : keys) {
			String value = getProp(key);
			if (value != null) {
				if ("true".equals(value))
					return true;
				if ("1".equals(value))
					return true;
				if ("0".equals(value))
					return false;
				if ("false".equals(value))
					return false;
			}
		}
		return null;
	}

	/**
	 * Get params from Object Properties Params needed
	 * 
	 * @param keys
	 * @return
	 */
	public String getExisting(String... keys) {
		String value = get(keys);
		if (value == null)
			throw new RuntimeException("Missing param: " + Arrays.toString(keys));
		return value;
	}

	/**
	 * Get params in Enum format from Object Properties
	 * 
	 * @param <E>
	 * @param cls
	 * @param keys
	 * @return
	 */
	public <E extends Enum<E>> E getEnumProperty(Class<E> cls, String... keys) {
		String value = get(keys);
		E tmp = null;

		if (value != null)
			try {
				tmp = Enum.valueOf(cls, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		if (tmp == null)
			throw new RuntimeException(
					"Invalid Enum value:" + value + " for keys:" + Arrays.toString(keys) + " Authorized Value:" + Arrays.toString(cls.getEnumConstants()));
		return tmp;
	}

}
