package net.minidev.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Random;

public class StringUtils {
	/**
	 * Pseudo-random number generator object for use with randomString(). The
	 * Random class is not considered to be cryptographically secure, so only
	 * use these random Strings for low to medium security applications.
	 */
	private static Random randGen = new Random();

	/**
	 * Array of numbers and letters of mixed case. Numbers appear in the list
	 * twice so that there is a more equal chance that a number will be picked.
	 * We can use the array to get a random number or letter by picking a random
	 * array index.
	 */
	private static char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
			+ "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();

	/**
	 * Returns a random String of numbers and letters (lower and upper case) of
	 * the specified length. The method uses the Random class that is built-in
	 * to Java which is suitable for low to medium grade security uses. This
	 * means that the output is only pseudo random, i.e., each number is
	 * mathematically generated so is not truly random.
	 * <p>
	 * 
	 * The specified length must be at least one. If not, the method will return
	 * null.
	 * 
	 * @param length
	 *            the desired length of the random String to return.
	 * @return a random String of numbers and letters of the specified length.
	 */
	public static String randomString(int length) {
		if (length < 1) {
			return null;
		}
		// Create a char buffer to put random letters and numbers in.
		char[] randBuffer = new char[length];
		for (int i = 0; i < randBuffer.length; i++) {
			randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
		}
		return new String(randBuffer);
	}

	static class LocalMessageDigest extends ThreadLocal<MessageDigest> {
		String algo;

		public LocalMessageDigest(String algo) {
			this.algo = algo;
		}

		protected synchronized MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance(algo);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("Fail creating " + algo + " Digester");
			}
		}
	}

	private static ThreadLocal<MessageDigest> MD5_DIGEST = new LocalMessageDigest("MD5");
	private static ThreadLocal<MessageDigest> SHA1_DIGEST = new LocalMessageDigest("SHA1");

	public static String digestSha1(final byte[] data) {
		MessageDigest sha1 = SHA1_DIGEST.get();
		sha1.reset();
		byte[] digest = sha1.digest(data);
		return encodeHex(digest);
	}
	
	public synchronized static String digestMD5(final byte[] data) {
		MessageDigest md = MD5_DIGEST.get();
		md.reset();
		byte[] digest = md.digest(data); // "UTF-8"
		return encodeHex(digest);
	}

	public static String digestSha1(final String data) {
		MessageDigest sha1 = SHA1_DIGEST.get();
		sha1.reset();
		byte[] digest = sha1.digest(data.getBytes());
		return encodeHex(digest);
	}

	public synchronized static String digestMD5(final String data) {
		MessageDigest md = MD5_DIGEST.get();
		md.reset();
		byte[] digest = md.digest(data.getBytes()); // "UTF-8"
		return encodeHex(digest);
	}

	public static String getMD5String(final String stringToHash) {
		return digestMD5(stringToHash);
	}

	static char[] hexa = "0123456789abcdef".toCharArray();

	public static String encodeHex(byte[] bytes) {
		char[] result = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			result[i * 2] = hexa[(bytes[i] & 0xF0) >> 4];
			result[i * 2 + 1] = hexa[bytes[i] & 0x0F];
		}
		return new String(result);
	}

	/**
	 * Return the next space index after the position give in parameter
	 * 
	 * @param src
	 * @param pos
	 * @return -1 if no space found
	 */
	public static int getNextSpaceIndex(String src, int pos) {
		if (src == null || src.length() < pos)
			return -1;
		for (int i = pos; i < src.length(); i++) {
			if (src.charAt(i) == ' ') {
				return i;
			}
		}
		return -1;
	}

	public static boolean isEmptyOrNull(String str) {
		return (str == null || str.length() == 0);
	}

	public static String removeAccents2(String text) {
		return java.text.Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "");
		// return Normalizer.decompose(text, Normalizer.Form.NFC)
	}

	public static boolean containsNonAscii(String str) {
		if (str == null)
			return false;
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (str.charAt(i) > 'z')
				return true;
		}
		return false;
	}

	/**
	 * Supprime tous les accents d'un chaine de caractere
	 * 
	 * @param value
	 * @return String
	 */
	public static String removeAccents(String value) {
		if (value == null)
			return null;

		int len = value.length();
		{
			boolean change = false;
			for (int i = 0; i < len; i++) {
				final char c = value.charAt(i);
				if (c >= 192) {
					change = true;
					break;
				}
			}
			if (!change)
				return value;
		}
		char[] result = new char[value.length()];
		for (int i = 0; i < len; i++) {
			final char c = value.charAt(i);
			switch (c) {
			case 192: // A grave
			case 193: // A aigue
			case 194: // A cir
			case 195: // A tild
			case 196: // A trema
			case 197: // A rond
				result[i] = 'A';
				break;
			case 198: // A dans le E
				result[i] = 'A';
				break;
			case 199: // C dedie
				result[i] = 'C';
				break;
			case 200: // E grave
			case 201: // E aigue
			case 202: // E cir
			case 203: // E trema
				result[i] = 'E';
				break;
			case 204: // I grave
			case 205: // I aigue
			case 206: // I cir
			case 207: // I trema
				result[i] = 'I';
				break;
			case 208: // D bar
				result[i] = 'D';
				break;
			case 209: // N tild
				result[i] = 'N';
				break;
			case 210: // O grave
			case 211: // O aigue
			case 212: // O cir
			case 213: // O tild
			case 214: // O trema
				result[i] = 'O';
				break;
			case 215: // multiplie lineaire
				result[i] = 'x';
				break;
			case 216: // O baré
				result[i] = 'O';
				break;
			case 217: // U grave
			case 218: // U aigue
			case 219: // U cir
			case 220: // U trema
				result[i] = 'U';
				break;
			case 221: // Y grave
				result[i] = 'Y';
				break;
			case 222: // sho
				result[i] = 'b';
				break;
			case 223: // beta
				result[i] = 'B';
				break;
			case 224: // a grace
			case 225: // a aigue
			case 226: // a cir
			case 227: // a tild
			case 228: // a trema
			case 229: // a rond
				result[i] = 'a';
				break;
			// 230: o dans le e
			case 231: // c cedie
				result[i] = 'c';
				break;
			case 232: // e grave
			case 233: // e aigue
			case 234: // e cir
			case 235: // e trema
				result[i] = 'e';
				break;
			case 236: // i grave
			case 237: // i aigue
			case 238: // i cir
			case 239: // i trema
				result[i] = 'i';
				break;
			case 240: // o croie (nordique)
				result[i] = 'o';
				break;
			case 241: // n tild (nodique)
				result[i] = 'n';
				break;
			case 242: // o grave
			case 243: // o aigue
			case 244: // o cir
			case 245: // o tild
			case 246: // o trema
				result[i] = 'o';
				break;
			case 247: // divise lineaire
				result[i] = '-';
				break;
			case 248: // 0 baré
				result[i] = 'o';
				break;
			case 249: // u grave
			case 250: // u aigue
			case 251: // u cir
			case 252: // u trema
				result[i] = 'u';
				break;
			case 253: // y grave
				result[i] = 'y';
				break;
			case 254: // san
				result[i] = 'b';
				break;
			case 255: // y trema
				result[i] = 'y';
				break;
			default:
				result[i] = c;
				break;
			}
		}
		return new String(result).replace("œ", "oe");
	}

	public static String escapeXmlAttribut(String str) {
		if (str == null)
			return str;
		StringBuilder sb = new StringBuilder(str.length() + 5);
		char[] cs = str.toCharArray();
		for (char c : cs) {
			if (c == '&')
				sb.append("&amp;");
			else if (c == '>')
				sb.append("&gt;");
			else if (c == '<')
				sb.append("&lt;");
			else if (c == '\"')
				sb.append("&quot;");
			else
				sb.append(c);
		}
		return sb.toString();
	}
}
