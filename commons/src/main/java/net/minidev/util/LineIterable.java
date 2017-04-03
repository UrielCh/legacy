package net.minidev.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;

public class LineIterable implements Iterable<String> {
	private String enc = "UTF-8";
	private File file;
	private URL url;
	private LineIterator iterator;
	private InputStream is;

	public LineIterable(File file) {
		this(file, "UTF-8");
	}

	public LineIterable(String filename) {
		this(filename, "UTF-8");
	}

	public LineIterable(URL url) {
		this(url, "UTF-8");
	}

	public LineIterable(String filename, String enc) {
		if (filename == null)
			throw new NullPointerException("filename should not be null");
		this.file = new File(filename);
		this.enc = enc;
	}

	public LineIterable(URL url, String enc) {
		if (url == null)
			throw new NullPointerException("filename should not be null");
		this.url = url;
		this.file = null;
		this.enc = enc;
	}

	public LineIterable(File file, String enc) {
		if (file == null)
			throw new NullPointerException("file should not be null");
		this.file = file;
		this.enc = enc;
	}

	@Override
	public LineIterator iterator() {
		if (iterator != null)
			iterator.close();
		Reader reader = null;
		InputStream stream;
		if (is == null) {
			try {
				if (file == null) {
					stream = url.openStream();
				} else {
					stream = new FileInputStream(file);
					// tester extention de fichier pour gzip...
					if (file.getName().endsWith(".gz"))
						stream = new GZIPInputStream(stream);
					// import lzma.sdk.lzma.Decoder;
					// import lzma.streams.LzmaInputStream;
					// if (file.getName().endsWith(".lzma"))
					// stream = new LzmaInputStream(stream, new Decoder());

				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unsuported Encoding:" + enc);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("File Not Found " + file.getAbsolutePath());
			} catch (IOException e) {
				throw new RuntimeException("IOException: " + e.getMessage(), e);
			}
		} else {
			stream = is;
		}
		try {
			reader = new InputStreamReader(stream, enc);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsuported Encoding:" + enc);
		}
		iterator = new LineIterator(new BufferedReader(reader));
		return iterator;
	}

	public Iterable<Integer> asIntegers() {
		return new ItemIterable<Integer>(this, new Converter<Integer>() {
			@Override
			Integer convert(String input) {
				return Integer.valueOf(input.trim());
			};
		});
	}

	public Iterable<Long> asLongs() {
		return new ItemIterable<Long>(this, new Converter<Long>() {
			@Override
			Long convert(String input) {
				return Long.valueOf(input.trim());
			};
		});
	}

	public void close() {
		if (iterator == null)
			return;
		iterator.close();
		iterator = null;
	}

	public static class ItemIterable<T> implements Iterable<T> {
		Converter<T> converter;
		LineIterable lineIterable;

		public ItemIterable(LineIterable lineIterable, Converter<T> converter) {
			this.converter = converter;
			this.lineIterable = lineIterable;
		}

		@Override
		public Iterator<T> iterator() {
			return new ItemIterator<T>(lineIterable.iterator(), converter);
		}
	}

	public abstract static class Converter<T> {
		abstract T convert(String input);
	}

	public static class ItemIterator<T> implements Iterator<T> {
		LineIterator source;
		Converter<T> converter;

		public ItemIterator(LineIterator source, Converter<T> converter) {
			this.source = source;
			this.converter = converter;
		}

		@Override
		public boolean hasNext() {
			return source.hasNext();
		}

		@Override
		public T next() {
			String next = source.next();
			return converter.convert(next);
		}

		@Override
		public void remove() {
			source.remove();
		}
	}

	public static class LineIterator implements Iterator<String> {
		private final BufferedReader bufferedReader;
		/** The current line. */
		private String cachedLine;
		/** A flag indicating if the iterator has been fully read. */
		private boolean finished = false;

		public LineIterator(final Reader reader) throws IllegalArgumentException {
			if (reader == null) {
				throw new IllegalArgumentException("Reader must not be null");
			}
			if (reader instanceof BufferedReader)
				bufferedReader = (BufferedReader) reader;
			else
				bufferedReader = new BufferedReader(reader);
		}

		public LineIterator(final BufferedReader reader) throws IllegalArgumentException {
			if (reader == null) {
				throw new IllegalArgumentException("Reader must not be null");
			}
			bufferedReader = reader;
		}

		public boolean hasNext() {
			if (cachedLine != null)
				return true;
			if (finished)
				return false;
			try {
				String line = bufferedReader.readLine();
				if (line == null) {
					finished = true;
					close();
					return false;
				}
				cachedLine = line;
				return true;
			} catch (IOException ioe) {
				close();
				throw new IllegalStateException(ioe);
			}
		}

		public String next() {
			return nextLine();
		}

		public String nextLine() {
			if (!hasNext())
				throw new NoSuchElementException("No more lines");
			String currentLine = cachedLine;
			cachedLine = null;
			return currentLine;
		}

		public void close() {
			finished = true;
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException ioe) {
			}
			cachedLine = null;
		}

		public void remove() {
			throw new UnsupportedOperationException("Remove unsupported on LineIterator");
		}
	}
}
