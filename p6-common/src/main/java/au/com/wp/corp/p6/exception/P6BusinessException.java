/**
 * 
 */
package au.com.wp.corp.p6.exception;

/**
 * @author n039126
 *
 */
public class P6BusinessException extends P6BaseException {

	/**
	 * 
	 */
	public P6BusinessException() {
		super();
	}

	/**
	 * @param message
	 */
	public P6BusinessException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public P6BusinessException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public P6BusinessException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public P6BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
