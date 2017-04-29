package au.com.wp.corp.p6.dataservice;

import java.util.List;

import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.model.Task;

public interface WorkOrderDAO {

	List<Task> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query);
	Task saveTask(Task task);
	Task fetch(String workOrderId);
	
}
