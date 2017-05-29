/**
 * 
 */
package au.com.wp.corp.p6.businessservice;

import org.hibernate.Session;

import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6ExceptionMapper;

/**
 * @author n039727
 * @version 1.0
 *
 */
@FunctionalInterface
public interface P6BusinessExceptionParser extends P6ExceptionMapper{
	
	Session getSession();
	
	default void parseException (Exception exc) throws P6BusinessException {
	
		if ( exc instanceof IllegalArgumentException ){
			throw new P6BusinessException (ARGUEMENT_MISMATCH, exc);
		}
	}
	
}
