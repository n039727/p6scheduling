/**
 * 
 */
package au.com.wp.corp.p6.dataservice.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;

/**
 * @author N039603
 *
 */
@Repository
public class WorkOrderDAOImpl implements WorkOrderDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkOrderDAOImpl.class);
	@Autowired
	SessionFactory sessionFactory; 

	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.dataservice.WorkOrderDAO#fetchWorkOrdersForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchInput)
	 */
	@Override
	@Transactional
	public List<Task> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query) {
		logger.debug("sessionfactory initialized ====={}",sessionFactory);
		
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Task.class);
        
		logger.debug("Input TASK_ID>>>>{}", query.getWorkOrderId());
		criteria.add(Restrictions.eq("taskId", query.getWorkOrderId()));
 
		@SuppressWarnings("unchecked")
		List<Task> listTask = (List<Task>) criteria
                  .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		/* This list size should always be 1*/
		logger.info("size={}",listTask.size());
        return listTask;
	}
	
	@Override
	@Transactional
	public Task saveTask(Task task) {
		//sessionFactory.getCurrentSession().flush();
		//sessionFactory.getCurrentSession().clear();
		try {
			long currentTime = System.currentTimeMillis();
			if (task.getCrtdTs() != null) {
				task.setCrtdTs(new Timestamp(currentTime));
				task.setCrtdUsr("Test"); //TODO update the user name here
			}
			task.setLstUpdtdTs(new Timestamp(currentTime));
			task.setLstUpdtdUsr("Test"); //TODO update the user name here
					
			if (task.getTodoAssignments() != null) {
				for (TodoAssignment todo: task.getTodoAssignments()) {
					if (todo.getCrtdTs() == null) {
						todo.setCrtdTs(new Timestamp(currentTime));
						todo.setCrtdUsr("Test"); //TODO update the user name here
					}
					todo.setLstUpdtdTs(new Timestamp(currentTime));
					todo.setLstUpdtdUsr("Test"); //TODO update the user name here
					sessionFactory.getCurrentSession().saveOrUpdate(todo);
				}
			}
			
			sessionFactory.getCurrentSession().saveOrUpdate(task);
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return task;
	}

	@Override
	@Transactional
	public Task fetch(String workOrderId) {
		logger.debug("sessionfactory initialized ====={}",sessionFactory);
		
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Task.class);
        
		logger.debug("Input TASK_ID>>>>{}", workOrderId);
		criteria.add(Restrictions.eq("taskId", workOrderId));
		criteria.setFetchSize(1);
 
		@SuppressWarnings("unchecked")
		List<Task> listTask = (List<Task>) criteria
                  .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		/* This list size should always be 1*/
		logger.info("size={}",listTask.size());
		if (listTask != null && listTask.size() == 1) {
			return listTask.get(0);
		} else {
			// TODO throw exception
		}
        return null;
	}

}
