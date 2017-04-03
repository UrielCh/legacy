package net.minidev.csv.parser;

public interface ParserBinary {
	public int lineLen();
	public int count(byte[] str);
	public byte[][] split(byte[] line);
	public byte[][] splitTo(byte[] str, byte[][] dest);	
}
