package au.com.wp.corp.p6.dataservice;

import java.util.List;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;

public interface TodoDAO {

	List<TodoTemplate> fetchAllToDos();
	WorkOrder saveToDos(WorkOrder workOrder);
	
	public List<TodoAssignment> fetchToDosByWorkOrder ( WorkOrder workOrder );
}
