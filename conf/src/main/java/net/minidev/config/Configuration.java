package net.minidev.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

/**
 * LinkedHashMap
 * 
 * @author uriel
 * 
 */
public class Configuration {
	File src;
	List<Line> main;
	TreeMap<File, List<Line>> files = new TreeMap<File, List<Line>>();
	String charset = null;

	Hashtable<String, LinkedList<Line>> allData = new Hashtable<String, LinkedList<Line>>();
	Hashtable<String, LinkedList<Line>> disableData = new Hashtable<String, LinkedList<Line>>();

	Hashtable<String, List<String>> cacheList = new Hashtable<String, List<String>>();
	Hashtable<String, JSONObject> cacheJSonObj = new Hashtable<String, JSONObject>();
	Hashtable<String, JSONArray> cacheJSonArray = new Hashtable<String, JSONArray>();

	Hashtable<String, LinkedList<Line>> tmpAllData = null;
	Hashtable<String, LinkedList<Line>> tmpDisableData = null;
	private HashSet<File> processed = null;

	public Object getProperty(String name) {
		LinkedList<Line> l = allData.get(name);
		if (l == null)
			return null;
		if (l.size() > 1)
			return getList(name);
		return l.getLast().getValue();
	}

	public String getString(String name) {
		LinkedList<Line> l = allData.get(name);
		if (l == null)
			return null;
		return l.getLast().getValue();
	}

	public Iterator<String> getKeys() {
		return allData.keySet().iterator();
	}

	public Configuration clone() {
		Configuration ret = new Configuration();
		ret.src = src;
		ret.main = new ArrayList<Configuration.Line>(main);
		ret.files = new TreeMap<File, List<Line>>(files);
		ret.charset = charset;
		ret.allData = new Hashtable<String, LinkedList<Line>>(allData);
		ret.disableData = new Hashtable<String, LinkedList<Line>>(disableData);
		return ret;
	}

	public Iterator<String> getKeys(String prefix) {
		ArrayList<String> keys = new ArrayList<String>();
		for (String key : allData.keySet())
			if (key.startsWith(prefix))
				keys.add(key);
		return keys.iterator();
	}

	public void addProperty(String key, String value) {
		Line last = main.get(main.size() - 1);
		Line line = new Line(src, last.num + 1, key + " = " + value);
		addData(allData, key, line);
		resetCache();
	}

	public List<String> getList(String name) {
		return getList(name, ",");
	}

	public List<String> getList(String name, String separators) {
		List<String> value = cacheList.get(name);
		if (value != null)
			return value;
		List<String> result = new ArrayList<String>();

		LinkedList<Line> list = allData.get(name);
		if (list == null)
			return result;
		for (Line line : list) {
			String[] split = line.getValue().split("\\s*[" + separators + "]\\s*");
			for (String s : split)
				result.add(s);
		}
		cacheList.put(name, result);
		return result;
	}

	public JSONObject getJSonObject(String name) {
		JSONObject value = cacheJSonObj.get(name);
		if (value != null)
			return value;
		JSONObject result = new JSONObject();

		LinkedList<Line> list = allData.get(name);
		if (list == null)
			return result;

		for (Line line : list) {
			String text = line.getValue();
			Object obj = null;
			try {
				obj = JSONValue.parseWithException(text);
			} catch (ParseException e) {
				throw new RuntimeException("Can Not parse JSon Block:\"" + text + "\"\nin " + line.getReference()
						+ "\n" + e);
			}
			result.merge(obj);
		}
		cacheJSonObj.put(name, result);
		return result;
	}

	public JSONArray getJSonArray(String name) {
		JSONArray value = cacheJSonArray.get(name);
		if (value != null)
			return (JSONArray) value;
		JSONArray result = new JSONArray();

		LinkedList<Line> list = allData.get(name);
		if (list == null)
			return result;

		for (Line line : list) {
			String text = line.getValue();
			Object obj = JSONValue.parse(text);
			result.merge(obj);
		}
		cacheJSonArray.put(name, result);
		return result;
	}

	static class Line {
		File src;
		int num;
		int eqPos;
		String data;
		String trimed;

		String name;
		String value;

		public Line(File src, int num, String line) {
			this.src = src;
			this.num = num;
			this.data = line;
			this.trimed = data.trim();
			this.eqPos = trimed.indexOf("=");
		}

		public String getReference() {
			return src + ":" + num;
		}

		boolean isInclude() {
			if ("include".equals(getName()))
				return true;
			if (eqPos >= 0) {
				return false;
			}
			if (trimed.toLowerCase().startsWith("include"))
				return true;
			return false;
		}

		String getName() {
			if (name != null)
				return name;
			if (trimed.startsWith("#"))
				return null;
			if (eqPos == -1)
				return null;
			name = trimed.substring(0, eqPos).trim();
			return name;
		}

		String getValue() {
			if (value != null)
				return value;
			if (trimed.startsWith("#"))
				return null;
			if (eqPos == -1)
				return null;
			value = trimed.substring(eqPos + 1, trimed.length()).trim();
			return value;
		}

		public String getInclude() {
			if (!isInclude())
				return null;
			if (eqPos == -1) {
				String value = trimed.substring(7);
				return value.trim();
			} else {
				return getValue();
			}
		}

		@Override
		public String toString() {
			return data;
		}
	}

	public Configuration() {
		main = new ArrayList<Line>(0);
		try {
			digestData();
		} catch (Exception e) {
		}
	}

	public Configuration(File file, String charset) throws FileNotFoundException, IOException {
		this.src = file;
		this.charset = charset;
		List<Line> lines = loadStream(file, charset);
		main = lines;
		files.put(file, lines);
		reloadDeps(file, lines);
		digestData();
	}

	public void addFile(File file) throws FileNotFoundException, IOException {
		List<Line> lines = loadStream(file, charset);
		main.addAll(lines);
		files.put(file, lines);
		// digestData(file, lines);
		digestData();
	}

	public Configuration(Reader reader) throws IOException {
		main = loadStream(null, reader);
		digestData();
	}

	private List<Line> loadStream(File file, String charset) throws IOException {
		Reader reader = new InputStreamReader(new FileInputStream(file), charset);
		return loadStream(file, reader);
	}

	private List<Line> loadStream(File src, Reader reader) throws IOException {
		LineNumberReader lnr = new LineNumberReader(reader);
		String text = null;
		List<Line> lines = new ArrayList<Line>();
		while ((text = lnr.readLine()) != null) {
			int num = lnr.getLineNumber();
			lines.add(new Line(src, num, text));
		}
		lnr.close();
		return lines;
	}

	/**
	 * Load Recurcively a conf file.
	 * 
	 * @param file
	 *            to be load
	 * @return
	 */
	private void reloadDeps(File parent, List<Line> lines) throws IOException {
		for (Line line : lines) {
			if (line.isInclude()) {
				File file = new File(parent.getParentFile(), line.getInclude());
				if (!files.containsKey(file)) {
					List<Line> l2 = loadStream(file, charset);
					files.put(file, l2);
					reloadDeps(file, l2);
				}
			}
		}
	}

	private synchronized void digestData() throws IOException {
		processed = new HashSet<File>();
		tmpAllData = new Hashtable<String, LinkedList<Line>>();
		tmpDisableData = new Hashtable<String, LinkedList<Line>>();
		digestData(src, main);
		allData = tmpAllData;
		disableData = tmpDisableData;
		tmpAllData = tmpDisableData = null;
		processed = null;
		resetCache();
	}

	private void resetCache() {
		cacheList = new Hashtable<String, List<String>>();
		cacheJSonObj = new Hashtable<String, JSONObject>();
		cacheJSonArray = new Hashtable<String, JSONArray>();
	}

	private synchronized void digestData(File parent, List<Line> lines) throws IOException {
		for (Line line : lines) {
			if (line.isInclude()) {
				File file = new File(parent.getParentFile(), line.getInclude());
				if (!processed.contains(file)) {
					List<Line> l2 = files.get(file);
					digestData(file, l2);
				}
			} else {
				String name = line.getName();
				String value = line.getValue();
				if (name != null && value != null) {
					if (name.startsWith(";"))
						addData(tmpDisableData, name.substring(1), line);
					else
						addData(tmpAllData, name, line);
				}
			}
		}
	}

	void addData(Hashtable<String, LinkedList<Line>> bags, String name, Line line) {
		LinkedList<Line> bag = bags.get(name);
		if (bag == null) {
			bag = new LinkedList<Line>();
			bags.put(name, bag);
		}
		bag.add(line);
	}
}
