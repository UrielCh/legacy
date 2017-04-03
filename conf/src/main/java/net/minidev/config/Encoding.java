package net.minidev.config;

import java.nio.charset.Charset;

public class Encoding {
	public final static Charset DEFAULT = Charset.forName("UTF-8");
	public final static Charset UTF8 = Charset.forName("UTF-8");
	public final static Charset UTF16 = Charset.forName("UTF-16");	

	public final static Charset UTF16LE = Charset.forName("UnicodeLittleUnmarked");
	public final static Charset UTF16BE = Charset.forName("UnicodeBigUnmarked");

	public final static Charset LATIN1 = Charset.forName("ISO-8859-1");
	public final static Charset LATIN15 = Charset.forName("ISO-8859-15");
}
