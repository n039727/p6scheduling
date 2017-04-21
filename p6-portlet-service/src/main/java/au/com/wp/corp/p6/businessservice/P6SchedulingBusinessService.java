package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;


public interface P6SchedulingBusinessService {
	
	List<WorkOrder> retrieveJobs(WorkOrderSearchInput input);
	List<WorkOrder> retrieveWorkOrders(WorkOrderSearchInput input);
	List<WorkOrder> saveWorkOrder(WorkOrder workOrder);
	List<TaskDTO> listTasks();
	List<ToDoItem> fetchToDos();
	List<ViewToDoStatus> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchInput query);
	WorkOrder saveToDo(WorkOrder workOrder);
	List<ExecutionPackageDTO> fetchExecutionPackageList();
	ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO);
	List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(WorkOrderSearchInput body);

}
