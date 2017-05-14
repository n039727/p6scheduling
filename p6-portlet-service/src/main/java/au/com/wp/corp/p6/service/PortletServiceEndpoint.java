/**
 * 
 */
package au.com.wp.corp.p6.service;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.exception.P6BusinessException;

/**
 * @author n039619
 *
 */
public interface PortletServiceEndpoint {
	
	ViewToDoStatus fetchWorkOrdersForViewToDoStatus(RequestEntity<WorkOrderSearchRequest> query);
	ResponseEntity<WorkOrder> saveWorkOrder(RequestEntity<WorkOrder> workOrder) throws P6BusinessException;
	
	List<ToDoItem> fetchToDoItems();
	//ResponseEntity<ExecutionPackageDTO> saveExecutionPackages(RequestEntity<ExecutionPackageDTO> executionPackageDTO) throws P6BaseException;
	ResponseEntity<ViewToDoStatus> saveViewToDoStatus(RequestEntity<ViewToDoStatus> viewToDoStatus) throws P6BusinessException;
	List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(RequestEntity<WorkOrderSearchRequest> query);

}
