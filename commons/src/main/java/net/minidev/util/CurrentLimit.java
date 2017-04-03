package net.minidev.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.Comparator;
import java.util.TreeMap;

/**
 * a simple serialisable/deserialisation counter classe
 * 
 * @author Uriel Chemouni
 *
 */
public class CurrentLimit {
	int base = 10;
	TreeMap<String, Integer> data = new TreeMap<String, Integer>(new StrComp());
	File file;

	static class StrComp implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			return o1.compareToIgnoreCase(o2);
		}		
	}
	
	public CurrentLimit(File f) {
		load(f);
	}
	
	public void load(File f) {
		this.data = new TreeMap<String, Integer>(new StrComp());
		this.file = f;
		//LineNumberReader lnr = null;
		try {
			if (!f.exists())
				if (!f.createNewFile())
                    throw new RuntimeException("Can Not create File f:" + f);
			LineNumberReader lnr = new LineNumberReader(new FileReader(file));
			String line;
			while ((line = lnr.readLine()) != null) {
				String split[] = line.split(" +");
				if (split.length != 2)
					continue;
				String name = split[0];
				int id = Integer.parseInt(split[1], 10);
				data.put(name, id);
			}
			lnr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * serialise the object to disk
	 */
	public void dump() {
		try {
			FileWriter fw = new FileWriter(file);
			for (String name : data.keySet()) {
				int id = data.get(name);
				// .toUpperCase()
				fw.append(name).append(' ').append(Integer.toString(id, 10));
				fw.append("\r\n");
			}
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Integer getCurrent(String client) {
		Integer id = data.get(client);
		if (id == null) {
			data.put(client, 0);
			return 0;
		}
		return id;
	}
	
	public void remove(String client) {
		data.remove(client);
	}

	public Integer updateCurrent(String client, Integer id) {
		return data.put(client, id);
	}
}
