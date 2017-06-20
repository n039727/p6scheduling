/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import org.hibernate.exception.ConstraintViolationException;

import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.integration.exception.P6ExceptionMapper;

/**
 * @author n039126
 * @version 1.0
 *
 */
public interface P6IntegrationDAOExceptionParser extends P6ExceptionMapper{
	
	
	default void parseException (Exception exc) throws P6DataAccessException {
		if ( exc instanceof ConstraintViolationException ){
			throw new P6DataAccessException (UNIQUE_CONSTRAINT_VIOLATION_1001, exc);
		}
		
		if ( exc instanceof Exception ){
			throw new P6DataAccessException (DB_LOOKUP_OR_UPDATE_ERROR_2001, exc);
		}
	}
	
}
