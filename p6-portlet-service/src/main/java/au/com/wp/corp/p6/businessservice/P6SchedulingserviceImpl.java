package au.com.wp.corp.p6.businessservice;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.service.WorkOrder;

@Service
public class P6SchedulingserviceImpl implements P6SchedulingService{

	/*@Override
	public String getWelcomeMessage() {
		return "Good Afternoon Updated..Don't Worry Saikat "+Time.valueOf(LocalTime.now(
				// @formatter:on
		));
	}*/
	
	public List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input){
		
		WorkOrder workOrder = new WorkOrder();
		
		
		
		return null;
		
	}

	public String getWelcomeMessage() {
		return "Good Afternoon Updated..Don't Worry Saikat "+Time.valueOf(LocalTime.now(
				// @formatter:on
		));
	}

	

}
