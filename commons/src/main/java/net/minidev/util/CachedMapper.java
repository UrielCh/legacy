package net.minidev.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A single or dual Way Cached mapping table
 * 
 * Datas may expire by timeout, or by a Max buffer Size
 * 
 * @author Uriel Chemouni
 * 
 * @param <K>
 *            Key
 * @param <V>
 *            Value
 */
public class CachedMapper<K, V> {
	protected final Queue<TimedData<K, V>> data;
	protected final HashMap<K, V> index1;
	protected final HashMap<V, K> index2;

	protected final long TTL;
	protected final int maxSize;

	private static CacheCleaner cleaner = new CacheCleaner();

	public static void destroy() {
		CacheCleaner cc = cleaner;
		if (cc != null)
			synchronized (cc.maps) {
				cc.maps.clear();
			}
	}

	public static enum CacheType {
		SINGLE, DUAL
	}

	/**
	 * create a CacheMapper
	 * 
	 * @param type SINGLE or DUAL
	 * @param time in millisec
	 */
	public CachedMapper(CacheType type, long time) {
		this(type, time, 10000);
	}

	/**
	 * create a CacheMapper
	 * 
	 * @param type SINGLE or DUAL
	 * @param time in millisec
	 * @param size cache capacity in object
	 */
	public CachedMapper(CacheType type, long time, int size) {
		synchronized (this.getClass()) {
			if (cleaner == null)
				cleaner = new CacheCleaner();
		}
		this.TTL = time;
		if (size < 1)
			throw new java.lang.IllegalArgumentException("Size must be > 0");
		this.maxSize = size;

		data = new LinkedBlockingQueue<TimedData<K, V>>();
		index1 = new HashMap<K, V>();
		if (type == CacheType.DUAL)
			index2 = new HashMap<V, K>();
		else
			index2 = null;

		cleaner.addMap(this);
	}

	public V get(K key) {
		return index1.get(key);
	}

	public K getRev(V value) {
		if (index2 != null)
			return index2.get(value);
		else
			throw new RuntimeException("to use CachedMapper Rev mapping, build ChachedMapper With CacheType.DUAL FLAG");
	}

	public void put(K key, V value) {
		TimedData<K, V> ent = new TimedData<K, V>(key, value);
		synchronized (data) {
			data.add(ent);
			index1.put(key, value);
			if (index2 != null)
				index2.put(value, key);
		}
	}

	public void remove(K key) {
		synchronized (data) {
			V value = index1.remove(key);
			if (index2 != null && value != null)
				index2.remove(value);
		}
	}

	public void clear() {
		synchronized (data) {
			data.clear();
			index1.clear();
			if (index2 != null)
				index2.clear();
		}
	}

	public static class TimedData<K, V> {
		public long time;
		public K key;
		public V value;

		public TimedData(K key, V value) {
			this.time = System.currentTimeMillis();
			this.key = key;
			this.value = value;
		}
	}

	static class CacheCleaner extends TimerTask {
		final LinkedList<WeakReference<CachedMapper<?, ?>>> maps;

		public CacheCleaner() {
			maps = new LinkedList<WeakReference<CachedMapper<?, ?>>>();
			StaticTimer.schedule(this, 3000, 3000);
		}

		public void addMap(CachedMapper<?, ?> map) {
			synchronized (maps) {
				maps.add(new WeakReference<CachedMapper<?, ?>>(map));
			}
		}

		private void pass() {
			synchronized (maps) {
				Iterator<WeakReference<CachedMapper<?, ?>>> iter;
				iter = maps.iterator();
				while (iter.hasNext()) {
					WeakReference<CachedMapper<?, ?>> ref = iter.next();
					CachedMapper<?, ?> map = ref.get();
					if (map == null) {
						iter.remove();
						continue;
					}
					long timeOut = System.currentTimeMillis() - map.TTL;
					TimedData<?, ?> entry;
					final Queue<?> data = map.data;
					synchronized (data) {
						// Filtrage par TTL
						entry = (TimedData<?, ?>) data.peek();
						while (entry != null && entry.time < timeOut) {
							TimedData<?, ?> ttData = (TimedData<?, ?>) data.poll();
							map.index1.remove(ttData.key);
							if (map.index2 != null)
								map.index2.remove(ttData.value);
							entry = (TimedData<?, ?>) data.peek();
						}

						// Filtrage par Max Size
						if (data.size() > map.maxSize) {
							int toRemove = data.size() - map.maxSize;
							while (toRemove-- > 0) {
								TimedData<?, ?> ttData = (TimedData<?, ?>) data.poll();
								map.index1.remove(ttData.key);
								if (map.index2 != null)
									map.index2.remove(ttData.value);
							}
						}
					}
				}
			}
		}

		public void run() {
			pass();
		}
	}
}
