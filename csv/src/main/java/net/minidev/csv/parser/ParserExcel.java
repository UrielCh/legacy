package net.minidev.csv.parser;

public class ParserExcel implements Parser {
	char sep;
	char quote = '"';

	/**
	 * 
	 * @param sep
	 *            separator char
	 * @param quote
	 *            quote char
	 */
	public ParserExcel(char sep, char quote) {
		this.sep = sep;
		this.quote = quote;
	}

	/**
	 * Support: "M":"tot\"ot":"55" "M":"totot":55
	 */
	public int count(String line) {
		byte state = 0;
		int nbBlock = 1;
		for (char c : line.toCharArray()) {
			if (state == 0) { // debut
				if (c == quote) {
					state = 1; // inQuote
				} else if (c == sep) { // fin block
					nbBlock++;
				} else {
				}
			} else if (state == 1) { // in Quote
				if (c == quote) {
					state = 2;
				} else {
				}
			} else if (state == 2) { // in Quote
				if (c == quote) {
					state = 1;
				} else if (c == sep) {
					nbBlock++;
					state = 0;
				} else {
					state = 1;
				}

			}
		}
		return nbBlock;
	}

	public String[] split(String line) {
		int c = count(line);
		String[] out = new String[c];
		return splitTo(line, out);
	}

	public String[] splitTo(String string, String[] dest) {
		byte state = 0;
		int nbBlock = 0;
		StringBuilder sb = new StringBuilder();

		for (char c : string.toCharArray()) {
			if (state == 0) { // debut
				if (c == quote) {
					state = 1; // inQuote
				} else if (c == sep) { // fin block
					if (nbBlock < dest.length)
						dest[nbBlock++] = sb.toString();
					sb = new StringBuilder();
				} else {
					sb.append(c);
				}
			} else if (state == 1) { // in Quote
				if (c == quote) {
					state = 2;
				} else {
					sb.append(c);
				}
			} else if (state == 2) { // in Quote 2
				if (c == quote) {
					state = 1;
					sb.append(c);
				} else if (c == sep) {
					if (nbBlock < dest.length)
						dest[nbBlock++] = sb.toString();
					sb = new StringBuilder();
					state = 0;
				} else {
					sb.append(c);
					state = 1;
				}
			}
		}
		if (nbBlock < dest.length)
			dest[nbBlock++] = sb.toString();
		while (nbBlock < dest.length)
			dest[nbBlock++] = null;
		return dest;
	}

	@Override
	public int getPriority() {
		return 10;
	}
	
	@Override
	public String toString() {
		return "ParserExcel(S:" + sep + ",Q:" + quote + ")";
	}
}
