package au.com.wp.corp.p6.dataservice.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.Task;

@Repository
public class TaskDAOImpl implements TaskDAO {

	private static final Logger logger = LoggerFactory.getLogger(TaskDAO.class);
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * returns current session 
	 * 
	 * @return currentSession {@link Session}
	 */
	public Session getSession (){
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public List<Task> listTasks() throws P6DataAccessException {
		logger.debug("sessionfactory initialized ====={}", sessionFactory);
		List<Task> listTasks = null;
		try {
			listTasks = (List<Task>) getSession().createCriteria(Task.class)
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (HibernateException e) {
			parseException(e);
		} catch (Exception e){
			parseException(e);
		}

		return listTasks;
	}
	
}
	

