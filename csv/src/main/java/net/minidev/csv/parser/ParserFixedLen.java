package net.minidev.csv.parser;

import java.util.Arrays;

public class ParserFixedLen implements Parser {
	int[] blockSize;
	int total;

	public ParserFixedLen(int... blockSize) {
		this.blockSize = blockSize;
		this.total = 0;
		for (int s : blockSize)
			this.total += s;
	}

	public int count(String str) {
		if (this.total >= str.length())
			throw new IndexOutOfBoundsException("To Short String expecting " + this.total + " for line:");
		return blockSize.length;
	}

	public String[] split(String str) {
		String[] out = new String[blockSize.length];
		return splitTo(str, out);
	}

	public String[] splitTo(String str, String[] dest) {
		if (this.total >= str.length())
			throw new IndexOutOfBoundsException("To Short String expecting " + this.total + " for line:");
		int p = 0;
		int nbBlock = 0;
		for (int s : blockSize) {
			dest[nbBlock++] = str.substring(p, s);
			p += s;
		}
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
		return "ParserFixedLen(" + Arrays.toString(blockSize) + ")";
	}

}
