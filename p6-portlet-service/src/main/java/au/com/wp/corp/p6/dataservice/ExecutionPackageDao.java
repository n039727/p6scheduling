/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;

/**
 * @author n039619
 *
 */
public interface ExecutionPackageDao extends P6DAOExceptionParser {

	ExecutionPackage fetch(String name);

	/**
	 * 
	 * @param executionPackageDTO
	 * @return
	 * @throws P6DataAccessException
	 */
	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO);

	/**
	 * returns work order details by work order id
	 * 
	 * @param taskId
	 * @return
	 * @throws P6DataAccessException
	 */
	public Task getTaskbyId(String taskId) throws P6DataAccessException;

	/**
	 * insert a record to execution package table and update the task with new
	 * execution package id
	 * 
	 * @param executionPackage
	 * @return ExecutionPackageDTO
	 * @throws P6DataAccessException
	 */
	public boolean createOrUpdateExecPackage(ExecutionPackage executionPackage)
			throws P6DataAccessException;

}
