package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.dto.MetadataDTO;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;


public interface P6SchedulingBusinessService extends P6BusinessExceptionParser{
	
	public MetadataDTO fetchMetadata()throws P6BusinessException;
	public ViewToDoStatus fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query) throws P6BusinessException;
	public WorkOrder saveToDo(WorkOrder workOrder) throws P6BusinessException;
	public List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(WorkOrderSearchRequest body) throws P6BusinessException;
	public ViewToDoStatus saveViewToDoStatus(ViewToDoStatus body) throws P6BusinessException;
	public List<WorkOrder> search(WorkOrderSearchRequest input) throws P6BusinessException;
	
	List<WorkOrder> retrieveWorkOrders(WorkOrderSearchRequest input) throws P6BusinessException;
	void updateTasksAndExecutionPackageInP6AndDB() throws P6BusinessException;
	void clearApplicationMemory();
	List<WorkOrder> retrieveWorkOrdersForExecutionPackage(WorkOrderSearchRequest input) throws P6BusinessException;
}
