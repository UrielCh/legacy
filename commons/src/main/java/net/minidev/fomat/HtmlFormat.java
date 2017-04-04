package net.minidev.fomat;

/**
 * Basic HTML helper
 * 
 * @author Uriel Chemouni
 * 
 */
public class HtmlFormat {
	/**
	 * escape XML chars &gt; &lt; &amp;
	 * 
	 * if (text stay unchanged return the original text)
	 * 
	 * @param text
	 * 
	 * @return the string widthout &amp; &lt; &gt;
	 */
	public static String escapeHtml(String text) {
		StringBuilder sb = new StringBuilder(text.length());

		for (char c : text.toCharArray()) {
			if (c == '&')
				sb.append("&amp;");
			else if (c == '>')
				sb.append("&gt;");
			else if (c == '<')
				sb.append("&lt;");
			else
				sb.append(c);
		}
		if (sb.length() == text.length())
			return text;
		return sb.toString();
	}

	public static String unEscapeHtml(String text) {
		text = text.replace("&gt;", ">");
		text = text.replace("&lt;", "<");
		text = text.replace("&amp;", "&");
		return text;
	}

	/**
	 * escape XML chars &gt; &lt; &amp;
	 * 
	 * append string widthout &amp; &lt; &gt;
	 */
	public static StringBuilder escapeHtml(StringBuilder sb, String text) {
		int len = text.length();
		for (int i = 0; i < len; i++) {
			char c = text.charAt(i);
			if (c == '&')
				sb.append("&amp;");
			else if (c == '>')
				sb.append("&gt;");
			else if (c == '<')
				sb.append("&lt;");
			else
				sb.append(c);
		}
		return sb;
	}
}
