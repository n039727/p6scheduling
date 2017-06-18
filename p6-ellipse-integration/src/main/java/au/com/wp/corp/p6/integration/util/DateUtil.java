/**
 * 
 */
package au.com.wp.corp.p6.integration.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author N039126
 *
 */
@Component
public class DateUtil {
	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	private static final int FIRST_FISCAL_MONTH = Calendar.JULY;
	
	public static final String DATE_FORMAT_DDMMYYYY = "dd/MM/yyyy";

	public static final String ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP = "dd/MM/yyyy hh:mm:ss";

	public static final String P6_DATE_FORMAT_WITH_TIMESTAMP = "yyyy-MM-dd'T'hh:mm:ss";

	public static final String ELLIPSE_DATE_FORMAT = "yyyyMMdd";

	private Calendar calendarDate;

	public int getFiscalYear() {
		int month = calendarDate.get(Calendar.MONTH);
		int year = calendarDate.get(Calendar.YEAR);
		return (month >= FIRST_FISCAL_MONTH) ? year : year - 1;
	}

	public Calendar getStartDateOfFiscalYear(final Calendar calendarDate) {
		this.calendarDate = calendarDate;
		int year = getFiscalYear();
		return setDate(year, FIRST_FISCAL_MONTH, 1);
	}

	public String getStartDateOfFiscalYear(final Date date) {
		this.calendarDate = Calendar.getInstance();
		this.calendarDate.setTime(date);
		return convertDateToString(getStartDateOfFiscalYear(calendarDate).getTime());
	}

	public String getStartDateOfFiscalYear(final String date, String expectedDateFormat) {
		this.calendarDate = Calendar.getInstance();
		this.calendarDate.setTime(convertStringToDatetime(date, expectedDateFormat));
		return convertDateToString(getStartDateOfFiscalYear(calendarDate).getTime());
	}

	private static Calendar setDate(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		return calendar;
	}

	public String convertDateToString(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat(P6_DATE_FORMAT_WITH_TIMESTAMP);
		return sdf.format(date);

	}

	public String convertDateToString(String date, String expectedDateFormat, String proviedDateFormat) {
		if (null == date || date.trim().isEmpty())
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(expectedDateFormat);
		try {
			return sdf.format(convertStringToDatetime(date, proviedDateFormat));
		} catch (Exception e) {
			logger.error("Invalid date - cant parse date# {}  - proviedDateFormat# {}  - expectedDateFormat# {} - the exception# {}", date,
					proviedDateFormat, expectedDateFormat, e);
		}
		return null;

	}

	public Date convertStringToDatetime(String date, String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);

		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			logger.error("Invalid date - cant parse date# {}  - format# {}", date, format);
		}
		return null;

	}

	public Date convertStringToDate(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);

		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			logger.error("Invalid date - cant parse date# {} ", date);

		}
		return null;

	}

	public Date convertStringToDate(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);

		try {
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) {
			logger.error("Invalid date - cant parse date# {} ", date);
		}
		return null;

	}

	public String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(P6_DATE_FORMAT_WITH_TIMESTAMP);
		return sdf.format(new Date());
	}

	public boolean isCurrentDate(String date) {
		Date dt = convertStringToDate(date);
		if (null != dt && dt.equals(convertStringToDate(new Date())))
			return true;

		return false;

	}

	public XMLGregorianCalendar convertStringToXMLGregorianClalander(final String date) {
		try {
			Date dt;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
			dt = df.parse(date);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(dt);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
		} catch (Exception e) {
			logger.error("Invalid date -- {} , can't covert to XMLGregorianCalendar", date);
			logger.error("Can't covert to XMLGregorianCalendar - ", e);
		}
		return null;

	}
	
	
	
	public boolean isSameDate (String date1, String date1Format, String date2, String date2Format) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(date1Format);
		
		SimpleDateFormat sdf2 = new SimpleDateFormat(date2Format);
		
		SimpleDateFormat sdf3 = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);
		
		try {
			Date dt1 =  sdf3.parse(sdf3.format(sdf1.parse(date1)));
			Date dt2 = sdf3.parse(sdf3.format(sdf2.parse(date2)));
			
			if ( dt1.compareTo(dt2) == 0)
			{
				return true;
			}
		} catch (Exception e) {
			logger.error("Invalid date - cant parse date1# {}  - date2# {}", date1, date2);
			logger.error("Can't parse input date - ", e);
		}
		
		return false;
	}


	
	public String substractMinuteFromDate ( String date, String format){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date dt = sdf.parse(date);
			Calendar calender = Calendar.getInstance();
			calender.setTime(dt);
			calender.add(Calendar.MINUTE, -1);
			Date newDt = calender.getTime();
			
			return sdf.format(newDt);
			
		} catch (ParseException e) {
			logger.error("Invalid date - cant parse date# {}  - format# {}", date, format);
			logger.error("Can't parse input date - ", e);
		}
		
		return null;
		
	}
	
	public int compare(String date1, String date1Format, String date2, String date2Format) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(date1Format);
		SimpleDateFormat sdf2 = new SimpleDateFormat(date2Format);
		
		try {
			Date dt1 = sdf1.parse(date1);
			Date dt2 = sdf2.parse(date2);
			return dt1.compareTo(dt2) ;
			
		} catch (Exception e) {
			logger.error("Invalid date - cant parse date1# {}  - date2# {}", date1, date2);
			logger.error("Can't parse input date - ", e);
		}
		
		return -1;
		
	}
	
}