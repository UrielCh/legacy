package net.minidev.csv;

import java.io.File;
import java.util.Iterator;

import net.minidev.csv.parser.ParserBinary;

public class BinaryInfo implements Iterable<byte[][]> {
	public int fieldCount;
	public ParserBinary parser;
	private File file;
	private CvsIterator iter;
	
	public BinaryInfo(File file, ParserBinary parser) {
		this.file = file;
		this.parser = parser;
	}
	
	@Override
	public Iterator<byte[][]> iterator() {
		if (iter != null) {
			iter.close();
		}
		iter = new CvsIterator(this);
		return iter;
	}

	public void close() {
		if (iter != null) {
			iter.close();
			iter = null;
		}
	}

	public static class CvsIterator implements Iterator<byte[][]> {
		BinaryInfo parent;
		LineReaderBinary lines;
		Iterator<byte[]> iter;
		byte[][] buffer;

		CvsIterator(BinaryInfo parent) {
			this.parent = parent;
			this.lines = new LineReaderBinary(parent.file, parent.parser);
			this.iter = this.lines.iterator();
			this.buffer = null;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public byte[][] next() {
			byte[] str = iter.next();
			if (buffer == null)
				buffer = parent.parser.split(str);
			else
				parent.parser.splitTo(str, buffer);
			return buffer;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void close() {
			lines.close();
		}
	}
}
