package net.minidev.fomat;

/**
 * Format code text
 * 
 * @author uriel
 *
 */
public class CodeUtils {
	public static String formatUpperCamlCase(String name) {
		StringBuilder sb = new StringBuilder();
		boolean upper = true;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '_') {
				upper = true;
				continue;
			}
			if (upper) {
				sb.append(Character.toUpperCase(name.charAt(i)));
				upper = false;
			} else
				sb.append(name.charAt(i));
		}
		return sb.toString();
	}

	public static String formatLowerCamlCase(String name) {
		StringBuilder sb = new StringBuilder();
		boolean upper = false;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '_') {
				upper = true;
				continue;
			}
			if (upper) {
				sb.append(Character.toUpperCase(name.charAt(i)));
				upper = false;
			} else
				sb.append(name.charAt(i));
		}
		return sb.toString();
	}
}
