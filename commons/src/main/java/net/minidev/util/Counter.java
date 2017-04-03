package net.minidev.util;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeMap;

/**
 * occurence counter
 * 
 * @author Uriel Chemouni
 *
 * @param <T>
 */
public class Counter<T extends Comparable<T>> {
	private TreeMap<T, Value<T>> map = new TreeMap<T, Value<T>>();
	int total = 0;

	public int add(T value) {
		return add(value, 1);
	}

	public int add(T value, int poid) {
		if (value == null)
			return 0;
		if (total != 0)
			total = 0;
		Value<T> v = map.get(value);
		if (v == null) {
			v = new Value<T>(value, poid);
			map.put(value, v);
		} else {
			v.inc(poid);
		}
		return v.getCount();
	}

	public Set<T> getKeys() {
		return map.keySet();
	}

	public int getCount(T key) {
		Value<T> i = map.get(key);
		if (i == null)
			return 0;
		else
			return i.getCount();
	}

	public int countDistinct() {
		return map.size();
	}

	public int count() {
		if (total != 0)
			return total;
		int c = 0;
		for (Value<T> i : map.values())
			c += i.getCount();
		total = c;
		return c;
	}

	public double getPct(T value) {
		int count = count();
		if (count == 0)
			return Double.NaN;
		Value<T> v = map.get(value);
		if (v == null)
			return 0;
		return ((double) v.getCount()) / ((double) count);
	}

	public Set<T> keys() {
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	public Value<T>[] getValues() {
		Value<T>[] out = new Value[map.size()];
		map.values().toArray(out);
		Arrays.sort(out);
		return out;
	}

	public Value<T> getTopValue() {
		int offCount = 0;
		Value<T> result = null;
		for (Value<T> v : map.values()) {
			if (v.getCount() > offCount) {
				offCount = v.getCount();
				result = v;
			}
		}
		return result;
	}

	public static class Value<T> implements Comparable<Value<T>> {
		private T key;
		private int count;

		public Value(T key, int count) {
			this.key = key;
			this.count = count;
		}

		public T getKey() {
			return key;
		}

		public int getCount() {
			return count;
		}

		private void inc(int poid) {
			this.count += poid;
		}

		public String toString() {
			return "Value:" + key + " occ:" + count;
		}

		@Override
		public int compareTo(Value<T> o) {
			return o.getCount() - this.getCount();
		}
	}
}
