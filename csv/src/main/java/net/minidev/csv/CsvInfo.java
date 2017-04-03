package net.minidev.csv;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minidev.csv.parser.Parser;
import net.minidev.csv.parser.ParserDumy;
import net.minidev.csv.parser.ParserExcel;
import net.minidev.csv.parser.ParserQuoted;
import net.minidev.csv.parser.ParserSimple;
import net.minidev.util.Counter;
import net.minidev.util.Counter.Value;
import net.minidev.util.EncodingUtils;
import net.minidev.util.LangUtils;
import net.minidev.util.LineIterable;

public class CsvInfo implements Iterable<String[]> {
	static Logger LOG = Logger.getLogger(CsvInfo.class.getName());
	public char[] KNOW_SEP = ",:;|\t".toCharArray();
	public static int MAX_TEST_LINE = Integer.MAX_VALUE;
	/**
	 * Used separator
	 */
	public char separator;
	public char quote;

	public int fieldCount;
	public int maxFieldCount;
	public int ligneCount;
	public Parser parser;

	private File file;
	private URL url;
	private CvsIterator iter;
	private boolean haveHeader = false;
	private String enc = "UTF-8";
	public static boolean STRICT_MODE = true;
	// return empty ligne instead of insecure data.
	public boolean DROP_INEXACT_LINE = true;

	private String[] firstLine = null;

	public String[] getFirstLine() {
		return firstLine;
	}

	public void setFirstLine(String[] firstLine) {
		this.firstLine = firstLine;
	}

	private static HashSet<String> commonHeader = new HashSet<String>();
	static {
		for (String s : CsvUtil.commonHeader)
			commonHeader.add(s.toLowerCase());
	}

	public boolean hasHeaderLine() {
		return haveHeader;
	}

	public CsvInfo() {
		separator = 0;
		quote = 0;
		fieldCount = 0;
		ligneCount = 0;
	}

	@Override
	public Iterator<String[]> iterator() {
		if (iter != null) {
			iter.close();
		}
		iter = new CvsIterator(this);
		return iter;
	}

	public File getFile() {
		return file;
	}

	public void close() {
		if (iter != null) {
			iter.close();
			iter = null;
		}
	}

	public static class CvsIterator implements Iterator<String[]> {
		CsvInfo parent;
		LineIterable lines;
		Iterator<String> iter;
		String[] buffer;
		private final static String[] ERROR = new String[0];

		CvsIterator(CsvInfo parent) {
			this.parent = parent;

			if (parent.file != null)
				this.lines = new LineIterable(parent.file, parent.enc);
			else
				this.lines = new LineIterable(parent.url, parent.enc);

			this.iter = this.lines.iterator();
			// this.buffer = new String[parent.fieldCount];
			this.buffer = new String[parent.maxFieldCount];
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public String[] next() {
			String str = iter.next();
			if (parent.DROP_INEXACT_LINE) {
				int count = parent.parser.count(str);
				if (count != parent.fieldCount)
					return ERROR;
			}
			return parent.parser.splitTo(str, buffer);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void close() {
			lines.close();
		}
	}

	/**
	 * parse the file as an UTF-8/16/32 file
	 */
	public CsvInfo(File file) throws IOException {
		this(file, "AUTO");
	}

	public CsvInfo(URL file) throws IOException {
		this(file, "AUTO");
	}

	/**
	 * parse the file using the given Encoding
	 */
	public CsvInfo(File file, String enc) throws IOException {
		this();
		this.file = file;
		this.enc = enc;
		checkEnc();
		LineIterable lines = new LineIterable(file, this.enc);
		this.autoDetect(lines);
		lines.close();
	}

	private void checkEnc() throws IOException {
		if (enc == null || "AUTO".equals(enc) || "UTF".equals(enc)) {
			RandomAccessFile ra = new RandomAccessFile(file, "r");
			byte[] magic = new byte[4096];
			int len = ra.read(magic);
			ra.close();

			if (len > 0) {
				magic = LangUtils.realloc(magic, len);
				enc = EncodingUtils.detectEncondingByBom(magic);
			} else
				enc = "UTF-8";
			if (enc == null)
				enc = EncodingUtils.detectEncondingByAligne(magic);
			if (enc == null)
				enc = "UTF-8";
		}
	}

	/**
	 * parse the file using the given Encoding
	 */
	public CsvInfo(URL url, String enc) throws IOException {
		this();
		this.url = url;
		if (enc == null || "AUTO".equals(enc) || "UTF".equals(enc)) {
			InputStream in = url.openStream();
			byte[] magic = new byte[4096];
			int len = in.read(magic);
			in.close();

			if (len > 0) {
				magic = LangUtils.realloc(magic, len);
				enc = EncodingUtils.detectEncondingByBom(magic);
			} else
				enc = "UTF-8";
			if (enc == null)
				enc = EncodingUtils.detectEncondingByAligne(magic);
			if (enc == null)
				enc = "UTF-8";
		}
		this.enc = enc;
		LineIterable lines = new LineIterable(url, enc);
		this.autoDetect(lines);
		lines.close();
	}

	public CsvInfo(File file, Parser parser) throws IOException {
		this();
		this.file = file;
		this.parser = parser;
	}

	public final static class ParserConf implements Comparable<ParserConf> {
		int sep;
		int quote;
		int escape;

		public ParserConf(int sep, int quote, int escape) {
			this.sep = sep;
			this.quote = quote;
			this.escape = escape;
		}

		public int toInt() {
			return sep * 255 * 255 + quote * 255 + escape;
		}

		/**
		 * trie dans l'ordre croissant
		 */
		@Override
		public int compareTo(ParserConf o) {
			return toInt() - o.toInt();
		}
	}

	/**
	 * return an set of parsser from a line
	 * 
	 * @param line
	 * @return
	 */
	private List<Parser> suggestParser(String line) {
		ArrayList<Parser> out = new ArrayList<Parser>(6);
		Counter<ParserConf> parseConfs = new Counter<ParserConf>();

		short index[] = indexChars(line);
		for (char c : KNOW_SEP) {
			if (index[c] == 0)
				continue;
			parseConfs.add(new ParserConf(c, 0, 0), index[c]);
		}
		/**
		 * for each separator add all parser
		 */
		for (Value<ParserConf> p : parseConfs.getValues()) {
			char sep = (char) p.getKey().sep;
			out.add(new ParserSimple(sep));
			// add quoted parser only if the line contains somes
			// if (index['"'] > 0) {
			out.add(new ParserExcel(sep, '"'));
			out.add(new ParserQuoted(sep, '"', false));
			// }
		}
		return out;
	}

	private short[] indexChars(String line) {
		short[] result = new short[128];
		for (char c : line.toCharArray()) {
			if (c >= result.length || c < 0)
				continue;
			result[c]++;
		}
		return result;
	}

	/**
	 * Autodetect CSV format type (separator, and escape sequence)
	 * 
	 * @param lines
	 */
	private void autoDetect(Iterable<String> lines) {
		List<Parser> parsers = null;
		Hashtable<Parser, Counter<Integer>> result = null;
		String firstLineRaw = null;

		int lc = 0;
		for (String line : lines) {
			lc++;
			if (result == null) {
				firstLineRaw = line;
				result = new Hashtable<Parser, Counter<Integer>>();
				parsers = suggestParser(line);
				for (Parser p : parsers) {
					result.put(p, new Counter<Integer>());
				}
			}

			for (Parser p : parsers) {
				int c = p.count(line);
				result.get(p).add(c);
			}
			if (lc >= MAX_TEST_LINE)
				break;
		}

		int maxCorrect = 0;
		ArrayList<Parser> topParser = new ArrayList<Parser>();
		if (parsers != null) {
			for (Parser p : parsers) {
				Counter<Integer> counter = result.get(p);
				Counter.Value<Integer> top = counter.getTopValue();

				int val = top.getKey();
				int count = top.getCount();
				if (LOG.isLoggable(Level.FINER))
					LOG.fine(p + " val:" + val + " oc " + count + " / " + lc);

				if (counter.countDistinct() == 1 || !STRICT_MODE) {
					// Select the parsers having the max maching peek lines.
					if (maxCorrect < count) {
						this.fieldCount = val;
						int max = 0;
						// Compute max field in line to avoid future OutOfBound
						// Exception
						for (Integer nbField : counter.getKeys()) {
							if (max < nbField)
								max = nbField;
						}
						this.maxFieldCount = max;
						topParser = new ArrayList<Parser>();
						maxCorrect = count;
					}
					// add to best parser selection
					if (maxCorrect == count) {
						topParser.add(p);
					}
				}
			}
		}

		if (topParser.size() > 0) {
			// if more than one parser have the same value
			// choose the one having the higher priority
			Collections.sort(topParser, new Comparator<Parser>() {
				@Override
				public int compare(Parser o1, Parser o2) {
					return o2.getPriority() - o1.getPriority();
				}
			});
			// take the highest priority
			this.parser = topParser.get(0);
			// debug
			if (LOG.isLoggable(Level.FINER))
				for (Parser p : topParser) {
					LOG.fine("Top:" + p.getPriority() + " " + p);
				}
		}

		if (parser != null) {
			Counter<Integer> counter = result.get(parser);
			Counter.Value<Integer> top = counter.getTopValue();
			int count = top.getCount();
			int errors = lc - count;
			if (errors > 0) {
				if (LOG.isLoggable(Level.WARNING))
					LOG.warning("Imperfect Match: on " + lc + " lines, " + errors + " lines are invalide. " + this.file);
			}
		}

		if (parser == null) {
			this.parser = new ParserDumy('"');
			this.fieldCount = 1;
			this.maxFieldCount = 1;
		}

		/**
		 * Get FirstLine
		 */
		if (firstLineRaw != null) {
			int knowFieldCount = 0;
			firstLine = this.parser.split(firstLineRaw);
			for (String s : firstLine)
				if (commonHeader.contains(s.trim().toLowerCase()))
					knowFieldCount++;
			if (knowFieldCount > 0) {
				this.haveHeader = true;
				// if (this.fieldCount < firstLine.length)
				// this.fieldCount = firstLine.length;
			}
		}
		if (haveHeader)
			lc--;
		ligneCount = lc;
	}
}
