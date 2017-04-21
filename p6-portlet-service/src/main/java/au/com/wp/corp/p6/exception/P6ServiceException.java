/**
 * 
 */
package au.com.wp.corp.p6.exception;

/**
 * @author n039126
 *
 */
public class P6ServiceException extends P6BusinessException {

	/**
	 * 
	 */
	public P6ServiceException() {
		super();
	}

	/**
	 * @param message
	 */
	public P6ServiceException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public P6ServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public P6ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public P6ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
