package au.com.wp.corp.p6.dataservice.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.model.TodoTemplate;

@Repository
public class TodoDAOImpl implements TodoDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskDAO.class);
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

}
