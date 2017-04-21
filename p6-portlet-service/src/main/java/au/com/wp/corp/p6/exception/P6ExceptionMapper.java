/**
 * 
 */
package au.com.wp.corp.p6.exception;

/**
 * @author n039126
 *
 */
public interface P6ExceptionMapper {
	
	public String UNIQUE_CONSTRAINT_VIOLATION_1001 = "UNIQUE_CONSTRAINT_VIOLATION_1001";
	
	public String NETWORK_CONNECTION_ERROR_1002 = "NETWORK_CONNECTION_ERROR_1002";
	
	public String LOCK_ACQUIRING_ERROR_1003 = "LOCK_ACQUIRING_ERROR_1003";
	
	public String LOCK_TIMEOUT_ERROR_1004 = "LOCK_TIMEOUT_ERROR_1004";
			
	
//	throw new P6DataAccessException(UNIQUE_CONSTRAINT_VIOLATION_1001, e);			
//			
//	ConstraintViolationException
//	JDBCConnectionException
//	LockAcquisitionException
//	DataException
//	LockTimeoutException
//	GenericJDBCException
//	SQLGrammarException
//	IOException
	
	
	
}
