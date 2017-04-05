package au.com.wp.corp.p6.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSerachInput;
import au.com.wp.corp.p6.service.P6SchedulingAppService;

@RestController
public class P6SchedulingAppServiceImpl implements P6SchedulingAppService{

    @RequestMapping("/home")
    @ResponseBody
    String home() {
        return "Hello World!";
    }
    @Inject
    P6SchedulingAppService p6Service;

    
    @RequestMapping(value = "/retrieveWorkOrders" , 
    		method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
	public List<WorkOrder> retrieveWorkOrders(WorkOrderSerachInput input) {
    	 return p6Service.retrieveWorkOrders(null);
	}

    @RequestMapping(value = "/retrieveJobs" , 
    		method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
	public List<WorkOrder> retrieveJobs(WorkOrderSerachInput input) {
    	return p6Service.retrieveJobs(null);
	}

	
}

