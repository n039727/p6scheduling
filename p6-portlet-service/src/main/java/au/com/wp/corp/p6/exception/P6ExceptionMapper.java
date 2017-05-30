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
	
	public String CREATE_EXEC_PCKG_VALIDATION_ERROR_2001 = "CREATE_EXEC_PCKG_VALIDATION_ERROR_2001";
	
	public String DB_LOOKUP_OR_UPDATE_ERROR_2001 = "DB_LOOKUP_OR_UPDATE_ERROR_2001";
	
	public String ARGUEMENT_MISMATCH = "INCORRECT_ARGUEMENT_2001";
			
	
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
