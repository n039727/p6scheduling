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
	
	
	public  Date toDateFromDD_MM_YYYY (final String dtString ){
		Date dt = null;
		try {
			dt = DATE_FORMAT_DD_MM_YYYY.parse(dtString);
		} catch (ParseException e) {
			logger.error("Error parsing date: " + dtString, e);
		}
		return dt;
	}
	
}
