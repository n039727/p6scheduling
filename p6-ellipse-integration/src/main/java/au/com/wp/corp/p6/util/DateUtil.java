/**
 * 
 */
package au.com.wp.corp.p6.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Component;

/**
 * @author N039126
 *
 */
@Component
public class DateUtil {

	private static final int FIRST_FISCAL_MONTH = Calendar.JULY;

	private Calendar calendarDate;

	public int getFiscalMonth() {
		int month = calendarDate.get(Calendar.MONTH);
		int result = ((month - FIRST_FISCAL_MONTH - 1) % 12) + 1;
		if (result < 0) {
			result += 12;
		}
		return result;
	}

	public int getFiscalYear() {
		int month = calendarDate.get(Calendar.MONTH);
		int year = calendarDate.get(Calendar.YEAR);
		return (month >= FIRST_FISCAL_MONTH) ? year : year - 1;
	}

	public int getCalendarMonth() {
		return calendarDate.get(Calendar.MONTH);
	}

	public int getCalendarYear() {
		return calendarDate.get(Calendar.YEAR);
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

	public String getStartDateOfFiscalYear(final String date) {
		this.calendarDate = Calendar.getInstance();
		this.calendarDate.setTime(convertStringToDatetime(date));
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
	
	public String convertDateToString(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return sdf.format(convertStringToDatetime(date));

	}

	public Date convertStringToDatetime(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

		try {
			return sdf.parse(date);
		} catch (ParseException e) {

		}
		return null;

	}
	
	public Date convertStringToDate(String date) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			return sdf.parse(date);
		} catch (ParseException e) {

		}
		return null;

	}
	
	
	public Date convertStringToDate(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			return sdf.parse(sdf.format(date));
		} catch (ParseException e) {

		}
		return null;

	}
	
	
	public String getCurrentDate () {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		return sdf.format(new Date());
	}
	
	
	public boolean isCurrentDate ( String date ){
		Date _date = convertStringToDate(date);
		if ( null != _date && _date.equals(convertStringToDate(new Date())))
			return true;
		
		return false;
		
	}

	private static void displayFinancialDate(Calendar calendar) {
		DateUtil fiscalDate = new DateUtil();
		System.out.println("Fiscal First Date : " + fiscalDate.getStartDateOfFiscalYear(calendar).getTime().toString());

		System.out.println(" ");
		
		System.out.println(fiscalDate.isCurrentDate("26/05/2017 08:00:00"));
	}
	
	
	public static void main(String[] args) {
		displayFinancialDate(Calendar.getInstance());
	}
}