/**
 * 
 */
package au.com.wp.corp.p6.service.impl;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.service.PortletServiceEndpoint;

/**
 * @author n039619
 *
 */
@RestController
public class PortletServiceEndpointImpl implements PortletServiceEndpoint {
	
	@Autowired
	private P6SchedulingBusinessService p6BusinessService;
	
	
	@RequestMapping(value="/schedulingToDo/searchWorkOrder", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@Override
	public List<WorkOrder> fetchWorkOrdersForAddUpdateScheduling(@RequestBody WorkOrderSearchInput query) {
		return p6BusinessService.retrieveJobs(query);
	}

	@RequestMapping(value="/fetchToDos", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@Override
	public List<ToDoItem> fetchToDoItems() {
		return p6BusinessService.fetchToDos();
	}
	
	@RequestMapping(value="/fetchWOForTODOStatus", method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
	@Override
	public List<WorkOrder> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchInput query){
		return null;
	}
	

}
