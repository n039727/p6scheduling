package au.com.wp.corp.p6.service;

import java.util.List;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSerachInput;

public interface P6SchedulingAppService {

	List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input);

	List<WorkOrder> retrieveJobs(WorkOrderSerachInput input);

}
