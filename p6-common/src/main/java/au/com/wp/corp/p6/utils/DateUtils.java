/**
 * 
 */
package au.com.wp.corp.p6.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author n039619
 *
 */

@Component
public class DateUtils {
	
	private  final Logger logger = LoggerFactory.getLogger(DateUtils.class);
	private final  SimpleDateFormat DATE_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
	
	private final  SimpleDateFormat DATE_FORMAT_DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy");
	
	private final  SimpleDateFormat DATE_FORMAT_DD_MM_YYYY_TIMESTAMP = new SimpleDateFormat("dd-MM-yyyy_hhmmssMs");
	
	private final  SimpleDateFormat DATE_FORMAT_DDMMYYYYTIMESTAMP = new SimpleDateFormat("ddMMyyyyhhmmssMs");
	
	private final  SimpleDateFormat DATE_FORMAT_DDMMYYYYT = new SimpleDateFormat("ddMMyyyy");
	
	
	public String convertDate ( String date )  {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("Exception while convertDate>>{}", e);
		}
		
		return DATE_FORMAT_YYYY_MM_DD.format(d);
	}
	
	public String convertDateDDMMYYYY ( String date )  {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("Exception while convertDate>>{}", e);
		}
		
		return DATE_FORMAT_DD_MM_YYYY.format(d);
	}
	public String convertDateDDMMYYYY ( String date ,String separator)  {
		SimpleDateFormat sdf = new SimpleDateFormat("dd"+separator+"MM"+separator+"yyyy");
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("Exception while convertDate>>{}", e);
		}
		
		return DATE_FORMAT_DD_MM_YYYY.format(d);
	}
	
	public String convertDateYYYYMMDD ( String date ,String separator)  {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy"+separator+"MM"+separator+"dd");
		Date d = new Date();
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			logger.error("Exception while convertDate>>{}", e);
		}
		
		return DATE_FORMAT_DD_MM_YYYY.format(d);
	}
	
	public  String toStringYYYY_MM_DD(Date dt) {
		return DATE_FORMAT_YYYY_MM_DD.format(dt);
	}
	
	public  String toStringDD_MM_YYYY(Date dt) {
		return DATE_FORMAT_DD_MM_YYYY.format(dt);
	}
	
	public  Date toDateFromYYYY_MM_DD(String dtString) {
		Date dt = null;
		try {
			dt = DATE_FORMAT_YYYY_MM_DD.parse(dtString);
		} catch (ParseException e) {
			logger.error("Error parsing date: " + dtString, e);
		}
		return dt;
	}
	
	
	public  String getCurrentDateWithTimeStamp (){
		return DATE_FORMAT_DD_MM_YYYY_TIMESTAMP.format(new Date());
		
	}
	
	public  String getCurrentDateWithTimeStampNoSeparator (){
		return DATE_FORMAT_DDMMYYYYTIMESTAMP.format(new Date());
		
	}
	
	
	public  Date toDateFromDD_MM_YYYY (final String dtString ){
		Date dt = null;
		try {
			dt = DATE_FORMAT_DD_MM_YYYY.parse(dtString);
		} catch (ParseException e) {
			logger.error("Error parsing date: " + dtString, e);
		}
		return dt;
	}
	
	public  Date toDateFromDDMMYYYY (final String dtString ){
		Date dt = null;
		try {
			dt = DATE_FORMAT_DDMMYYYYT.parse(dtString);
		} catch (ParseException e) {
			logger.error("Error parsing date: " + dtString, e);
		}
		return dt;
	}
	
}
