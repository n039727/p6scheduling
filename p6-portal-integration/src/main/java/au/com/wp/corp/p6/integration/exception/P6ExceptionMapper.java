/**
 * 
 */
package au.com.wp.corp.p6.integration.exception;

/**
 * @author n039126
 *
 */
public interface P6ExceptionMapper {
	
	public String UNIQUE_CONSTRAINT_VIOLATION_1001 = "UNIQUE_CONSTRAINT_VIOLATION_1001";
	
	public String NETWORK_CONNECTION_ERROR_1002 = "NETWORK_CONNECTION_ERROR_1002";
	
	public String DB_LOOKUP_OR_UPDATE_ERROR_2001 = "DB_LOOKUP_OR_UPDATE_ERROR_2001";
	
	public String INTEGRATION_FAULT = "INTEGRATION_FAULT_2001";
			
	
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
