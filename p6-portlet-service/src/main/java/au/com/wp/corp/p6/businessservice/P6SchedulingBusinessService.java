package au.com.wp.corp.p6.businessservice;

import java.util.List;

import au.com.wp.corp.p6.dto.Crew;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.MetadataDTO;
import au.com.wp.corp.p6.dto.ResourceSearchRequest;
import au.com.wp.corp.p6.dto.TaskDTO;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;


public interface P6SchedulingBusinessService {
	
	public List<WorkOrder> retrieveWorkOrders(WorkOrderSearchRequest input) throws P6BusinessException;
	public List<TaskDTO> listTasks() throws P6BusinessException;
	public MetadataDTO fetchMetadata()throws P6BusinessException;
	public ViewToDoStatus fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query);
	public WorkOrder saveToDo(WorkOrder workOrder) throws P6BusinessException;
	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO) throws P6BusinessException;
	public List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(WorkOrderSearchRequest body);
	public ViewToDoStatus saveViewToDoStatus(ViewToDoStatus body) throws P6BusinessException;
	public List<WorkOrder> search(WorkOrderSearchRequest input) throws P6BusinessException;
	public List<Crew> retrieveCrews(ResourceSearchRequest input) throws P6BusinessException;

}
