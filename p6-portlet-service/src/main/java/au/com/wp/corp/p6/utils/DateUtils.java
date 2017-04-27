/**
 * 
 */
package au.com.wp.corp.p6.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author n039619
 *
 */
public class DateUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
	private static final SimpleDateFormat DATE_FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public static String toStringYYYY_MM_DD(Date dt) {
		return DATE_FORMAT_YYYY_MM_DD.format(dt);
	}
	
	public static Date toDateFromYYYY_MM_DD(String dtString) {
		Date dt = null;
		try {
			dt = DATE_FORMAT_YYYY_MM_DD.parse(dtString);
		} catch (ParseException e) {
			logger.error("Error parsing date: " + dtString, e);
		}
		return dt;
	}
	
}
