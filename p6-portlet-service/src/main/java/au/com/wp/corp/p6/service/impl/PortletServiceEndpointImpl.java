/**
 * 
 */
package au.com.wp.corp.p6.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.service.PortletServiceEndpoint;
import au.com.wp.corp.p6.validation.Validator;

/**
 * @author n039619
 *
 */
@RestController
@RequestMapping(value="/scheduler")
public class PortletServiceEndpointImpl implements PortletServiceEndpoint {
	
	private static final Logger logger = LoggerFactory.getLogger(PortletServiceEndpointImpl.class);
	@Autowired
	private P6SchedulingBusinessService p6BusinessService;
	@Autowired
	Validator validator;
	
	
	@RequestMapping(value="/fetchToDos", method = RequestMethod.GET,
			produces = {MediaType.APPLICATION_JSON_VALUE})
	@Override
	public List<ToDoItem> fetchToDoItems() {
		return p6BusinessService.fetchToDos();
	}
	
	@RequestMapping(value="/searchWorkOrder", method = RequestMethod.GET,
			produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
	@Override
	public List<WorkOrder> fetchWorkOrdersForAddUpdateScheduling(WorkOrderSearchRequest query) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@RequestMapping(value="/fetchWOForTODOStatus", method = RequestMethod.POST,
			produces = {MediaType.APPLICATION_JSON_VALUE}, 
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	@Override
	public List<ViewToDoStatus> fetchWorkOrdersForViewToDoStatus(RequestEntity<WorkOrderSearchRequest> query){
		logger.debug("DEPOT_ID>>>>{}", query.getBody().getDepotList());
		return p6BusinessService.fetchWorkOrdersForViewToDoStatus(query.getBody());
	}
	
	@RequestMapping(value="/fetchWOForAddUpdateToDo", method = RequestMethod.POST,
			produces = {MediaType.APPLICATION_JSON_VALUE}, 
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	@Override
	public List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(RequestEntity<WorkOrderSearchRequest> query){ 
		logger.debug("DEPOT_ID>>>>{}", query.getBody().getDepotList());
		return p6BusinessService.fetchWorkOrdersForAddUpdateToDo(query.getBody());
	}
	
	@RequestMapping(value = "/saveWorkOrder" , 
    		method = RequestMethod.POST, 
    		produces = {MediaType.APPLICATION_JSON_VALUE}, 
    		consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @Override
	public ResponseEntity<WorkOrder> saveWorkOrder(RequestEntity<WorkOrder> workOrder) throws P6BusinessException {
    	return new ResponseEntity<WorkOrder>(p6BusinessService.saveToDo(workOrder.getBody()),HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/saveWorkOrderForViewToDoStatus" , 
    		method = RequestMethod.POST, 
    		produces = {MediaType.APPLICATION_JSON_VALUE}, 
    		consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    @Override
	public ResponseEntity<ViewToDoStatus> saveViewToDoStatus(RequestEntity<ViewToDoStatus> viewToDoStatus) throws P6BusinessException {
    	return new ResponseEntity<ViewToDoStatus>(p6BusinessService.saveViewToDoStatus(viewToDoStatus.getBody()),HttpStatus.CREATED);
	}

	@RequestMapping(value="/saveExecutionPackages", method = RequestMethod.POST,
			produces = {MediaType.APPLICATION_JSON_VALUE}, 
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	@Override
	public ResponseEntity<ExecutionPackageDTO> saveExecutionPackages(RequestEntity<ExecutionPackageDTO> executionPackageDTO)throws P6BaseException{
		validator.validate(executionPackageDTO.getBody()); 
		return new ResponseEntity<ExecutionPackageDTO>(p6BusinessService.saveExecutionPackage(executionPackageDTO.getBody()),HttpStatus.CREATED);
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ResponseEntity<List<WorkOrder>> search(RequestEntity<WorkOrderSearchRequest> request)
			throws P6BaseException {
		if (request.getBody() == null) {
			logger.error(" Invalid request - {}", request.getBody());
			throw new P6BaseException(" invalid request ");
		}
		logger.info("Search String # crews - {} , start date - {}", request.getBody().getCrewList(),
				request.getBody().getFromDate());
		return new ResponseEntity<List<WorkOrder>>(p6BusinessService.search(request.getBody()), HttpStatus.OK);
	}
}