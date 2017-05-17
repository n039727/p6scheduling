package au.com.wp.corp.p6.dataservice;

import java.math.BigDecimal;
import java.util.List;

import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.TodoTemplate;

public interface TodoDAO extends P6DAOExceptionParser{

	List<TodoTemplate> fetchAllToDos();
	String getToDoName(Long id);
	BigDecimal getToDoId(String todoName);
	public List<TodoTemplate> fetchToDoForGratestToDoId();
	public boolean createToDo(TodoTemplate odoTemplate) throws P6DataAccessException;

}
