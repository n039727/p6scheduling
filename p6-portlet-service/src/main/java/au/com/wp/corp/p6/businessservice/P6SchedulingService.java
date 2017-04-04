package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.service.WorkOrder;

public interface P6SchedulingService {
	
	String getWelcomeMessage();
	
	List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input);
}
