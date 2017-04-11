package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.businessservice.dto.TaskDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSerachInput;

public interface P6SchedulingBusinessService {
	
	List<WorkOrder> retrieveJobs(WorkOrderSerachInput input);
	List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input);
	List<WorkOrder> saveWorkOrder(WorkOrder workOrder);
	List<TaskDTO> listTasks();
}
