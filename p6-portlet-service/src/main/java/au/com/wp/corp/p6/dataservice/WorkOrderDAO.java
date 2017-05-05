package au.com.wp.corp.p6.dataservice;

import java.util.List;

import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.Task;

public interface WorkOrderDAO extends P6DAOExceptionParser {

	List<Task> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query);
	Task saveTask(Task task) throws P6DataAccessException;
	Task fetch(String workOrderId) throws P6DataAccessException;
	
}
