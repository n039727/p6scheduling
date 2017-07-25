/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;

import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.integration.exception.P6ExceptionMapper;
import au.com.wp.corp.p6.integration.exception.P6ExceptionType;

/**
 * @author n039126
 * @version 1.0
 *
 */
@FunctionalInterface
public interface P6DAOExceptionParser extends P6ExceptionMapper{
	
	Session getSession();
	
	default void parseException (Exception exc) throws P6DataAccessException {
		if ( exc instanceof ConstraintViolationException ){
			throw new P6DataAccessException (UNIQUE_CONSTRAINT_VIOLATION_1001, exc);
		}
		if ( exc instanceof JDBCConnectionException ){
			throw new P6DataAccessException(P6ExceptionType.SYSTEM_ERROR.name(), exc.getCause());
		}
		if ( exc instanceof HibernateException ){
			throw new P6DataAccessException (DB_LOOKUP_OR_UPDATE_ERROR_2001, exc);
		}
	}
	
}
