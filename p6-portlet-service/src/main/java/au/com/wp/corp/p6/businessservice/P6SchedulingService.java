package au.com.wp.corp.p6.businessservice;

import java.util.List;

public interface P6SchedulingService {
	
	List<WorkOrder> retrieveJobs(WorkOrderSerachInput input);
	List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input);
}
