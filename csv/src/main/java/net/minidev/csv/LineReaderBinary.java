package net.minidev.csv;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.minidev.csv.parser.ParserBinary;

public class LineReaderBinary implements Iterable<byte[]> {
	private File file;
	private LineIterator iterator;
	private ParserBinary parser;

	public LineReaderBinary(File file, ParserBinary parser) {
		this.file = file;
		this.parser = parser;
	}

	@Override
	public Iterator<byte[]> iterator() {
		if (iterator != null)
			iterator.close();
		FileInputStream reader = null;
		try {
			reader = new FileInputStream(file);
		} catch (Exception e) {
			throw new RuntimeException("File Access File");
		}
		iterator = new LineIterator(reader, parser.lineLen(), 0);
		return iterator;
	}

	public void close() {
		if (iterator != null) {
			iterator.close();
			iterator = null;
		}
	}

	static class LineIterator implements Iterator<byte[]> {
		/** A flag indicating if the iterator has been fully read. */
		private boolean done = false;
		private FileInputStream in;
		/** Buffer input Stream Reading Buffer **/
		private byte[] line;
		/** The current line. */
		private byte[] cachedLine;

		private int interleave;

		public LineIterator(FileInputStream in, int size, int interleave) {
			this.in = in;
			line = new byte[size];
			cachedLine = null;
			this.interleave = interleave;
		}

		@Override
		public boolean hasNext() {
			if (cachedLine != null)
				return true;
			if (done)
				return false;
			int size = 0;
			try {
				size = in.read(line);
				if (interleave > 0)
					in.skip(interleave);
			} catch (Exception e) {
			}
			if (size == line.length)
				cachedLine = line;
			else
				close();
			return !done;
		}

		@Override
		public byte[] next() {
			if (cachedLine == null) {
				if (!done)
					hasNext();
				if (cachedLine == null)
					throw new NoSuchElementException();
			}
			byte[] ret = cachedLine;
			cachedLine = null;
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean isDone() {
			return done;
		}

		public void close() {
			this.done = true;
			this.cachedLine = null;
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
				in = null;
			}
		}
	}
}
