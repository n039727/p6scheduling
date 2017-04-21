/**
 * 
 */
package au.com.wp.corp.p6.exception;

/**
 * 
 *  
 * @author n039126
 *
 */
public class P6BaseException extends Exception {
	
	/**
	 * 
	 */
	public P6BaseException(){
		super();
	}
	/**
	 * 
	 * @param message
	 */
	public P6BaseException(String message) {
		super(message);
	}
	/**
	 * 
	 * @param cause
	 */
	public P6BaseException(Throwable cause){
		super(cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public P6BaseException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * 
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public P6BaseException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
