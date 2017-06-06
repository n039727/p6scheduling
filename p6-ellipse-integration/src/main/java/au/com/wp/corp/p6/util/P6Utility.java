/**
 * 
 */
package au.com.wp.corp.p6.util;

/**
 * Utility class to provide data type conversion
 * 
 * @author N039126
 * @version 1.0
 */
public class P6Utility {

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

		}

		return 0D;
	}

	public static boolean isEqual(double value1, double value2) {

		if (value1 == value2)
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
		try {
			return String.valueOf(value);
		} catch (Exception e) {

		}

		return null;
	}

	/**
	 * converts Long to String
	 * 
	 * @param value
	 * @return
	 */
	public static String covertLongToString(Long value) {
		try {
			return String.valueOf(value);
		} catch (Exception e) {

		}

		return null;
	}

	/**
	 * converts Integer to String
	 * 
	 * @param value
	 * @return
	 */
	public static String covertIntegerToString(Integer value) {
		try {
			return String.valueOf(value);
		} catch (Exception e) {

		}

		return null;
	}

}
