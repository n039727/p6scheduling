/**
 * 
 */
package au.com.wp.corp.p6.scheduling.exception;

/**
 * @author n039126
 *
 */
public class P6DataAccessException extends P6BusinessException {

	/**
	 * 
	 */
	public P6DataAccessException() {
		super();
	}

	/**
	 * @param message
	 */
	public P6DataAccessException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public P6DataAccessException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public P6DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public P6DataAccessException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
