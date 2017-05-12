/**
 * 
 */
package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6DataAccessException;

/**
 * ExecutionPackageService performs following tasks regarding the execution
 * package such as a. createExecutionPackage b. updateExecutionPackage c.
 * mergeExecutionPackages d. splitExecutionPackage
 * 
 * 
 * @author n039126
 * @version 1.0
 */
public interface IExecutionPackageService {

	/**
	 * Updates the execution package with adding new job/jobs or removing
	 * existing job/jbos
	 * 
	 * @param execPackg
	 * @return ExecutionPackageDTO
	 * @throws P6BusinessException
	 */
	public ExecutionPackageDTO createOrUpdateExecutionPackage(ExecutionPackageDTO executionPackageDTO, String user) throws P6BusinessException;

	List<WorkOrder> searchByExecutionPackage(WorkOrderSearchRequest input) throws P6BaseException;

}
