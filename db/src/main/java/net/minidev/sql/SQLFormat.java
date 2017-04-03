package net.minidev.sql;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

public abstract class SQLFormat {
	public final static MysqlFormat mysql = new MysqlFormat();
	public final static HSQLDBFormat hsqldb = new HSQLDBFormat();

	public final static SimpleDateFormat MySQL_Date = new SimpleDateFormat("yyyy.MM.dd");
	public final static SimpleDateFormat MySQL_DateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	protected static char[] hexa = "0123456789ABCDEF".toCharArray();
	protected static HashSet<Class<?>> appendable = new HashSet<Class<?>>(20);
	static {
		appendable.add(Integer.class);
		appendable.add(Integer.TYPE);
		appendable.add(Boolean.class);
		appendable.add(Boolean.TYPE);
		appendable.add(Short.class);
		appendable.add(Short.TYPE);
		appendable.add(Float.class);
		appendable.add(Float.TYPE);
		appendable.add(Double.class);
		appendable.add(Double.TYPE);
		appendable.add(Byte.class);
		appendable.add(Byte.TYPE);
		appendable.add(Date.class);
		appendable.add(String.class);
		appendable.add(java.sql.Date.class);
	}

	public abstract StringBuilder appendDateTime(StringBuilder sb, Date date);

	public abstract StringBuilder appendDate(StringBuilder sb, Date date);

	public abstract StringBuilder append(StringBuilder sb, String value);

	public StringBuilder appendStrings(StringBuilder sb, Iterable<String> values) {
		int c = 0;
		for (String s : values) {
			if (c++ > 0)
				sb.append(',');
			append(sb, s);
		}
		return sb;
	}

	public StringBuilder appendNumbers(StringBuilder sb, int... values) {
		int c = 0;
		for (int s : values) {
			if (c++ > 0)
				sb.append(',');
			sb.append(s);
		}
		return sb;
	}

	public StringBuilder appendNumbers(StringBuilder sb, Iterable<? extends Number> values) {
		int c = 0;
		for (Number s : values) {
			if (c++ > 0)
				sb.append(',');
			sb.append(s);
		}
		return sb;
	}

	public StringBuilder append(StringBuilder sb, Object value) {
		if (value == null)
			return sb.append("null");
		if (value instanceof String)
			return append(sb, (String) value);
		if (value instanceof Integer)
			return sb.append(((Integer) value).intValue());
		if (value instanceof Long)
			return sb.append(((Long) value).longValue());
		if (value instanceof Number)
			return sb.append(value.toString());
		if (value instanceof Date)
			return appendDateTime(sb, (Date) value);
		if (value instanceof Enum)
			return append(sb, value.toString());
		if (value instanceof Boolean) {
			if (((Boolean) value))
				return sb.append("1");
			else
				return sb.append("0");
		}
		if (value instanceof byte[])
			return append(sb, (byte[]) value);
		if (value.getClass().isArray()) {
			int l = Array.getLength(value);
			for (int c = 0; c < l; c++) {
				if (c > 0)
					sb.append(',');
				append(sb, Array.get(value, c));
			}
			return sb;
		}
		throw new RuntimeException("non Supported Type:" + value.getClass());
	}

	public abstract StringBuilder append(StringBuilder sb, byte[] data);

	public boolean isFormatable(Object o) {
		if (o == null)
			return true;
		Class<?> cls = o.getClass();
		if (cls.isEnum())
			return true;
		return appendable.contains(cls);
	}
}
