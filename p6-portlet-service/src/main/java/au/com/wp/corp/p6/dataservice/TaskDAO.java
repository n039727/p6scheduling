package au.com.wp.corp.p6.dataservice;

import java.util.List;

import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;

/**
 * performs database operation on P6PORTAL.TASK table.It retrieves all the task,
 * list of execution packages and also it performs save the execution packages
 * 
 * @author n039126
 * @version 1.0
 */

public interface TaskDAO extends P6DAOExceptionParser {
	/**
	 * returns list of tasks from the task table
	 * 
	 * @return {@link List<Task>}
	 * @throws P6DataAccessException
	 */
	public List<Task> listTasks() throws P6DataAccessException;
	/**
	 * 
	 * @return
	 * @throws P6DataAccessException
	 */
	public List<ExecutionPackage> listExecutionPackages() throws P6DataAccessException;
	
}
