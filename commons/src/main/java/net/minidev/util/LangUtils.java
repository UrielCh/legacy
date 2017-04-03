package net.minidev.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.lang.reflect.Array;

/**
 * extracted from TSADOM Project.
 * 
 * Useful java methods
 * 
 * @author Uriel Chemouni
 */
public class LangUtils {
	public static int[] realloc(int[] data, int newSize) {
		if (newSize == data.length)
			return data;
		int[] result = new int[newSize];
		System.arraycopy(data, 0, result, 0, newSize < data.length ? newSize : data.length);
		return result;
	}

	public static byte[] realloc(byte[] data, int newSize) {
		if (newSize == data.length)
			return data;
		byte[] result = new byte[newSize];
		System.arraycopy(data, 0, result, 0, newSize < data.length ? newSize : data.length);
		return result;
	}

	public static <T> T[] realloc(T[] data, int newSize) {
		if (newSize == data.length)
			return data;
		T[] result = allocLike(data, newSize);
		System.arraycopy(data, 0, result, 0, newSize < data.length ? newSize : data.length);
		return result;
	}

	public static <T> T[] subArray(T[] data, int from, int size) {
		if (size == data.length && from == 0)
			return data;
		if (size + from > data.length)
			throw new ArrayIndexOutOfBoundsException();
		T[] result = allocLike(data, size);
		System.arraycopy(data, from, result, 0, size);
		return result;
	}

	public static int[] subArray(int[] data, int from, int size) {
		if (size == data.length && from == 0)
			return data;
		if (size + from > data.length)
			throw new ArrayIndexOutOfBoundsException();
		int[] result = (int[]) Array.newInstance(data.getClass().getComponentType(), size);
		System.arraycopy(data, from, result, 0, size);
		return result;
	}

	public static <T> T[] merge(T[] data1, T data2) {
		T[] result = allocLike(data1, data1.length + 1);
		System.arraycopy(data1, 0, result, 0, data1.length);
		result[data1.length] = data2;
		return result;
	}

	public static <T> T[] merge(T[] data1, T[] data2) {
		if (data2 == null || data2.length == 0)
			return data1;
		if (data1 == null || data1.length == 0)
			return data2;
		T[] result = allocLike(data1, data1.length + data2.length);
		System.arraycopy(data1, 0, result, 0, data1.length);
		System.arraycopy(data2, 0, result, data1.length, data2.length);
		return result;
	}

	public static <T> T[] merge(T[] data1, T[] data2, T[] data3) {
		int len = 0;
		if (data1 != null)
			len += data1.length;
		if (data2 != null)
			len += data2.length;
		if (data3 != null)
			len += data3.length;

		T[] result = allocLike(data1, len);
		int p = 0;

		if (data1 != null) {
			System.arraycopy(data1, 0, result, p, data1.length);
			p += data1.length;
		}

		if (data2 != null) {
			System.arraycopy(data2, 0, result, p, data2.length);
			p += data2.length;
		}

		if (data3 != null) {
			System.arraycopy(data3, 0, result, p, data3.length);
			p += data3.length;
		}
		return result;
	}

	public static <T> T[] merge(T[] data1, Collection<T> data2) {
		if (data2 == null || data2.size() == 0)
			return data1;
		if (data1 == null || data1.length == 0)
			return data2.toArray(data1);
		final int len = data2.size();
		T[] result = allocLike(data1, data1.length + len);
		System.arraycopy(data1, 0, result, 0, data1.length);
		Iterator<T> iter = data2.iterator();
		for (int i = 0; i < len; i++) {
			if (iter.hasNext())
				result[i + data1.length] = iter.next();
			else
				throw new ConcurrentModificationException();
		}
		return result;
	}

	public static <T> T[] dropNull(T[] data) {
		if (data == null || data.length == 0)
			return data;
		int size = data.length;
		for (T element : data)
			if (element == null)
				size--;
		if (size == data.length)
			return data;
		T[] result = allocLike(data, size);
		for (int j = 0, i = 0; i < data.length; i++)
			if (data[i] != null)
				result[j++] = data[i];
		return result;
	}

	public static <T> List<T[]> split(T[] data, int newSize) {
		int nbArrays = data.length / newSize + (data.length % newSize > 0 ? 1 : 0);
		ArrayList<T[]> result = new ArrayList<T[]>();
		for (int i = 0; i < nbArrays; i++) {
			int length = Math.min(newSize, data.length - i * newSize);
			T[] tmp = allocLike(data, length);
			System.arraycopy(data, i * newSize, tmp, 0, length);
			result.add(tmp);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] alloc(Class<T> type, int size) {
		return (T[]) Array.newInstance(type, size);
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] allocLike(T[] data, int size) {
		return (T[]) Array.newInstance(data.getClass().getComponentType(), size);
	}

	public static <T> T[] clone(T[] data) {
		T[] result = allocLike(data, data.length);
		System.arraycopy(data, 0, result, 0, data.length);
		return result;
	}

	public static boolean[] clone(boolean[] data) {
		boolean[] result = new boolean[data.length];
		System.arraycopy(data, 0, result, 0, data.length);
		return result;
	}

	public static int IPToInt(String IP) {
		String ips[] = IP.split("\\.");

		int intIP = 0;
		try {
			for (int i = 3; i >= 0; i--) {
				intIP = intIP << 8;
				intIP |= Integer.parseInt(ips[i]);
			}
		} catch (Exception e) {
			System.err.println("Can not parse IP:" + IP);
		}
		return (intIP);
	}

	public static String IPToString(int intIP) {
		StringBuilder sb = new StringBuilder(15);
		try {
			for (int i = 3; i >= 0; i--) {
				if (i != 3)
					sb.append('.');
				sb.append((int) (intIP & 0xFF));
				intIP = intIP >>> 8;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (sb.toString());
	}

	/**
	 * @deprecated
	 */
	public static void shutdown(ExecutorService es, int show) {
		LocalUtils.shutdown(es, show);
	}
}
