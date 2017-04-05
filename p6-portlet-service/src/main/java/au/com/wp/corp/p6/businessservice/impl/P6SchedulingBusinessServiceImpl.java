package au.com.wp.corp.p6.businessservice.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSerachInput;

@Service
public class P6SchedulingBusinessServiceImpl implements P6SchedulingBusinessService{

	Map<String,WorkOrder> mapStorage = null; 
	
	public List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input){
		List<WorkOrder>  workOrders = new ArrayList<WorkOrder>();
		/* this code will be replaced will the actual P6 Service call */
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
		/* this code will be replaced will the actual P6 Service call */
		return workOrders;
		
	}

	public List<WorkOrder> retrieveJobs(WorkOrderSerachInput input){
		List<WorkOrder>  workOrders = new ArrayList<WorkOrder>();
		
		/* this code will be replaced will the actual P6 Service call */
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
		/* this code will be replaced will the actual P6 Service call */
		workOrders.add(workOrder3);
		
		return workOrders;
		
	}

	public List<WorkOrder> saveWorkOrder(WorkOrder workOrder) {
		System.out.println(workOrder);
		
		if(mapStorage == null || mapStorage.size() < 1){
    		mapStorage = new HashMap<String, WorkOrder>();
    		mapStorage.put("1", workOrder);
    	}else{
    		String key = String.valueOf(mapStorage.size() + 1);
    		mapStorage.put(key, workOrder);
    	}
    	List<WorkOrder> listofJobs = new ArrayList<WorkOrder>(mapStorage.values());
    	
    	return listofJobs;
	}

	

	

}
