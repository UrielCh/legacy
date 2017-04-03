package net.minidev.csv.parser;

public class ParserQuoted implements Parser {
	char sep;
	char quote;
	boolean allowEscape;
	/**
	 * 
	 * @param sep separator char
	 * @param quote quote char
	 * @param allowEscape allow the backslash usage.
	 */
	public ParserQuoted(char sep, char quote, boolean allowEscape) {
		this.sep = sep;
		this.quote = quote;
		this.allowEscape = allowEscape;
	}

	/**
	 * Support: "M":"tot\"ot":"55" "M":"totot":55
	 */
	public int count(String str) {
		byte state = 0; // init
		char c0 = 0;
		int nbBlock = 1;
		for (char c : str.toCharArray()) {
			if (state == 0) {
				if (c == quote) {
					state = 1; // inQuote
				} else if (c == sep) {
					// push
					nbBlock++;
				} else {
					// append
				}
			} else if (state == 1) { // in Quote
				if (c == quote) {
					if (allowEscape && c0 == '\\') {
						; // fix + append
					} else {
						state = 0; // inQuote
					}
				} else {
					// append
				}
			}
			c0 = c;
		}
		return nbBlock;
	}

	public String[] split(String line) {
		int c = count(line);
		String[] out = new String[c];
		return splitTo(line, out);
	}

	public String[] splitTo(String str, String[] dest) {
		byte state = 0; // init
		char c0 = 0;
		int nbBlock = 0;
		StringBuilder sb = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (state == 0) {
				if (c == quote) {
					state = 1; // inQuote
				} else if (c == sep) {
					dest[nbBlock++] = sb.toString();
					sb = new StringBuilder();
				} else {
					sb.append(c);
				}
			} else if (state == 1) { // in Quote
				if (c == quote) {
					if (allowEscape && c0 == '\\') {
						sb.deleteCharAt(sb.length() - 1);
						sb.append(c);
					} else {
						state = 0; // inQuote
					}
				} else {
					sb.append(c);
				}
			}
			c0 = c;
		}
		dest[nbBlock++] = sb.toString();
		while (nbBlock < dest.length)
			dest[nbBlock++] = null;
		return dest;
	}
	@Override
	public int getPriority() {
		return 5;
	}
	
	@Override
	public String toString() {
		return "ParserQuoted(S:" + sep + ",Q:" + quote + ",allowEscape:" + allowEscape + ")";
	}
}
