/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;

/**
 * @author n039619
 *
 */
public interface ExecutionPackageDao {
	
	ExecutionPackage fetch(String name);
	/**
	 * 
	 * @param executionPackageDTO
	 * @return
	 * @throws P6DataAccessException
	 */
	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO)
			throws P6DataAccessException;

}
