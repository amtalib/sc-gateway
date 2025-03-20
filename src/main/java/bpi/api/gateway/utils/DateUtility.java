package bpi.api.gateway.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author AMTalib
 * @since 09/15/2019
 * */
public final class DateUtility {

	public static void main(String[] args) throws ParseException {
		
		Date x = DateUtility.convertStringToDate("1990-Sep-01", "yyyy-MM-dd");
		
		// Date x = DateUtility.convertStringToDate("2021-05-05", "yyyy-MM-dd");
		
		System.out.println(x);
		
		System.out.println(DateUtility.getNumberOfMinutesSinceMidnight() );
		
		 
	}
	
	public static int getNumberOfMinutesSinceMidnight() {
		return (int) DateUtility.getNumberOfMinutesInBetween(DateUtility.getMidNightDate(), new java.util.Date() );
	}
	
	public static int getNumberOfSecondsSinceMidnight() {
		return (int) DateUtility.getNumberOfSecondsInBetween(DateUtility.getMidNightDate(), new java.util.Date() );
	}
	
	public static Date getMidNightDate() {
		Calendar date = new GregorianCalendar();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		
		return date.getTime();
	}
	
	
	public static LocalDate convertToLocalDate(Date dateToConvert) {
	    return Instant.ofEpochMilli(dateToConvert.getTime())
	      .atZone(ZoneId.systemDefault())
	      .toLocalDate();
	}
	
	public static Date convertLocalDateToDate(LocalDate ld) {
		return java.sql.Date.valueOf(ld);
	}
	
	public static Date convertToDate(long milliseconds) {
		Date applicationDate = null;
		
		try {
			int len = String.valueOf(milliseconds).length();
			
			if (len == 10) {
				// Java 8
				applicationDate = Date.from( Instant.ofEpochSecond(milliseconds) );
			}
			else if (len == 13) {
				applicationDate = new Date(milliseconds);	
			}
			
		} catch (Exception e) {}
		
		return applicationDate;
	}
	
	public static Date subtractMinutesToDate(Date date, int numberOfMinutes) {

		Calendar cal = new GregorianCalendar();

		cal.setTime(date);

		cal.add(Calendar.MINUTE, (numberOfMinutes < 0 ? numberOfMinutes
				: (numberOfMinutes * -1) ) );

		return cal.getTime();
	}
	
	public static Date getLastMinute(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static long getNumberOfHoursInBetween(Date startDate, Date endDate) {
		long duration = endDate.getTime() - startDate.getTime();
		return TimeUnit.HOURS.convert(duration, TimeUnit.MILLISECONDS);
	}

	public static long getNumberOfMinutesInBetween(Date startDate, Date endDate) {
		long duration = endDate.getTime() - startDate.getTime();
		return TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
	}

	public static long getNumberOfSecondsInBetween(Date startDate, Date endDate) {
		long duration = endDate.getTime() - startDate.getTime();
		return TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS);
	}

	public static long getNumberOfMilliSecondsInBetween(Date startDate, Date endDate) {
		long duration = endDate.getTime() - startDate.getTime();
		return TimeUnit.MILLISECONDS.convert(duration, TimeUnit.MILLISECONDS);
	}
	
	public static Date getFirstDateOfTheYear(java.util.Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		
		try {
			return formatDate("MMddyyyy", cal.getTime() );	
		} catch (Exception e) {}
		
		return cal.getTime();
	} // End method

	public static Date getLastDateOfTheYear(java.util.Date date) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_YEAR, DateUtility.getNumberOfDaysInYear(date) );
		
		try {
			return formatDate("MMddyyyy", cal.getTime() );	
		} catch (Exception e) {}
		
		return cal.getTime();
	} // End method

	public static int getNumberOfDaysInMonth(java.util.Date date) {
		Calendar mycal = Calendar.getInstance();
		mycal.setTime(date);

		// Get the number of days in that month
		return mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
	} // End method

	public static int getNumberOfDaysInYear(java.util.Date date) {
		Calendar mycal = Calendar.getInstance();
		mycal.setTime(date);

		// Get the number of days in that year
		return mycal.getActualMaximum(Calendar.DAY_OF_YEAR);
	} // End method

	/**
	 * GMT, UTC
	 * **/
	public static java.util.Date changeTimeZone(String timeZone, Date date)
			throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

		java.util.Date parsedDate = dateFormat.parse(DateUtility
				.extractDateDetails("yyyyMMdd HH:mm:ss", date));

		return parsedDate;
	} // end method

	public static java.sql.Timestamp getCurrentTimeStamp() {
		java.util.Date today = new java.util.Date();

		return new java.sql.Timestamp(today.getTime());
	} // End method

	public static Date getDate(Date baseDate, int hours) {

		Calendar now = Calendar.getInstance();

		now = Calendar.getInstance();
		now.add(Calendar.HOUR, hours);

		return now.getTime();
	}

	public static String getCurrentDate(String format) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		return new SimpleDateFormat(format).format(cal.getTime());
	}

	public static String extractDateDetails(String format, Date date) {
		if (date == null) {
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		return new SimpleDateFormat(format).format(cal.getTime());
	}

	public static java.sql.Date convertDate(java.util.Date date) {
		if (date == null) {
			return null;
		}

		return new java.sql.Date(date.getTime());
	}

	public static java.util.Date convertDate(java.sql.Date date) {
		if (date == null) {
			return null;
		}

		return new java.sql.Date(date.getTime());
	}

	public static int diffInYears(Date first, Date last) {
	    Calendar a = getCalendar(first);
	    Calendar b = getCalendar(last);
	    int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
	    
	    if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || 
	        (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE) ) ) {
	        diff--;
	    }
	    
	    return diff;
	}

	public static Calendar getCalendar(Date date) {
	    Calendar cal = Calendar.getInstance(Locale.US);
	    cal.setTime(date);
	    return cal;
	}
	
	public static int diff(java.sql.Date start, java.sql.Date end) {

		long endLongDate = end.getTime();
		long startLongDate = start.getTime();
		long diff = (endLongDate - startLongDate);

		return (int) (diff / (24 * 60 * 60 * 1000));
	}

	public static int diffInHours(java.util.Date start, java.util.Date end) {
		long endLongDate = end.getTime();
		long startLongDate = start.getTime();
		long diff = (endLongDate - startLongDate);

		return (int) (diff / (60 * 60 * 1000));
	}

	public static int diff(java.util.Date start, java.util.Date end) {

		long endLongDate = end.getTime();
		long startLongDate = start.getTime();
		long diff = (endLongDate - startLongDate);

		return (int) (diff / (24 * 60 * 60 * 1000));
	}

	public static int diff(java.sql.Date start, java.util.Date end) {

		long endLongDate = end.getTime();
		long startLongDate = start.getTime();
		long diff = (endLongDate - startLongDate);

		return (int) (diff / (24 * 60 * 60 * 1000));
	}

	public static int diff(java.util.Date start, java.sql.Date end) {

		long endLongDate = end.getTime();
		long startLongDate = start.getTime();
		long diff = (endLongDate - startLongDate);

		return (int) (diff / (24 * 60 * 60 * 1000));
	}

	/**
	 * Validates dateStr
	 * 
	 * @param dateStr
	 *            format MMddyyyy
	 * */
	public static boolean isValidDateString(String dateStr) {

		if (dateStr == null || dateStr.length() != 8) {
			return false;
		}

		try {
			new SimpleDateFormat("MMddyyyy").parse(dateStr);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}
	
	/**
	 * Validates dateStr
	 * 
	 * @param dateStr
	 *            format MMddyyyy
	 * */
	public static boolean isValidDateString(String format, String dateStr) {
		
		if (format == null || format == "") {
			return false;
		}

		if (dateStr == null || dateStr == "") {
			return false;
		}

		try {
			new SimpleDateFormat(format).parse(dateStr);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}

	/**
	 * Converts dateStr to sql.Date
	 * 
	 * @param dateStr
	 *            format MMddyyyy
	 * */
	public static java.sql.Date convert(String dateStr) {
		// Precondition
		assert isValidDateString(dateStr) : "Invalid date format " + dateStr;

		if (!isValidDateString(dateStr)) {
			return null;
		}

		java.util.Date tmpDate = null;
		java.sql.Date date = null;

		try {
			tmpDate = new SimpleDateFormat("MMddyyyy").parse(dateStr);

			date = new java.sql.Date(tmpDate.getTime());
		} catch (Exception e) {
			return null;
		}

		return date;
	} // End method

	/**
	 * @param julianDate
	 *            (format=yyyyDDD)
	 * **/
	public static Date convertJulianDateToUtilDate(String julianDate)
			throws ParseException {

		if (julianDate == null || julianDate.length() != 7) {
			return null;
		}

		DateFormat fmt1 = new SimpleDateFormat("yyyyDDD");

		return fmt1.parse(julianDate);
	}

	/**
	 * Converts java.util.Date to Julian date
	 * 
	 * @param date
	 *            - the date to be converted to Julian format (yyyyDDD) must be
	 *            of the format
	 * 
	 * @return Julian date formatted as yyyyDDD
	 * */
	public static String converUtilDateToJulian(Date date) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.setGregorianChange(new Date(Long.MAX_VALUE)); // setting the
															// calendar to act
															// as a pure Julian
															// calendar.

		Date todayJD = cal.getTime(); // getting the calculated time of today?s
										// Julian date

		return new SimpleDateFormat("yyyyDDD").format(todayJD);
	}
	
	public static String converUtilDateToJulianShort(Date date) {

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.setGregorianChange(new Date(Long.MAX_VALUE)); // setting the
															// calendar to act
															// as a pure Julian
															// calendar.

		Date todayJD = cal.getTime(); // getting the calculated time of today?s
										// Julian date

		return new SimpleDateFormat("yyDDD").format(todayJD);
	}

	public static boolean isEqualDate(Date date1, Date date2) {

		if (date1 == null && date2 == null) {
			return true;
		}

		if (date1 == null) {
			return false;
		}

		if (date2 == null) {
			return false;
		}

		String j1 = converUtilDateToJulian(date1);
		String j2 = converUtilDateToJulian(date2);

		return j1.equals(j2);
	} // End method

	/*
	 * Allowed date formats : yy/MM/dd MMddyyyy dd/MM/yy, yyyy-MM-dd, MM/dd/yyyy, yyyy-MMM-dd
	 */
	public static Date convertStringToDate(String dateStr, String format)
			throws ParseException {

		if (dateStr == null) {
			return null;
		}

		if (dateStr.length() == 11 && "yyyy-MMM-dd".equalsIgnoreCase(format) == false) {
			format = "yyyy-MMM-dd";
			System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
			
			DateFormat f = new SimpleDateFormat(format);
			return f.parse(dateStr);
		}
		
		if (dateStr.length() == 20 && "yyyy-MMM-dd hh:mm aa".equalsIgnoreCase(format) == false) {
			format = "yyyy-MMM-dd hh:mm aa";
			System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
			
			DateFormat f = new SimpleDateFormat(format);
			return f.parse(dateStr);
		}
		
		if (dateStr.length() == 10 && dateStr.contains("/") == true && ("MM/dd/yyyy".equalsIgnoreCase(format) == false 
				|| "yyyy/MM/dd".equalsIgnoreCase(format) == false) ) {
			
			try {
				format = "MM/dd/yyyy";
				System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
				
				DateFormat f = new SimpleDateFormat(format);
				return f.parse(dateStr);
			} catch (Exception e) {
				format = "yyyy/MM/dd";
				System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
				
				DateFormat f = new SimpleDateFormat(format);
				return f.parse(dateStr);
			}
		}
		
		if (dateStr.length() == 10 && dateStr.contains("-") == true && ("yyyy-MM-dd".equalsIgnoreCase(format) == false 
				|| "MM-dd-yyyy".equalsIgnoreCase(format) == false) ) {
			
			try {
				format = "yyyy-MM-dd";
				System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
				
				DateFormat f = new SimpleDateFormat(format);
				return f.parse(dateStr);
			} catch (Exception e) {
				format = "MM-dd-yyyy";
				System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
				
				DateFormat f = new SimpleDateFormat(format);
				return f.parse(dateStr);
			}
		}
		
		// yy/MM/dd, dd/MM/yy
		if (dateStr.length() == 8 && dateStr.contains("/") && ("yy/MM/dd".equalsIgnoreCase(format) == false 
				|| "dd/MM/yy".equalsIgnoreCase(format) == false) ) {
			try {
				format = "yy/MM/dd";
				System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
				
				DateFormat f = new SimpleDateFormat(format);
				return f.parse(dateStr);
			} catch (Exception e) {
				format = "dd/MM/yy";
				System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
				
				DateFormat f = new SimpleDateFormat(format);
				return f.parse(dateStr);
			}
		}
		
		// MMddyyyy yyyyMMdd
		if (dateStr.length() == 8 && "MMddyyyy".equalsIgnoreCase(format) == false) {
			try {
				format = "MMddyyyy";
				System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
				
				DateFormat f = new SimpleDateFormat(format);
				return f.parse(dateStr);
			} catch (Exception e) {
				format = "yyyyMMdd";
				System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
				
				DateFormat f = new SimpleDateFormat("yyyyMMdd");
				return f.parse(dateStr);
			}
		}
		
		if (dateStr.length() > 11 && format.contains("yyyy-MMM-dd") ) {
			format = "yyyy-MMM-dd";
			System.out.println("Transforming date [" + dateStr + "] to [" + format + "].");
			
			DateFormat f = new SimpleDateFormat(format);
			return f.parse(dateStr);
		}
		
		DateFormat f = new SimpleDateFormat(format);
		return f.parse(dateStr);
	}

	public static Date addDaysToDate(Date date, int numberOfDays) {

		Calendar cal = new GregorianCalendar();

		cal.setTime(date);

		cal.add(Calendar.DATE, numberOfDays);

		return cal.getTime();
	}

	public static Date addMonthsToDate(Date date, int numMonths) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, numMonths);

		return cal.getTime();
	}
	
	public static Date addYearsToDate(Date date, int numYears) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, numYears);

		return cal.getTime();
	}

	public static Date subtractMonthsToDate(Date date, int numMonths) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.MONTH, (numMonths < 0 ? numMonths: (numMonths * -1) ) );

		return cal.getTime();
	}
	
	public static Date subtractDaysToDate(Date date, int numberOfDays) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, (numberOfDays < 0 ? numberOfDays: (numberOfDays * -1) ) );

		return cal.getTime();
	}
	
	public static Date subtractYearsToDate(Date date, int numberOfYears) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.YEAR, (numberOfYears < 0 ? numberOfYears: (numberOfYears * -1) ) );
		return cal.getTime();
	}

	/**
	 * Converts java.util.Date to java.util.Calendar
	 * */
	public static Calendar dateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(date);

		return cal;
	}

	/**
	 * Returns the previous workings
	 * */
	public static Calendar workingDaysBack(final Calendar from, final int count) {
		for (int daysBack = 0; daysBack < count; ++daysBack) {
			do {
				from.add(Calendar.DAY_OF_YEAR, -1);
			} while (isWeekend(from));
		}
		return from;
	}

	/**
	 * Returns true if @param cal is weekend
	 * */
	public static boolean isWeekend(Calendar cal) {
		return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
				|| cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
	}

	public static String dateToString(String format, Date date) {

		if (format == null || date == null) {
			return null;
		}

		return new SimpleDateFormat(format).format(date);
	}
	
	public static String changeDateStringFormat(String dateStr, String curFormat, String newFormat) throws ParseException {
		if (curFormat == dateStr) {
			return null;
		}
		
		if (curFormat == null) {
			return null;
		}
		
		if (newFormat == null) {
			return null;
		}
		
		Date date = new SimpleDateFormat(curFormat).parse(dateStr);
		
		return new SimpleDateFormat(newFormat).format(date);
	}

	public static Date formatDate(String format, Date date)
			throws ParseException {

		if (format == null) {
			return null;
		}

		String tempDateStr = dateToString(format, date);

		return stringToDate(format, tempDateStr);
	}

	/**
	 * Sample usage: Utilities.stringToDate("MM/dd/yyyy", "09/06/2006")
	 * DateUtility.stringToDate("MM/dd/yyyy", dtStr);
	 * DateUtility.stringToDate("MM/dd/yyyy HH:mm", dtStr);
	 * DateUtility.stringToDate("MM/dd/yyyy HH:mm:ss", dtStr);
	 * */
	public static Date stringToDate(String format, String dateStr)
			throws ParseException {

		if (format == null || dateStr == null) {
			return null;
		}

		return new SimpleDateFormat(format).parse(dateStr);
	}

	public static Date stringToDate(String dateStr) throws ParseException {
		if (dateStr == null) {
			return null;
		}

		dateStr = dateStr.trim();

		Date date = null;

		if (StringUtil.isNumeric(dateStr)) {
			date = new Date(Long.valueOf(dateStr));
		} else {
			if (dateStr.contains("/")) {
				if (dateStr.length() == 10) {
					date = DateUtility.stringToDate("MM/dd/yyyy", dateStr);
				}
				if (dateStr.length() == 11 && dateStr.startsWith("0")) {
					date = DateUtility.stringToDate("MM/dd/yyyy",
							dateStr.substring(1));
				} else if (dateStr.length() == 16) {
					date = DateUtility
							.stringToDate("MM/dd/yyyy HH:mm", dateStr);
				} else if (dateStr.length() == 19) {
					date = DateUtility.stringToDate("MM/dd/yyyy HH:mm:ss",
							dateStr);
				}
			} else if (dateStr.contains("-") ) {
				if (dateStr.length() == 10) {
					date = DateUtility.stringToDate("MM-dd-yyyy", dateStr);
				} else if (dateStr.length() == 16) {
					date = DateUtility
							.stringToDate("MM-dd-yyyy HH:mm", dateStr);
				} else if (dateStr.length() == 19) {
					date = DateUtility.stringToDate("MM-dd-yyyy HH:mm:ss",
							dateStr);
				}
			} else {
				if (dateStr.length() == 8) {
					date = DateUtility.stringToDate("MMddyyyy", dateStr);
				}
			}
		}

		return date;
	} // End method

	/**
	 * Returns 0 if date1 and date2 are equal. -1 if date1 is less than date2 1
	 * if date1 is greater than date2
	 * */
	
	public static int compare(java.sql.Date date1, java.sql.Date date2) {
		return compare(convertDate(date1), convertDate(date2) );
	}
	
	public static int compare(Date date1, Date date2) {

		if (date1 == null) {
			if (date2 == null) {
				return 0;
			}
		}

		if (date1 == null) {
			if (date2 != null) {
				return 1;
			}
		}

		if (date2 == null) {
			if (date1 != null) {
				return 1;
			}
		}

		long date1Long = Long.valueOf(converUtilDateToJulian(date1));
		long date2Long = Long.valueOf(converUtilDateToJulian(date2));

		if (date1Long == date2Long) {
			return 0;
		} else if (date1Long < date2Long) {
			return -1;
		} else {
			return 1;
		}
	} // End method

	public static Date getFirstDayOfTheCurrentMonth() {
		Calendar c = Calendar.getInstance(); // this takes current date

		c.set(Calendar.DAY_OF_MONTH, 1);

		return c.getTime();
	} // End method

	public static Date getLastDayOfTheGivenMonth(Date givenMonth) {
		Calendar c = Calendar.getInstance();
		c.setTime(givenMonth);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));

		return c.getTime();
	}

	public static Date getFirstDayOfTheGivenMonth(Date givenMonth) {

		if (givenMonth == null) {
			return DateUtility.getFirstDayOfTheCurrentMonth();
		}

		Calendar c = Calendar.getInstance(); // this takes current date

		c.setTime(givenMonth);

		c.set(Calendar.DAY_OF_MONTH, 1);

		return c.getTime();
	} // End method

	public static String getCurrentTime(String format) throws ParseException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		
		return sdf.format(date);
	}
	
    public static boolean isValidTimeString(String format, String timeStr) {
		
		if (format == null || format == "") {
			return false;
		}

		if (timeStr == null || timeStr == "") {
			return false;
		}

		try {
			DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern(format)
		            .withResolverStyle(ResolverStyle.STRICT);
			
			LocalTime.parse(timeStr, strictTimeFormatter);
	    } catch (DateTimeParseException e) {
	        return false;
	    }

		return true;
	}
    
    public static String convertTimeFromHHMMAA(String previousFormat, String newFormat, String timeStr) throws ParseException {
    	if (previousFormat == null) {
    		return null;
    	}
    	
    	if (newFormat == null) {
    		return null;
    	}
    	
    	if (timeStr == null) {
    		return null;
    	}
    	
    	SimpleDateFormat displayFormat = new SimpleDateFormat(newFormat);
	    SimpleDateFormat parseFormat = new SimpleDateFormat(previousFormat);
	    Date date = parseFormat.parse(timeStr);
	    
		return displayFormat.format(date).toString();
	}
    
  
}
