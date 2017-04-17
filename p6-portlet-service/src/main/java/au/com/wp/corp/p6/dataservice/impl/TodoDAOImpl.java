package au.com.wp.corp.p6.dataservice.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;

@Repository
public class TodoDAOImpl implements TodoDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(TodoDAO.class);
	@Autowired
	SessionFactory sessionFactory;

	@Transactional
	public List<TodoTemplate> fetchAllToDos() {

		logger.debug("sessionfactory initialized ====="+sessionFactory);
	        @SuppressWarnings("unchecked")
			List<TodoTemplate> listToDo = (List<TodoTemplate>) sessionFactory.getCurrentSession()
	                .createCriteria(TodoTemplate.class)
	                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
	 
	        return listToDo;

	}
	
	@Transactional
	public WorkOrder saveToDos(WorkOrder workOrder) {
		logger.info("Entering method saveToDos");
        @SuppressWarnings("unchecked")
		TodoAssignment todoAssignment = new TodoAssignment();
        todoAssignment.setCmts(workOrder.getSchedulingToDoComment());
        ExecutionPackage executionPackage = new ExecutionPackage();
        executionPackage.setExctnPckgNam(workOrder.getExecutionPackage());
        todoAssignment.setExecutionPackage(executionPackage);
        sessionFactory.getCurrentSession().saveOrUpdate(todoAssignment);
		logger.info("Entering method saveToDos");
        return workOrder;
	}

}
