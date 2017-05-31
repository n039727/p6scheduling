/**
 * 
 */
package au.com.wp.corp.p6.businessservice;

import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6ExceptionMapper;
import au.com.wp.corp.p6.exception.P6ServiceException;

/**
 * @author n039727
 * @version 1.0
 *
 */
public interface P6BusinessExceptionParser extends P6ExceptionMapper{
	
	
	default void parseException (Exception throwable) throws P6BusinessException {
	
		if ( throwable instanceof IllegalArgumentException ){
			throw new P6BusinessException (ARGUEMENT_MISMATCH, throwable);
		}
		if ( throwable instanceof IndexOutOfBoundsException ){
			throw new P6BusinessException (ARGUEMENT_MISMATCH, throwable);
		}
		if ( throwable instanceof P6ServiceException ){
			throw new P6BusinessException (INTEGRATION_FAULT, throwable);
		}
	}
	
}
