package au.com.wp.corp.p6.dataservice;

import java.util.List;

import au.com.wp.corp.p6.model.TodoTemplate;

public interface TodoDAO {

	List<TodoTemplate> fetchAllToDos();
}
