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
import au.com.wp.corp.p6.model.Task;

@Repository
public class TaskDAOImpl implements TaskDAO{

	private static final Logger logger = LoggerFactory.getLogger(TaskDAO.class);
	@Autowired
	SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Task> listTasks() {
		logger.debug("sessionfactory initialized ====="+sessionFactory);
        List<Task> listTasks = (List<Task>) sessionFactory.getCurrentSession()
                .createCriteria(Task.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
 
        return listTasks;
	}

}
