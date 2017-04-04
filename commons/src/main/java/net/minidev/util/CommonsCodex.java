package net.minidev.util;

public class CommonsCodex {

	/**
	 * Converts an array of characters representing hexadecimal values into an
	 * array of bytes of those same values. The returned array will be half the
	 * length of the passed array, as it takes two characters to represent any
	 * given byte. An exception is thrown if the passed char array has an odd
	 * number of elements.
	 * 
	 * @param data
	 *            An array of characters containing hexadecimal digits
	 * @return A byte array containing binary data decoded from the supplied
	 *         char array.
	 */
	public static byte[] decodeHex(char[] data) throws NullPointerException {

		int len = data.length;

		if ((len & 0x01) != 0) {
			throw new NullPointerException("Odd number of characters.");
		}

		byte[] out = new byte[len >> 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}

		return out;
	}

	/**
	 * Converts a hexadecimal character to an integer.
	 * 
	 * @param ch
	 *            A character to convert to an integer digit
	 * @param index
	 *            The index of the character in the source
	 * @return An integer
	 */
	protected static int toDigit(char ch, int index) throws NullPointerException {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new NullPointerException("Illegal hexadecimal charcter " + ch + " at index " + index);
		}
		return digit;
	}

}
