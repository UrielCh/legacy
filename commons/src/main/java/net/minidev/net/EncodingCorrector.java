package net.minidev.net;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.TreeSet;

public class EncodingCorrector {
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
		int bugUTF8 = countChar(text, (char) 65533);
		if (bugUTF8 > 0)
			// bug can not be Fixed.
			// Data Lost
			return text;

		TreeSet<String> errors = getLatin1Errors(text);

		if (errors == null)
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

	public static int getLatin1ErrorCount(String text) {
		TreeSet<String> errors = getLatin1Errors(text);
		if (errors == null)
			return 0;
		return errors.size();
	}

	public static int countNonAsciiChar(String text) {
		int c = 0;
		int l = text.length();
		for (int i = 0; i < l; i++) {
			char ch = text.charAt(i);
			if (ch < 0 || ch > 127)
				c++;
		}
		return c;
	}

	public static char getFirstNonAsciiChar(String text) {
		int l = text.length();
		for (int i = 0; i < l; i++) {
			char ch = text.charAt(i);
			if (ch < 0 || ch > 127)
				return ch;
		}
		return 0;
	}

	private static TreeSet<String> getLatin1Errors(String text) {
		TreeSet<String> errors = null;
		for (int i = 0; i < text.length() - 1; i++) {
			String key = text.substring(i, i + 2);
			if (!latinError.contains(key))
				continue;
			// bugLatin++;
			if (errors == null) {
				errors = new TreeSet<String>();
			}
			errors.add(key.intern());
		}
		return errors;
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
		add(new CommonError(160, "&nbps;")); //  
		add(new CommonError(161, "&iexcl;")); // ¡
		add(new CommonError(162, "&cent;")); // ¢
		add(new CommonError(163, "&pound;")); // £
		// add(new CommonError(164, "&curren;")); // ¤
		add(new CommonError(164, "&euro;")); // ¤
		add(new CommonError(165, "&yen;")); // ¥
		add(new CommonError(166, "&brvbar;")); // ¦
		add(new CommonError(167, "&sect;")); // §
		add(new CommonError(168, "&uml;")); // ¨
		add(new CommonError(169, "&copy;")); // ©
		add(new CommonError(170, "&ordf;")); // ª
		add(new CommonError(171, "&laquo;")); // «
		add(new CommonError(172, "&not;")); // ¬
		add(new CommonError(173, "&shy;")); // ­
		add(new CommonError(174, "&reg;")); // ®
		add(new CommonError(175, "&masr;")); // ¯
		add(new CommonError(176, "&deg;")); // °
		add(new CommonError(177, "&plusmn;")); // ±
		add(new CommonError(178, "&sup2;")); // ²
		add(new CommonError(179, "&sup3;")); // ³
		add(new CommonError(180, "&acute;")); // ´
		add(new CommonError(181, "&micro;")); // µ
		add(new CommonError(182, "&para;")); // ¶
		add(new CommonError(183, "&middot;")); // ·
		add(new CommonError(184, "&cedil;")); // ¸
		add(new CommonError(185, "&sup1;")); // ¹
		add(new CommonError(186, "&ordm;")); // º
		add(new CommonError(187, "&raquo;")); // »

		add(new CommonError(188, "&frac14;")); // ¼
		add(new CommonError(189, "&frac12;")); // ½
		add(new CommonError(190, "&frac34;")); // ¾

		add(new CommonError(191, "&iquest;")); // ¿

		add(new CommonError(192, "&Agrave;")); // À
		add(new CommonError(193, "&Aacute;")); // Á
		add(new CommonError(194, "&Acirc;")); // Â
		add(new CommonError(195, "&Atilde;")); // Ã
		add(new CommonError(196, "&Auml;")); // Ä
		add(new CommonError(197, "&Aring;")); // Å
		add(new CommonError(198, "&Aelig")); // Æ
		add(new CommonError(199, "&Ccedil;")); // Ç
		add(new CommonError(200, "&Egrave;")); // È
		add(new CommonError(201, "&Eacute;")); // É
		add(new CommonError(202, "&Ecirc;")); // Ê
		add(new CommonError(203, "&Euml;")); // Ë
		add(new CommonError(204, "&Igrave;")); // Ì
		add(new CommonError(205, "&Iacute;")); // Í
		add(new CommonError(206, "&Icirc;")); // Î
		add(new CommonError(207, "&Iuml;")); // Ï
		add(new CommonError(208, "&ETH;")); // Ð
		add(new CommonError(209, "&Ntilde;")); // Ñ
		add(new CommonError(210, "&Ograve;")); // Ò
		add(new CommonError(211, "&Oacute;")); // Ó
		add(new CommonError(212, "&Ocirc;")); // Ô
		add(new CommonError(213, "&Otilde;")); // Õ
		add(new CommonError(214, "&Ouml;")); // Ö
		add(new CommonError(215, "&times;")); // ×
		add(new CommonError(216, "&Oslash;")); // Ø
		add(new CommonError(217, "&Ugrave;")); // Ù
		add(new CommonError(218, "&Uacute;")); // Ú
		add(new CommonError(219, "&Ucirc;")); // Û
		add(new CommonError(220, "&Uuml;")); // Ü
		add(new CommonError(221, "&Yacute;")); // Ý
		add(new CommonError(222, "&thorn;")); // Þ
		add(new CommonError(223, "&szlig;")); // ß

		add(new CommonError(224, "&agrave;")); // à
		add(new CommonError(225, "&aacute;")); // á
		add(new CommonError(226, "&acirc;")); // â
		add(new CommonError(227, "&atilde;")); // ã
		add(new CommonError(228, "&auml;")); // ä
		add(new CommonError(229, "&aring;")); // å
		add(new CommonError(230, "&aelig;")); // æ
		add(new CommonError(231, "&ccedil;")); // ç
		add(new CommonError(232, "&egrave;")); // è
		add(new CommonError(233, "&eacute;")); // é
		add(new CommonError(234, "&ecirc;")); // ê
		add(new CommonError(235, "&euml;")); // ë
		add(new CommonError(236, "&iagrave;")); // ì
		add(new CommonError(237, "&iacute;")); // í
		add(new CommonError(238, "&acirc;")); // î
		add(new CommonError(239, "&imul;")); // ï
		add(new CommonError(240, "&eth;")); // ð
		add(new CommonError(241, "&ntilde;")); // ñ
		add(new CommonError(242, "&ograve;")); // ò
		add(new CommonError(243, "&oacute;")); // ó
		add(new CommonError(244, "&ocirc;")); // ô
		add(new CommonError(245, "&otilde;")); // õ
		add(new CommonError(246, "&ouml;")); // ö
		add(new CommonError(247, "&divide;")); // ÷
		add(new CommonError(248, "&oslash;")); // ø
		add(new CommonError(249, "&ugrave;")); // ù
		add(new CommonError(250, "&uacute;")); // ú
		add(new CommonError(251, "&ucirc")); // û
		add(new CommonError(252, "&uuml;")); // ü
		add(new CommonError(253, "&yuml;")); // ý
		add(new CommonError(254, "&thorn;")); // þ
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
