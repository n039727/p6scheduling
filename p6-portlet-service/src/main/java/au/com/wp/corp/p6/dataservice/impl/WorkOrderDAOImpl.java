/**
 * 
 */
package au.com.wp.corp.p6.dataservice.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.model.Task;

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
	public List<Task> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchInput query) {
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
		sessionFactory.getCurrentSession().saveOrUpdate(task);
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
