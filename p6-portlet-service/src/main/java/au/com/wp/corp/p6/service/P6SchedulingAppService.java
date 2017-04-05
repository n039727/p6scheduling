package au.com.wp.corp.p6.service;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSerachInput;

public interface P6SchedulingAppService {

	public ResponseEntity<List<WorkOrder>> retrieveWorkOrders(RequestEntity<WorkOrderSerachInput> input);

	public ResponseEntity<List<WorkOrder>> retrieveJobs(RequestEntity<WorkOrderSerachInput> input);
	
	public ResponseEntity<List<WorkOrder>> saveWorkOrder(RequestEntity<WorkOrder> workOrder);

}
