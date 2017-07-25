/**
 * 
 */
package au.com.wp.corp.p6.integration.exception;

/**
 * @author n039727
 * @version 1.0
 *
 */
public interface P6BusinessExceptionParser extends P6ExceptionMapper{
	
	
	default void parseException (Exception throwable) throws P6BusinessException {
		if(throwable instanceof P6ServiceException){
			
		}
		if ( throwable instanceof P6ServiceException){
			throw new P6BusinessException(P6ExceptionType.SYSTEM_ERROR.name(), throwable.getCause());
		}
		if ( throwable instanceof P6BusinessException ){
			throw new P6BusinessException (throwable.getMessage(), throwable);
		}
	}
	
}
