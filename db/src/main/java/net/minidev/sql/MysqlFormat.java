package net.minidev.sql;

import java.util.Date;

/**
 * @author Uriel Chemouni
 */
public class MysqlFormat extends SQLFormat {
	public StringBuilder appendDateTime(StringBuilder sb, Date date) {
		if (date == null)
			return sb.append("null");
		sb.append('"');
		sb.append(MySQL_DateTime.format(date));
		return sb.append('"');
	}

	public StringBuilder appendDate(StringBuilder sb, Date date) {
		if (date == null)
			return sb.append("null");
		sb.append('"');
		sb.append(MySQL_Date.format(date));
		return sb.append('"');
	}

	public StringBuilder append(StringBuilder sb, String value) {
		return append(sb, value, '"');
	}

	/**
	 * Methode de formatage de bynaire pour MySQL
	 * 
	 * Syntax x'4D7953514C' (Mysql 4.0 +)
	 * 
	 * Altervative: 0xAABBCC (Mysql ODBC)
	 */
	public StringBuilder append(StringBuilder sb, byte[] data) {
		if (data == null) {
			sb.append("null");
			return sb;
		}
		char[] escaped = new char[data.length * 2 + 3];
		escaped[0] = 'x';
		escaped[1] = '\'';
		escaped[escaped.length - 1] = '\'';
		for (int i = 0; i < data.length; i++) {
			byte c = data[i];
			int pos = 2 + i * 2;
			escaped[pos] = hexa[c >> 4 & 0xF];
			escaped[pos + 1] = hexa[c & 0xF];
		}
		return sb.append(escaped);
	}

	public StringBuilder append(StringBuilder sb, String value, char quote) {
		if (value == null)
			return sb.append("null");

		final int len = value.length();
		sb.append(quote);
		for (int i = 0; i < len; i++) {
			final char c = value.charAt(i);

			if (c >= 40 && c <= 91) // speed up
				sb.append(c);
			else if (c >= 93 && c <= 126) // speed up
				sb.append(c);
			else if (c == 0)
				sb.append("\\0");
			else if (c == '\'') {
				if (quote == c)
					sb.append("\\'");
				else
					sb.append("'"); // '
			} else if (c == '"') {
				if (quote == c)
					sb.append("\\\"");
				else
					sb.append("\""); // \"
			} else if (c == '\b')
				sb.append("\\b");

			else if (c == '\n')
				sb.append("\\n");

			else if (c == '\r')
				sb.append("\\r");

			else if (c == '\t')
				sb.append("\\t");

			else if (c == 26)
				sb.append("\\Z");

			else if (c == '\\')
				sb.append("\\\\");

			else if (c == 26)
				sb.append("\\Z");

			else if (c >= '0' && c < 0x20) {
				// drop non printable chars
				if (c == 0x09 || c == 0x0A || c == 0x0D)
					sb.append(c);
			} else
				sb.append(c);
		}
		return sb.append(quote);
	}
}
