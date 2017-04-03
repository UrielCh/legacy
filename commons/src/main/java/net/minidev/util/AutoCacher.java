package net.minidev.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minidev.util.CachedMapper.CacheType;

/**
 * Buffered data getter
 * 
 * automaticaly get value from cache and from external datasource
 * 
 * @author Uriel Chemouni
 *
 * @param <K> Key
 * @param <V> Value
 */
public class AutoCacher<K, V> {
	private CachedMapper<K, V> cache;
	private Loader<K, V> loader;

	public void clear() {
		cache.clear();
	}
	
	public AutoCacher(CacheType type, long time, int size, Loader<K, V> loader) {
		cache = new CachedMapper<K, V>(type, time, size);
		this.loader = loader;
	}

	public V getFromCache(K key) {
		return cache.get(key);
	}

	public void pushValuesToCache(Map<K, V> map) {
		for (Map.Entry<K, V> ent : map.entrySet()) {
			V value = ent.getValue();
			cache.put(ent.getKey(), value);
		}
	}

	public void pushValueToCache(K key, V value) {
		cache.put(key, value);
	}
	
	/**
	 * get Data from Cache and missing data from datasource. 
	 */
	public List<V> getCachedList(Iterable<K> keys, Object context) {
		ArrayList<K> toPreload = new ArrayList<K>();
		ArrayList<V> result = new ArrayList<V>();

		for (K key : keys) {
			V m = cache.get(key);
			if (m != null)
				result.add(m);
			else
				toPreload.add(key);
		}
		Map<K, V> map = loader.load(toPreload, context);
		pushValuesToCache(map);
		result.addAll(map.values());
		return result;
	}

	public Map<K, V> getCachedMap(Iterable<K> keys, Object context) {
		HashMap<K, V> result = new HashMap<K, V>();
		ArrayList<K> toPreload = new ArrayList<K>();

		for (K key : keys) {
			V m = cache.get(key);
			if (m != null)
				result.put(key, m);
			else
				toPreload.add(key);
		}
		Map<K, V> map = loader.load(toPreload, context);
		if (map.size() > 0) {
			pushValuesToCache(map);
			result.putAll(map);
		}
		return result;
	}

	public static interface Loader<K, V> {
		public Map<K, V> load(Iterable<K> keys, Object context);
	}
}
