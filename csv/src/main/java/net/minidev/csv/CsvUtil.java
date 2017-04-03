package net.minidev.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import org.apache.commons.io.LineIterator;

public class CsvUtil {
	public static String[] commonHeader = new String[] { "email", "date", "gender", "city", "zip", "year", "country",
			"county", "addr", "addr1", "addr2", "firstname", "first", "last", "lastname", "gender", "age", "marital",
			"education", "zipcode", "address1", "address2", "address3", "Adresse Email", "Email Format",
			"Adresse postalle", "Civilité", "Code postal", "Nom", "Ville", "user.Email", "user.FirstName",
			"user.LastName", "user.ZipCode", "user.Title", "user.DateOfBirth", "date_unsub", "id_profile" };

	public static ArrayList<String> LoadHead(Reader in, int nbLine) throws IOException {
		LineIterator iter = new LineIterator(in);
		String line = null;

		ArrayList<String> lines = new ArrayList<String>();
		while (iter.hasNext()) {
			line = iter.nextLine();
			lines.add(line);
			if (--nbLine <= 0)
				break;
		}
		iter.close();
		return lines;
	}

	public static int countChar(String str, char c) {
		int count = 0;
		for (char t : str.toCharArray())
			if (t == c)
				count++;
		return count;
	}
}
