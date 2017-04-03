package net.minidev.csv.parser;

public class ParserFixedLenBinary implements ParserBinary {
	int[] blockSize;
	int total;

	public ParserFixedLenBinary(int... blockSize) {
		this.blockSize = blockSize;
		this.total = 0;
		for (int s : blockSize)
			this.total += s;
	}
	
	public int lineLen() {
		return total;
	}
	
	public int count(byte[] str) {
		if (this.total >= str.length)
			throw new IndexOutOfBoundsException("To Short String expecting " + this.total + " for line:");
		return blockSize.length;
	}

	public byte[][] split(byte[] str) {
		byte[][] out = new byte[blockSize.length][];
		int c = 0;
		for (int size : blockSize) {
			out[c++] = new byte[size];
		}
		return splitTo(str, out);
	}

	public byte[][] splitTo(byte[] str, byte[][] out) {
		if (this.total > str.length)
			throw new IndexOutOfBoundsException("To Short String expecting " + this.total + " for lineLen:" +  str.length);
		int p = 0;
		int cel = 0;
		for (int s : blockSize) {
			System.arraycopy(str, p, out[cel++], 0, s);
			p += s;
		}
		return out;
	}
}
