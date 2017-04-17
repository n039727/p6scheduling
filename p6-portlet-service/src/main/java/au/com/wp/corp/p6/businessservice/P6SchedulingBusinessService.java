package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;

public interface P6SchedulingBusinessService {
	
	List<WorkOrder> retrieveJobs(WorkOrderSearchInput input);
	List<WorkOrder> retrieveWorkOrders(WorkOrderSearchInput input);
	List<WorkOrder> saveWorkOrder(WorkOrder workOrder);
	List<TaskDTO> listTasks();
	List<ToDoItem> fetchToDos();
	WorkOrder saveToDo(WorkOrder workOrder);
}
