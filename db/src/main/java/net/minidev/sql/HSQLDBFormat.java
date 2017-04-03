package net.minidev.sql;

import java.util.Date;

public class HSQLDBFormat extends SQLFormat {

	@Override
	public StringBuilder appendDateTime(StringBuilder sb, Date date) {
		throw new RuntimeException("not implemened yet");
	}

	@Override
	public StringBuilder appendDate(StringBuilder sb, Date date) {
		throw new RuntimeException("not implemened yet");
	}

	@Override
	public StringBuilder append(StringBuilder sb, String value) {
		sb.append('\'');
		int l = value.length();
		for (int i = 0; i < l; i++) {
			char c = value.charAt(i);
			if (c == '\'')
				sb.append('\'');
			sb.append(c);
		}
		sb.append('\'');
		return null;
	}

	@Override
	public StringBuilder append(StringBuilder sb, byte[] data) {
		throw new RuntimeException("not implemened yet");
	}

}
