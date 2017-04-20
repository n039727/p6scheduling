package au.com.wp.corp.p6.dataservice;

import java.util.List;

import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.model.Task;

public interface WorkOrderDAO {

	List<Task> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchInput query);
	Task saveTask(Task task);
	Task fetch(String workOrderId);
	
}
