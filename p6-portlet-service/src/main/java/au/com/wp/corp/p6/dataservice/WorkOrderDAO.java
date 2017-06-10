package au.com.wp.corp.p6.dataservice;

import java.util.Date;
import java.util.List;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.Task;

public interface WorkOrderDAO extends P6DAOExceptionParser {

	List<Task> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query) throws P6DataAccessException;
	Task saveTask(Task task) throws P6DataAccessException;
	Task fetch(String workOrderId) throws P6DataAccessException;
	List<Task> fetchTasks(List<String> workOrderId) throws P6DataAccessException;
	List<Task> fetchTasks(WorkOrderSearchRequest query, List<WorkOrder> listWOData) throws P6DataAccessException;
	List<Task> fetchTasksByOnlyDate(List<Date> dateRange) throws P6DataAccessException;
	List<Task> fetchTasksByDateAndWo(List<Date> dateRange, List<String> workOrderId)
			throws P6DataAccessException;
	Task saveTaskForExecutionPackage(Task task) throws P6DataAccessException;
	
}
