package net.minidev.fomat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Parse a date by guessing date format.
 * 
 * @author Uriel Chemouni
 */
public class DateUtils {
	static DateFrmtIndexed fr4Digit;
	static DateFrmtIndexed us4Digit;
	static DateFrmtIndexed fr2Digit;
	static DateFrmtIndexed us2Digit;

	static DateFrmtIndexed frDigit;
	static DateFrmtIndexed usDigit;

	private static DateFrmtIndexed combineFormat(Locale locale, String... prefixs) {
		DateFrmtIndexed ret = new DateFrmtIndexed();

		for (String prefix : prefixs) {
			ret.add(locale, prefix, prefix + " HH:mm", prefix + " HH:mm:ss");
			ret.add(locale, prefix + " z", prefix + " HH:mm z", prefix + " HH:mm:ss z");
			ret.add(locale, prefix + " Z", prefix + " HH:mm Z", prefix + " HH:mm:ss Z");
			ret.add(locale, prefix + "Z", prefix + " HH:mmZ", prefix + " HH:mm:ssZ");
		}

		// always add the Standard code
		if (locale.equals(Locale.US)) {
			ret.add(locale, "EEE MMM dd HH:mm:ss z yyyy");
			ret.add(locale, "EEE MMM dd HH:mm:ss Z yyyy");
		}
		return ret;
	}

	private static int[] ORDERED_DATE_FIELD = new int[] {
			// DST_OFFSET
			Calendar.ZONE_OFFSET, Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR_OF_DAY,
			Calendar.DAY_OF_MONTH, Calendar.MONTH };

	public static GregorianCalendar getStartHour(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		for (int field = 0; field < 4; field++)
			// reset all to Calendar.HOUR_OF_DAY
			cal.set(ORDERED_DATE_FIELD[field], 0);
		return cal;
	}

	public static GregorianCalendar getStartDay(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		for (int field = 0; field < 5; field++)
			// reset all to Calendar.HOUR_OF_DAY
			cal.set(ORDERED_DATE_FIELD[field], 0);
		return cal;
	}

	public static GregorianCalendar getStartMonth(Date date) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		for (int field = 0; field < 6; field++)
			// reset all to DAY_OF_MONTH
			cal.set(ORDERED_DATE_FIELD[field], 0);
		return cal;
	}

	static {
		DateUtils.fr4Digit = combineFormat(Locale.FRANCE, "dd/MM/yyyy", "yyyy/MM/dd", "dd-MM-yyyy", "yyyy-MM-dd");
		DateUtils.us4Digit = combineFormat(Locale.US, "MM/dd/yyyy", "yyyy/dd/MM", "MM-dd-yyyy", "yyyy-dd-MM");
		DateUtils.fr2Digit = combineFormat(Locale.FRANCE, "dd/MM/yy", "dd-MM-yy", "dd-MMM-yy");
		DateUtils.us2Digit = combineFormat(Locale.US, "MM/dd/yy", "MM-dd-yy", "dd-MMM-yy");

		DateUtils.frDigit = combineFormat(Locale.FRANCE, "dd/MM/yy", "dd-MM-yy", "dd/MM/yyyy", "yyyy/MM/dd",
				"dd-MM-yyyy", "yyyy-MM-dd");
		DateUtils.usDigit = combineFormat(Locale.US, "MM/dd/yy", "MM-dd-yy", "MM/dd/yyyy", "yyyy/dd/MM", "MM-dd-yyyy",
				"yyyy-dd-MM");
	}

	/**
	 * @deprecated
	 */
	public static Date ParseDateFR2Digit(String text) throws ParseException {
		return fr2Digit.ParseDate(text);
	}

	public static Date parseDateFR2Digit(String text) throws ParseException {
		return fr2Digit.ParseDate(text);
	}

	/**
	 * @deprecated
	 */
	public static Date parseDateUS2Date(String text) throws ParseException {
		return us2Digit.ParseDate(text);
	}

	public static Date parseDateUS2Digit(String text) throws ParseException {
		return us2Digit.ParseDate(text);
	}

	public static Date parseDateFR4Digit(String text) throws ParseException {
		return fr4Digit.ParseDate(text);
	}

	public static Date parseDateUS4Digit(String text) throws ParseException {
		return us4Digit.ParseDate(text);
	}

	public static Date parseDateFR(String text) throws ParseException {
		return frDigit.ParseDate(text);
	}

	public static Date parseDateUS(String text) throws ParseException {
		return usDigit.ParseDate(text);
	}

	static class DateFrmt {
		public String frm;
		public Pattern pat;
		public SimpleDateFormat sdf;

		public DateFrmt(String format, Locale locale) {
			this.frm = format;
			String tmp = format;
			tmp = tmp.replace("yyyy", "(:?20|19)\\d\\d");
			tmp = tmp.replace("yy", "\\d\\d");

			tmp = tmp.replace("Z", "[-+]HHmm");
			// 01 -31
			tmp = tmp.replace("dd",
					"(:?01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31)");
			tmp = tmp.replace("HH", "(:?00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23)");
			tmp = tmp.replace("hh", "(:?01|02|03|04|05|06|07|08|09|10|11|12)");
			tmp = tmp.replace("mm", "[0-5][0-9]");
			tmp = tmp.replace("ss", "[0-5][0-9]");
			tmp = tmp.replace("z", "[A-Z][A-Z][A-Z]");

			tmp = tmp.replace("EEE", "(:?Mon|Tue|Wed|Thu|Fri|Sat|Sun)");
			tmp = tmp.replace("MMM", "(:?Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)");
			tmp = tmp.replace("MM", "(:?01|02|03|04|05|06|07|08|09|10|11|12)");

			this.pat = Pattern.compile("^" + tmp + "$", Pattern.CASE_INSENSITIVE);
			this.sdf = new SimpleDateFormat(format, locale);
		}

		@Override
		public String toString() {
			return frm;
		}
	}

	static class DateFrmtIndexed {
		@SuppressWarnings("unchecked")
		ArrayList<DateFrmt>[] frmts = new ArrayList[31];

		public DateFrmtIndexed() {
		}

		public void add(Locale locale, String... formats) {
			for (String format : formats) {
				int len = format.length();
				// z => CET,PDT ...
				if (format.indexOf("z") >= 0)
					len += 2;
				// Z => +0100, -0800...
				if (format.indexOf("Z") >= 0)
					len += 4;
				if (len >= frmts.length)
					throw new NullPointerException("Too Long DateFormat:" + format);
				if (frmts[len] == null)
					frmts[len] = new ArrayList<DateUtils.DateFrmt>();
				frmts[len].add(new DateFrmt(format, locale));
			}
		}

		public synchronized Date ParseDate(String text) throws ParseException {
			if (text == null)
				return null;
			text = text.trim();
			if (text.length() == 0)
				return null;

			int len = text.length();
			if (len >= frmts.length)
				throw new NullPointerException("the date: '" + text + "' is too long to be parse");

			if (frmts[len] == null)
				throw new NullPointerException("Not right length known pattern for parsing date:" + text);

			ArrayList<DateFrmt> selected = frmts[len];

			for (DateFrmt df : selected) {
				if (df.pat.matcher(text).matches())
					return df.sdf.parse(text);
			}
			throw new NullPointerException("can not parse (" + text + ")");
		}
	}

	public static Date[] getIntervalDate(Date start, Date lastDate, int dayinterval) {
		ArrayList<Date> dates = new ArrayList<Date>();
		dates.add(start);
		Date current = start;
		while (current.getTime() < lastDate.getTime()) {
			start = current;
			current = new Date(start.getTime() + 1000L * 60L * 60L * 24L * dayinterval);
			dates.add(current);
		}
		return dates.toArray(new Date[dates.size()]);
	}
}
