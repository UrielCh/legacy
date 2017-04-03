package net.minidev.config;

import java.io.File;

public class ConfigurationKey implements CharSequence {
	private String key;
	private KeyMeta meta;

	public static String toErrorPrefix(CharSequence chr) {
		if (chr instanceof ConfigurationKey) {
			return "\n " + ((ConfigurationKey) chr).getErrorMessage() + "\n";
		}
		return "";
	}

	public ConfigurationKey(String key, KeyMeta meta) {
		this.key = key;
		this.meta = meta;
		if (meta == null)
			throw new NullPointerException("META can not be null");
		if (key == null)
			throw new NullPointerException("Key can not be null");
	}

	public String getErrorMessage() {
		return meta.errorMessage.replace("$(key)", key);
	}

	public String getsuggestValue() {
		return meta.suggestValue.replace("$(key)", key);
	}

	boolean callback(File file) {
		if (meta.callback != null) {
			return meta.callback.run(file, key, meta.suggestValue);
		}
		return false;
	}

	public char charAt(int index) {
		return key.charAt(index);
	}

	public int length() {
		return key.length();
	}

	public CharSequence subSequence(int start, int end) {
		return key.subSequence(start, end);
	}

	public String toString() {
		return key;
	}

	public KeyMeta getMeta() {
		return meta;
	}

	public static class KeyMeta {
		public String errorMessage;
		public String suggestValue;
		public KeyMetaCallback callback;
	}

	public static interface KeyMetaCallback {
		/**
		 * @param file
		 *            configuration file
		 * @param key
		 *            keyname
		 * @param suggestValue
		 *            suggested value
		 * 
		 * @return true if the error may be solved.
		 */
		boolean run(File file, String key, String suggestValue);
	}
}
