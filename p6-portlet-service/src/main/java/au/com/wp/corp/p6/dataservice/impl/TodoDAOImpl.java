package au.com.wp.corp.p6.dataservice.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.TodoTemplate;

@Repository
public class TodoDAOImpl implements TodoDAO { 
	
	private static final Logger logger = LoggerFactory.getLogger(TodoDAO.class);
	@Autowired 
	SessionFactory sessionFactory;
	
	private volatile Map<Long, TodoTemplate> toDoMap = null;
	private volatile Map<String, TodoTemplate> toDoNameMap = null;
	private Object lock = new Object();
	private Long maxPk = null;
	
	
	@Transactional
	public List<TodoTemplate> fetchAllToDos() {

		if (toDoMap == null) {
			synchronized (lock) {
				if (toDoMap == null) {
					@SuppressWarnings("unchecked")
					List<TodoTemplate> listToDo = (List<TodoTemplate>) getSession()
							.createCriteria(TodoTemplate.class)
							.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
					toDoMap = new HashMap<Long, TodoTemplate>();
					toDoNameMap = new HashMap<String, TodoTemplate>();
					for (TodoTemplate todo:listToDo) {
						toDoMap.put(todo.getTodoId(), todo);
						toDoNameMap.put(todo.getTodoNam(), todo);
					}
				}
			}
		}

		return new ArrayList<TodoTemplate>(toDoMap.values());
		
	}
	
	@Override
	public String getToDoName(Long id) {
		fetchAllToDos();
		if (toDoMap != null && toDoMap.containsKey(id)) {
			return toDoMap.get(id).getTodoNam();
		}
		return null;
	}
	
	
	@Override
	public BigDecimal getToDoId(String todoName) {
		fetchAllToDos();
		if (toDoNameMap != null && toDoNameMap.containsKey(todoName)) {
			return new BigDecimal(toDoNameMap.get(todoName).getTodoId());
		}
		return null;
	}
	
	@Override
	public Long getTypeId(String todoName) {
		fetchAllToDos();
		if (toDoNameMap != null && toDoNameMap.containsKey(todoName)) {
			return toDoNameMap.get(todoName).getTypId().longValue();
		}
		return null;
	}
	
	@Override
	public Long getMaxToDoId() { 
		Criteria criteria = getSession()
			    .createCriteria(TodoTemplate.class)
			    .setProjection(Projections.max("id.todoId"));
		Long maxPk = (Long)criteria.uniqueResult();
		logger.debug("Returning Max Pk: " + maxPk);
		return maxPk;
	}
	
		
	@Transactional
	@Override
	public boolean createToDo(TodoTemplate todoTemplate) throws P6DataAccessException {
		logger.debug("inserting the user define TodoTemplate");
		boolean status = Boolean.FALSE;

		try {
			getSession().saveOrUpdate(todoTemplate);
			status = Boolean.TRUE;
		} catch (Exception e) {
			parseException(e);
		}
		logger.debug("inserted the user define TodoTemplate");
		
		// update Max To Do Id
		synchronized (lock) {
			maxPk = todoTemplate.getTodoId();
			toDoNameMap.put(todoTemplate.getTodoNam(), todoTemplate);
			toDoMap.put(todoTemplate.getTodoId(), todoTemplate);
			logger.debug("Setting Max pk after adding new to do: " + maxPk);
		}
		return status;
	}

	@Override
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

}
