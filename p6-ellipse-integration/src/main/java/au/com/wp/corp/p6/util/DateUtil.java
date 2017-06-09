/**
 * 
 */
package au.com.wp.corp.p6.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(date);

	}

	public String convertDateToString(String date, String expectedDateFormat, String proviedDateFormat) {
		if (null == date || date.trim().isEmpty())
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat(expectedDateFormat);
		return sdf.format(convertStringToDatetime(date, proviedDateFormat));

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

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			logger.error("Invalid date - cant parse date# {} ", date);

		}
		return null;

	}

	public Date convertStringToDate(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

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
		Date _date = convertStringToDate(date);
		if (null != _date && _date.equals(convertStringToDate(new Date())))
			return true;

		return false;

	}

	public XMLGregorianCalendar convertStringToXMLGregorianClalander(final String date) {
		try {
			Date dob = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
			dob = df.parse(date);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(dob);
			XMLGregorianCalendar xmlDate2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
			return xmlDate2;
		} catch (ParseException e) {
			logger.error("Invalid date -- {} , can't covert to XMLGregorianCalendar", date);
		} catch (DatatypeConfigurationException e) {
			logger.error("Invalid date -- {} , can't covert to XMLGregorianCalendar", date);
		} catch (Exception e ) {
			logger.error("Invalid date -- {} , can't covert to XMLGregorianCalendar", date);
		}

		return null;

	}

}