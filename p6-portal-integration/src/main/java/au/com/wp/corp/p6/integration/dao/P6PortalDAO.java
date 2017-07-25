/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import java.util.List;
import java.util.Map;

import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;

/**
 * @author N039126
 *
 */
public interface P6PortalDAO extends P6DAOExceptionParser{

	public List<Task> getALlTasks() throws P6DataAccessException;

	Task saveTask(Task task) throws P6DataAccessException;

	public boolean createOrUpdateExecPackage(ExecutionPackage exectionPkg) throws P6DataAccessException;

	Map<String, List<String>> fetchAllResourceDetail() throws P6DataAccessException;

	void removeTask(Task task) throws P6DataAccessException;
	
}
