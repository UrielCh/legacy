package net.minidev.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class EditableHash {
	final File src;
	public TreeMap<String, String> data;

	public EditableHash(File src) throws IOException {
		this.src = src;
		if (!src.exists())
			src.createNewFile();
		data = new TreeMap<String, String>();
		addToMap(src, data);
	}
	
	public static void addToMap(File file, Map<String, String>map) {
		if (file.isDirectory())
			return;
		for (String l : new LineIterable(file)) {
			int p = l.indexOf("\t");
			if (p == -1)
				p = l.indexOf(" ");
			if (p == -1)
				p = l.length();
			String key = l.substring(0,p);
			String value = l.substring(p).trim();
			if (key.length() == 0)
				continue;
			map.put(key, value);
		}
	}

	
	public boolean contains(String key) {
		return data.containsKey(key);
	}

	public boolean containsKey(String key) {
		return data.containsKey(key);
	}

	public Set<String> keySet() {
		return data.keySet();
	}

	public String get(String key) {
		return data.get(key);
	}

	public int size() {
		return data.size();
	}
	public synchronized int add(Iterable<String> keys) {
		int c = 0;
		try {
			FileOutputStream fos = new FileOutputStream(src, true);
			for (String key : keys) {
				if (key.length() == 0)
					continue;
				if (contains(key))
					continue;
				String txt = key + "\t\r\n";
				fos.write(txt.getBytes());
				data.put(key, "");
				c++;
			}
			fos.close();
		} catch (Exception e) {
		}
		return c;
	}

	public synchronized int addAll(Map<?,?> values) {
		int c = 0;
		try {
			FileOutputStream fos = new FileOutputStream(src, true);
			for (Object keyO : values.keySet()) {
				String key = keyO.toString(); 
				if (key.length() == 0)
					continue;
				if (contains(key))
					continue;
				String txt = key + "\t" + values.get(keyO) + "\r\n";
				fos.write(txt.getBytes());
				data.put(key, "");
				c++;
			}
			fos.close();
		} catch (Exception e) {
		}
		return c;
	}

	public synchronized boolean add(String key, String value) {
		try {
			if (contains(key))
				return false;
			// System.out.println("ignore UID : " + UID);
			FileOutputStream fos = new FileOutputStream(src, true);
			String txt = key + "\t" + value + "\r\n";
			fos.write(txt.getBytes());
			fos.close();
			data.put(key, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
