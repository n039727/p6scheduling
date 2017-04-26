package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.exception.P6BusinessException;


public interface P6SchedulingBusinessService {
	
	public List<WorkOrder> retrieveJobs(WorkOrderSearchInput input);
	public List<WorkOrder> retrieveWorkOrders(WorkOrderSearchInput input);
	public List<WorkOrder> saveWorkOrder(WorkOrder workOrder);
	public List<TaskDTO> listTasks() throws P6BusinessException;
	public List<ToDoItem> fetchToDos();
	public List<ViewToDoStatus> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchInput query);
	public WorkOrder saveToDo(WorkOrder workOrder);
	public List<ExecutionPackageDTO> fetchExecutionPackageList();
	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO) throws P6BusinessException;
	public List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(WorkOrderSearchInput body);
	public ViewToDoStatus saveViewToDoStatus(ViewToDoStatus body);

}
