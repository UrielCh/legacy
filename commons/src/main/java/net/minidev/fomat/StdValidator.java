package net.minidev.fomat;

import java.util.regex.Pattern;

public class StdValidator {

	private static Pattern EmailPat = Pattern
			.compile("^[a-zA-Z0-9\\-_]+[a-zA-Z0-9+!\\.\\-_]*@[a-zA-Z0-9\\-_]+\\.[a-zA-Z0-9\\.\\-_]{1,}[a-zA-Z\\-_]+");

	public static boolean isValidEmail(String mail) {
		if (mail == null)
			return false;
		return EmailPat.matcher(mail).matches();
	}
}
