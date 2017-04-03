package net.minidev.csv.parser;

public class ParserSimple implements Parser {
	char sep;

	public ParserSimple(char sep) {
		this.sep = sep;
	}

	public int count(String str) {
		int count = 1;
		int p = str.indexOf(sep);
		while (p >= 0) {
			p = str.indexOf(sep, p + 1);
			count++;
		}
		return count;
	}

	public String[] split(String str) {
		int count = count(str);
		String[] out = new String[count];
		return splitTo(str, out);
	}

	public String[] splitTo(String str, String[] dest) {
		int p = 0;
		int nbBlock = 0;
		while (true) {
			int p2 = str.indexOf(sep, p);
			if (p2 >= 0)
				dest[nbBlock++] = str.substring(p, p2);
			else {
				dest[nbBlock++] = str.substring(p);
				break;
			}
			p = p2 + 1;
		}
		while (nbBlock < dest.length)
			dest[nbBlock++] = null;
		return dest;
	}

	@Override
	public int getPriority() {
		return 2;
	}

	@Override
	public String toString() {
		return "ParserSimple(" + sep + ")";
	}
}
