package net.minidev.csv.parser;

public class ParserDumy implements Parser {
	char quote;

	public ParserDumy(char quote) {
		this.quote = quote;
	}

	public int count(String str) {
		return 1;
	}

	public String[] split(String str) {
		String[] out = new String[1];
		return splitTo(str, out);
	}

	public String[] splitTo(String str, String[] out) {
		int len = str.length();
		if (len > 1) {
			if (str.charAt(0) == quote && str.charAt(len - 1) == quote)
				str = str.substring(1, len - 1);
		}
		out[0] = str;
		return out;
	}
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public String toString() {
		return "ParserDummy(" + quote + ")";
	}

}
