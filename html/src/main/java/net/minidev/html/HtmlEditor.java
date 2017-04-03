package net.minidev.html;

import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlEditor {
	public String html;
	public String headerSrc;
	public String headerDst;
	public int insert1;
	public int insert2;

	public HtmlEditor(String html) {
		this.html = html;
		Pattern p1 = Pattern.compile("<head( [^>]*)?>", Pattern.CASE_INSENSITIVE);
		Pattern p2 = Pattern.compile("</head( [^>]*)?>", Pattern.CASE_INSENSITIVE);
		Matcher m1 = p1.matcher(html);
		Matcher m2 = p2.matcher(html);
		if (!m1.find() || !m2.find()) {
			headerSrc = null;
			headerDst = null;
			return;
		}
		insert1 = m1.start(0);
		insert2 = m2.start(0);
		headerSrc = html.substring(insert1, insert2);
		headerDst = headerSrc;
	}

	public static String New_Line = System.getProperty("line.separator");

	static TreeMap<String, Pattern> patternCache1 = new TreeMap<String, Pattern>();

	public static Pattern getPatternMeta1(String name) {
		Pattern pat = patternCache1.get(name);
		if (pat != null)
			return pat;
		String name2 = Pattern.quote(name);
		pat = Pattern.compile("<meta\\s+name=[\"']" + name2 + "[\"']\\s*content=[\"']([^'\"]*)[\"']\\s*/?>", Pattern.CASE_INSENSITIVE);
		patternCache1.put(name, pat);
		return pat;
	}

	static TreeMap<String, Pattern> patternCache2 = new TreeMap<String, Pattern>();

	public static Pattern getPatternMeta2(String name) {
		Pattern pat = patternCache2.get(name);
		if (pat != null)
			return pat;
		String name2 = Pattern.quote(name);
		pat = Pattern.compile("<meta\\s+content=[\"']([\"]*)[\"']\\s*name=[\"']" + name2 + "[^'\"']\\s*/?>", Pattern.CASE_INSENSITIVE);
		patternCache2.put(name, pat);
		return pat;
	}

	public boolean setMetaValue(String tagName, String[] keys, String... values) {
		if (headerDst == null)
			return false;// not supported
		if (keys.length == 0)
			throw new NullPointerException("No keys provided !");

		StringBuilder sb = new StringBuilder();
		sb.append("<").append(tagName);
		for (String value : keys) {
			if (value.length() > 0)
				sb.append(" ");
			sb.append(value);
		}
		for (String value : values) {
			if (value.length() > 0)
				sb.append(" ");
			sb.append(value);
		}
		sb.append("/>");
		String fullMeta = sb.toString();
		if (headerDst.contains(fullMeta))
			return false;

		Pattern pat = Pattern.compile("<" + tagName + "[^>]+/?>", Pattern.CASE_INSENSITIVE);
		Matcher m = pat.matcher(headerDst);
		String old = null;
		while (m.find()) {
			String balise = m.group(0);
			Boolean valide = false;
			for (String s : keys) {
				if (!balise.contains(s))
					valide = false;
			}
			if (valide) {
				if (old != null)
					throw new NullPointerException("More than one head match " + fullMeta);
				old = balise;
			}
		}
		if (old != null) {
			if (values.length == 0) {
				headerDst = headerDst.replace(old, "");
				return true;
			}
			if (old.equals(fullMeta))
				return false;
			headerDst = headerDst.replace(old, fullMeta);
			return true;
		}
		// name=\"").append(tagName).append("\" content=\"").append(content).append("\"/>");
		StringBuffer sb2 = new StringBuffer();
		sb2.append(headerDst);
		sb2.append(fullMeta);
		sb2.append(New_Line);
		headerDst = sb2.toString();
		return true;
	}

	public boolean changes() {
		if (headerSrc == null)
			return false; // not supported
		if (headerDst.length() == 0)
			return false;
		if (headerSrc.equals(headerDst))
			return false;
		return true;
	}

	public String getFixed() {
		return html.substring(0, insert1) + headerDst + html.substring(insert2);
	}
}
