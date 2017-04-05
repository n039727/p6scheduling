package au.com.wp.corp.p6.businessservice.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSerachInput;

@Service
public class P6SchedulingBusinessServiceImpl implements P6SchedulingBusinessService{

	/*@Override
	public String getWelcomeMessage() {
		return "Good Afternoon Updated..Don't Worry Saikat "+Time.valueOf(LocalTime.now(
				// @formatter:on
		));
	}*/
	
	public List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input){
		List<WorkOrder>  workOrders = new ArrayList<WorkOrder>();
		
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setExecutionPackage("test execution 1");
		workOrder1.setLeadCrew("MOST1");
		workOrder1.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder1.setWorkOrders(Arrays.asList(new String[] {
				"Y6UIOP67" ,"Y6UIOP70" ,"Y6UIOP97"
		}));
		
		workOrders.add(workOrder1);
		WorkOrder workOrder2 = new WorkOrder();
		workOrder2.setExecutionPackage("test execution 1");
		workOrder2.setLeadCrew("MOST1");
		workOrder2.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder2.setWorkOrders(Arrays.asList(new String[] {
				"Y6UIOP67" ,"Y6UIOP70" ,"Y6UIOP97"
		}));
		
		workOrders.add(workOrder2);
		
		WorkOrder workOrder3 = new WorkOrder();
		workOrder3.setExecutionPackage("test execution 1");
		workOrder3.setLeadCrew("MOST1");
		workOrder3.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder3.setWorkOrders(Arrays.asList(new String[] {
				"Y6UIOP67" ,"Y6UIOP70" ,"Y6UIOP97"
		}));
		
		workOrders.add(workOrder3);
		
		return workOrders;
		
	}

	public List<WorkOrder> retrieveJobs(WorkOrderSerachInput input){
		List<WorkOrder>  workOrders = new ArrayList<WorkOrder>();
		
		WorkOrder workOrder1 = new WorkOrder();
		workOrder1.setLeadCrew("MOST1");
		workOrder1.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder1.setWorkOrders(Arrays.asList(new String[] {
				"Y6UIOP97"
		}));
		
		workOrders.add(workOrder1);
		WorkOrder workOrder2 = new WorkOrder();
		workOrder2.setLeadCrew("MOST2");
		workOrder2.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder2.setWorkOrders(Arrays.asList(new String[] {
				"Y6UIOP67"
		}));
		
		workOrders.add(workOrder2);
		
		WorkOrder workOrder3 = new WorkOrder();
		workOrder3.setLeadCrew("MOST3");
		workOrder3.setScheduleDate(String.valueOf(Date.valueOf(LocalDate.now())));
		workOrder3.setWorkOrders(Arrays.asList(new String[] {
				"Y6UIOP67"
		}));
		
		workOrders.add(workOrder3);
		
		return workOrders;
		
	}

	

	

}
