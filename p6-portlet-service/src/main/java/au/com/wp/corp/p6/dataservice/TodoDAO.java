package au.com.wp.corp.p6.dataservice;

import java.math.BigDecimal;
import java.util.List;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;

public interface TodoDAO {

	List<TodoTemplate> fetchAllToDos();
	WorkOrder saveToDos(WorkOrder workOrder);
	
	List<TodoAssignment> fetchToDosByWorkOrder ( WorkOrder workOrder );
	String getToDoName(Long id);
	BigDecimal getToDoId(String todoName);
	TodoAssignment fetchAssignmentWorkOrderNToDo(String workOrderId, String toDoName);
	TodoAssignment saveToDoAssignment(TodoAssignment todoAssign);
	
}
