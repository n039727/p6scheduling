/**
 * 
 */
package au.com.wp.corp.p6.service;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;

/**
 * @author n039619
 *
 */
public interface PortletServiceEndpoint {
	
	List<WorkOrder> fetchWorkOrdersForAddUpdateScheduling(WorkOrderSearchInput query);
	List<WorkOrder> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchInput query);
	/*List<WorkOrder> fetchWorkOrdersForExecutionPackage(WorkOrderQuery query);*/
	ResponseEntity<WorkOrder> saveWorkOrder(RequestEntity<WorkOrder> workOrder);
	
	List<ToDoItem> fetchToDoItems();

}
