/**
 * 
 */
package au.com.wp.corp.p6.businessservice;

import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;

/**
 * DepotTodoService performs following tasks for depot TODOs
 * a. Display TODOs for both execution package or single task b. update depot TODOs status and different comments
 * @author N039603
 *
 */
public interface DepotTodoService {
	
	/**
	 * Retrieve TODOs for both execution package and single task
	 * @param query
	 * @return ViewToDoStatus
	 */
	public ViewToDoStatus fetchDepotTaskForViewToDoStatus(WorkOrderSearchRequest query);
	
	/**
	 * Update TODO status and comment for both execution package and single task
	 * @param workOrder
	 * @return ViewToDoStatus
	 * @throws P6BusinessException
	 */
	public ViewToDoStatus UpdateDepotToDo(ViewToDoStatus workOrder) throws P6BusinessException;

}
