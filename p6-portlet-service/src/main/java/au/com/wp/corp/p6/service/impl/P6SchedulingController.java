package au.com.wp.corp.p6.service.impl;

import java.util.List;

import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.exception.P6BusinessException;



@RestController
public class P6SchedulingController{
	private static final Logger logger = LoggerFactory.getLogger(P6SchedulingController.class);
   
	@Autowired
    P6SchedulingBusinessService p6Service;
    
    @RequestMapping(value = "/retrieveWorkOrders" , 
    		method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
	public ResponseEntity<List<WorkOrder>> retrieveWorkOrders(RequestEntity<WorkOrderSearchInput> input) {
    	 return new ResponseEntity<List<WorkOrder>>(p6Service.retrieveWorkOrders(null),HttpStatus.OK);
	}
    @RequestMapping(value = "/retrieveJobs" , 
    		method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
	public ResponseEntity<List<WorkOrder>> retrieveJobs(RequestEntity<WorkOrderSearchInput> input) {
    	return new ResponseEntity<List<WorkOrder>>(p6Service.retrieveJobs(null),HttpStatus.OK);
	}
    @RequestMapping(value = "/saveWorkOrder" , 
    		method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
    		consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
	public ResponseEntity<List<WorkOrder>> saveWorkOrder(RequestEntity<WorkOrder> workOrder) {
    	return new ResponseEntity<List<WorkOrder>>(p6Service.saveWorkOrder(workOrder.getBody()),HttpStatus.CREATED);
	}
    @RequestMapping(value = "/listTasks" , 
    		method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
   	public ResponseEntity<List<TaskDTO>> listTasks() throws P6BaseException {
       	try {
			return new ResponseEntity<List<TaskDTO>>(p6Service.listTasks(),HttpStatus.CREATED);
		} catch (P6BusinessException exc) {
			logger.error("An error occurs while retrieving list of tasks ", exc);
			throw new P6BaseException(exc);
		}
   	}
	
}

