package com.example.bugrap.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Utility methods.
 * 
 * @author bogdan
 */
public class Utils {

	/*
	 * The calendar fields and their types. 
	 */
	private static final int[] fields = new int[] { Calendar.YEAR, Calendar.MONTH, Calendar.WEEK_OF_YEAR, Calendar.DAY_OF_YEAR, Calendar.HOUR_OF_DAY,
			Calendar.MINUTE };
	private static final String[] types = new String[] { "year", "month", "week", "day", "hour", "minute" };

	/**
	 * Format the date as a string representing the human readable date/time interval since the specified date.
	 * @param date	the date to process.
	 * @return	the string representation.
	 */
	public static String stringIntervalFromDate(Date date) {

		// TODO: Well we can optimize these calendars...
		//*
		Calendar past = Calendar.getInstance();
		past.setTime(date);

		Calendar now = Calendar.getInstance();

		for (int i = 0; i < fields.length; i++) {
			int diff = now.get(fields[i]) - past.get(fields[i]);
			if (diff != 0) {
				return stringInterval(diff, types[i]);
			}
		}

		return "";

		//*/

		/*
		long past = date.getTime();
		long now = System.currentTimeMillis();

		long diff = past - now;
		//*/

	}

	/*
	 * Construct the final string interval.
	 */
	private static String stringInterval(int value, String type) {
		if (value > 1) {
			type += "s";
		}
		return value + " " + type + " ago";
	}

	/**
	 * Convert mills to years.
	 * @param mills	the mills count.
	 * @return	the years value.
	 */
	public static int yearsFromMills(long mills) {
		return -1;
	}

	/**
	 * Convert mills to months.
	 * @param mills	the mills count.
	 * @return	the months value.
	 */
	public static int monthsFromMills(long mills) {
		return -1;
	}

	/**
	 * Convert mills to weeks.
	 * @param mills	the mills count.
	 * @return	the weeks value.
	 */
	public static int weeksFromMills(long mills) {
		return -1;
	}

	/**
	 * Convert mills to days.
	 * @param mills	the mills count.
	 * @return	the days value.
	 */
	public static int daysFromMills(long mills) {
		return -1;
	}

	/**
	 * Convert mills to hours.
	 * @param mills	the mills count.
	 * @return	the hours value.
	 */
	public static int hoursFromMills(long mills) {
		return -1;
	}

	/**
	 * Convert mills to mins.
	 * @param mills	the mills count.
	 * @return	the mins value.
	 */
	public static int minsFromMills(long mills) {
		return -1;
	}

}
