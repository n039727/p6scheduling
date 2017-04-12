package au.com.wp.corp.p6.service;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;

public interface P6SchedulingAppService {

	ResponseEntity<List<WorkOrder>> retrieveWorkOrders(RequestEntity<WorkOrderSearchInput> input);

	ResponseEntity<List<WorkOrder>> retrieveJobs(RequestEntity<WorkOrderSearchInput> input);
	
	ResponseEntity<List<WorkOrder>> saveWorkOrder(RequestEntity<WorkOrder> workOrder);
	
	ResponseEntity<List<TaskDTO>> listTasks();

}
