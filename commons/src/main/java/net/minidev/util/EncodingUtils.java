package net.minidev.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

public class EncodingUtils {
	public static String detectEncondingByBom(byte[] data) {
		if (data == null)
			return null;
		if (data.length < 2)
			return null;

		if (data.length >= 4) {
			if (data[0] == -1 && data[1] == -2 && data[2] == 0 && data[3] == 0)
				return "UTF-32LE"; // ALias X-UTF-32LE
			if (data[0] == 0 && data[1] == 0 && data[2] == -2 && data[3] == -1)
				return "UTF-32BE"; // Alias X-UTF-32BE
		}

		if (data.length >= 3) {
			if ((data[0] == -17 && data[1] == -69 && data[2] == -65))
				return "UTF-8"; // Alias unicode-1-1-utf-8
		}

		if (data.length >= 2) {
			if (data[0] == -1 && data[1] == -2)
				return "UTF-16LE"; // Alias UnicodeLittleUnmarked, X-UTF-16LE
			if (data[0] == -2 && data[1] == -1)
				return "UTF-16BE"; // Alias X-UTF-16BE, ISO-10646-UCS-2,
									// UnicodeBigUnmarked
		}
		return null;
	}

	public static String detectEncondingByAligne(byte[] data) {
		int len = data.length;
		int len2 = len - 3;
		int p[] = new int[4];
		for (int i = 0; i < len2; i += 4) {
			if (data[i] == 0)
				p[0]++;
			if (data[i + 1] == 0)
				p[1]++;
			if (data[i + 2] == 0)
				p[2]++;
			if (data[i + 3] == 0)
				p[3]++;
		}
		final int QL = len / 4;
		if (QL <= 1)
			return null;
		if (p[0] == 0 && p[1] == 0 && p[2] == QL && p[3] == 0)
			return "UTF-32LE";
		else if (p[0] == 0 && p[1] == 0 && p[2] == 0 && p[3] == QL)
			return "UTF-32BE";
		else if (p[0] == 0 && p[1] == len / 4 && p[2] == 0 && p[3] == QL)
			return "UTF-16LE";
		else if (p[0] == QL && p[1] == 0 && p[2] == QL && p[3] == 0)
			return "UTF-16BE";
		return null;
	}

	private final static Charset ENC_LATIN1 = Charset.forName("ISO-8859-1");
	// private final static Charset ENC_LATIN15 =
	// Charset.forName("ISO-8859-15");
	private final static Charset ENC_UTF8 = Charset.forName("UTF-8");

	private static int countChar(String text, char ch) {
		int count = 0;
		int length = text.length();
		for (int i = 0; i < length; i++)
			if (text.charAt(i) == ch)
				count++;
		return count;
	}

	public static boolean haveInvalidUtf8(String text) {
		int length = text.length();
		for (int i = 0; i < length; i++)
			if (text.charAt(i) == (char) 65533)
				return true;
		return false;
	}

	public static String dropInvalidUtf8(String text) {
		if (text == null)
			return null;
		return text.replace("\uFFFD", "");
	}

	/**
	 * Correct Invalid Encodage
	 */
	public static String fixEncodage(final String text) {
		int bugLatin = 0;
		int bugUTF8 = countChar(text, (char) 65533);
		if (bugUTF8 > 0)
			// bug can not be Fixed.
			// Data Lost
			return text;
		TreeSet<String> errors = null;
		for (int i = 0; i < text.length() - 1; i++) {
			String key = text.substring(i, i + 2);
			if (!latinError.contains(key))
				continue;
			bugLatin++;
			if (errors == null) {
				errors = new TreeSet<String>();
			}
			errors.add(key.intern());
		}
		if (bugLatin == 0)
			return text;
		String fixed = readAsUTF8(text);
		bugUTF8 = countChar(fixed, (char) 65533);
		if (bugUTF8 == 0)
			return fixed;

		fixed = text;
		// try to Fix Error by Hand
		if (errors != null)
			for (String s : errors) {
				String sf = readAsUTF8(s);
				bugUTF8 = countChar(sf, (char) 65533);
				fixed = fixed.replace(s, sf);
			}
		bugUTF8 = countChar(fixed, (char) 65533);
		if (bugUTF8 == 0)
			return fixed;

		return text;
	}

	public static String readAsUTF8(String text) {
		if (text == null)
			return null;
		return new String(text.getBytes(ENC_LATIN1), ENC_UTF8);
	}

	public static String cleanHtmlChars(String text) {
		int change = 0;
		StringBuilder sb = new StringBuilder(text.length());
		for (int i = 0; i < text.length(); i++) {
			String key = text.substring(i, i + 1);
			String html = htmlEncode.get(key);
			if (html != null) {
				change++;
				sb.append(html);
			} else {
				sb.append(key);
			}
		}
		if (change > 0)
			text = sb.toString();
		return text;
	}

	static HashSet<String> latinError;
	static Hashtable<String, String> htmlEncode;
	// static Hashtable<String, CommonError> ErrorIndex;

	static ArrayList<CommonError> ces;
	static {
		latinError = new HashSet<String>();
		htmlEncode = new Hashtable<String, String>();
		ces = new ArrayList<CommonError>();
		add(new CommonError(160, "&nbps;")); // Â 
		add(new CommonError(161, "&iexcl;")); // Â¡
		add(new CommonError(162, "&cent;")); // Â¢
		add(new CommonError(163, "&pound;")); // Â£
		// add(new CommonError(164, "&curren;")); // Â¤
		add(new CommonError(164, "&euro;")); // Â¤
		add(new CommonError(165, "&yen;")); // Â¥
		add(new CommonError(166, "&brvbar;")); // Â¦
		add(new CommonError(167, "&sect;")); // Â§
		add(new CommonError(168, "&uml;")); // Â¨
		add(new CommonError(169, "&copy;")); // Â©
		add(new CommonError(170, "&ordf;")); // Âª
		add(new CommonError(171, "&laquo;")); // Â«
		add(new CommonError(172, "&not;")); // Â¬
		add(new CommonError(173, "&shy;")); // Â­
		add(new CommonError(174, "&reg;")); // Â®
		add(new CommonError(175, "&masr;")); // Â¯
		add(new CommonError(176, "&deg;")); // Â°
		add(new CommonError(177, "&plusmn;")); // Â±
		add(new CommonError(178, "&sup2;")); // Â²
		add(new CommonError(179, "&sup3;")); // Â³
		add(new CommonError(180, "&acute;")); // Â´
		add(new CommonError(181, "&micro;")); // Âµ
		add(new CommonError(182, "&para;")); // Â¶
		add(new CommonError(183, "&middot;")); // Â·
		add(new CommonError(184, "&cedil;")); // Â¸
		add(new CommonError(185, "&sup1;")); // Â¹
		add(new CommonError(186, "&ordm;")); // Âº
		add(new CommonError(187, "&raquo;")); // Â»

		add(new CommonError(188, "&frac14;")); // Â¼
		add(new CommonError(189, "&frac12;")); // Â½
		add(new CommonError(190, "&frac34;")); // Â¾

		add(new CommonError(191, "&iquest;")); // Â¿

		add(new CommonError(192, "&Agrave;")); // Ã€
		add(new CommonError(193, "&Aacute;")); // Ã�
		add(new CommonError(194, "&Acirc;")); // Ã‚
		add(new CommonError(195, "&Atilde;")); // Ãƒ
		add(new CommonError(196, "&Auml;")); // Ã„
		add(new CommonError(197, "&Aring;")); // Ã…
		add(new CommonError(198, "&Aelig")); // Ã†
		add(new CommonError(199, "&Ccedil;")); // Ã‡
		add(new CommonError(200, "&Egrave;")); // Ãˆ
		add(new CommonError(201, "&Eacute;")); // Ã‰
		add(new CommonError(202, "&Ecirc;")); // ÃŠ
		add(new CommonError(203, "&Euml;")); // Ã‹
		add(new CommonError(204, "&Igrave;")); // ÃŒ
		add(new CommonError(205, "&Iacute;")); // Ã�
		add(new CommonError(206, "&Icirc;")); // ÃŽ
		add(new CommonError(207, "&Iuml;")); // Ã�
		add(new CommonError(208, "&ETH;")); // Ã�
		add(new CommonError(209, "&Ntilde;")); // Ã‘
		add(new CommonError(210, "&Ograve;")); // Ã’
		add(new CommonError(211, "&Oacute;")); // Ã“
		add(new CommonError(212, "&Ocirc;")); // Ã”
		add(new CommonError(213, "&Otilde;")); // Ã•
		add(new CommonError(214, "&Ouml;")); // Ã–
		add(new CommonError(215, "&times;")); // Ã—
		add(new CommonError(216, "&Oslash;")); // Ã˜
		add(new CommonError(217, "&Ugrave;")); // Ã™
		add(new CommonError(218, "&Uacute;")); // Ãš
		add(new CommonError(219, "&Ucirc;")); // Ã›
		add(new CommonError(220, "&Uuml;")); // Ãœ
		add(new CommonError(221, "&Yacute;")); // Ã�
		add(new CommonError(222, "&thorn;")); // Ãž
		add(new CommonError(223, "&szlig;")); // ÃŸ

		add(new CommonError(224, "&agrave;")); // Ã 
		add(new CommonError(225, "&aacute;")); // Ã¡
		add(new CommonError(226, "&acirc;")); // Ã¢
		add(new CommonError(227, "&atilde;")); // Ã£
		add(new CommonError(228, "&auml;")); // Ã¤
		add(new CommonError(229, "&aring;")); // Ã¥
		add(new CommonError(230, "&aelig;")); // Ã¦
		add(new CommonError(231, "&ccedil;")); // Ã§
		add(new CommonError(232, "&egrave;")); // Ã¨
		add(new CommonError(233, "&eacute;")); // Ã©
		add(new CommonError(234, "&ecirc;")); // Ãª
		add(new CommonError(235, "&euml;")); // Ã«
		add(new CommonError(236, "&iagrave;")); // Ã¬
		add(new CommonError(237, "&iacute;")); // Ã­
		add(new CommonError(238, "&acirc;")); // Ã®
		add(new CommonError(239, "&imul;")); // Ã¯
		add(new CommonError(240, "&eth;")); // Ã°
		add(new CommonError(241, "&ntilde;")); // Ã±
		add(new CommonError(242, "&ograve;")); // Ã²
		add(new CommonError(243, "&oacute;")); // Ã³
		add(new CommonError(244, "&ocirc;")); // Ã´
		add(new CommonError(245, "&otilde;")); // Ãµ
		add(new CommonError(246, "&ouml;")); // Ã¶
		add(new CommonError(247, "&divide;")); // Ã·
		add(new CommonError(248, "&oslash;")); // Ã¸
		add(new CommonError(249, "&ugrave;")); // Ã¹
		add(new CommonError(250, "&uacute;")); // Ãº
		add(new CommonError(251, "&ucirc")); // Ã»
		add(new CommonError(252, "&uuml;")); // Ã¼
		add(new CommonError(253, "&yuml;")); // Ã½
		add(new CommonError(254, "&thorn;")); // Ã¾
	}

	private static void add(CommonError ce) {
		ces.add(ce);
		latinError.add(ce.bugSymptom);
		htmlEncode.put(ce.correct, ce.html);
		htmlEncode.put(ce.correct, ce.html);
	}

	protected static class CommonError {
		int code;
		byte[] latin1;
		byte[] utf8;
		String correct;
		String bugSymptom;
		String html;

		public CommonError(int code) {
			this(code, "&#" + code + ";");
		}

		public CommonError(int code, String html) {
			this.code = code;
			this.latin1 = new byte[] { (byte) code };
			this.correct = new String(latin1, ENC_LATIN1).intern();
			this.utf8 = correct.getBytes(ENC_UTF8);
			this.bugSymptom = new String(utf8, ENC_LATIN1).intern();
			this.html = html;
		}
	}
}
