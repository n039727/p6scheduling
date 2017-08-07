/**
 * 
 */
package au.com.wp.corp.p6.integration.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to provide data type conversion
 * 
 * @author N039126
 * @version 1.0
 */
public class P6Utility {

	private static final Logger logger = LoggerFactory.getLogger(P6Utility.class);

	private static final String ELLIPSE_STREET_NAME_PATTERN = "ELLIPSE_STREET_NAME_PATTERN";

	private P6Utility() {
	}

	/**
	 * converts String to double
	 * 
	 * @param value
	 * @return
	 */
	public static Double covertStringToDouble(String value) {
		try {
			return Double.valueOf(value);
		} catch (NumberFormatException e) {
			logger.error("An error occurs while converting string to double - ", e);

		}

		return 0D;
	}

	public static boolean isEqual(double value1, double value2) {

		if (Double.compare(value1, value2) == 0)
			return true;

		return false;

	}

	/**
	 * converts String to long
	 * 
	 * @param value
	 * @return
	 */
	public static Long covertStringToLong(String value) {
		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			logger.error("An error occurs while converting string to long - ", e);
		}

		return 0l;
	}

	/**
	 * converts String to integer
	 * 
	 * @param value
	 * @return
	 */
	public static Integer covertStringToInteger(String value) {
		try {
			return Integer.valueOf(value);
		} catch (NumberFormatException e) {
			logger.error("An error occurs while converting string to integer - ", e);
		}
		return 0;
	}

	/**
	 * converts Double to String
	 * 
	 * @param value
	 * @return
	 */
	public static String covertDoubleToString(Double value) {
		return String.valueOf(value);
	}

	/**
	 * converts Long to String
	 * 
	 * @param value
	 * @return
	 */
	public static String covertLongToString(Long value) {
		return String.valueOf(value);

	}

	/**
	 * converts Integer to String
	 * 
	 * @param value
	 * @return
	 */
	public static String covertIntegerToString(Integer value) {
		return String.valueOf(value);

	}

	/**
	 * extract the street name
	 * 
	 * @param street
	 * @return
	 */
	public static String getStreetName(final String street) {
		if (null == street) {
			return "";
		}
		final String ellipseStreetNamePattern = "(\\b[a-zA-Z]{3,}\\b)";
		final Pattern pattern = Pattern.compile(ellipseStreetNamePattern);
		final Matcher matcher = pattern.matcher(street);

		if (matcher.find()) {
			return street.substring(street.indexOf(matcher.group()));
		} else {
			return street;
		}

	}

}
