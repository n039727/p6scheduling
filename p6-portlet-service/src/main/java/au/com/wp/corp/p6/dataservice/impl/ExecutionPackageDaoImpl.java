/**
 * 
 */
package au.com.wp.corp.p6.dataservice.impl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.mock.CreateP6MockData;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;

/**
 * @author n039619
 *
 */
@Repository
public class ExecutionPackageDaoImpl implements ExecutionPackageDao {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionPackageDao.class);
	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	CreateP6MockData mockData;
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.dataservice.ExecutionPackageDao#fetch(java.lang.String)
	 */
	@Override
	@Transactional
	public ExecutionPackage fetch(String name) {
		logger.debug("sessionfactory initialized ====={}", sessionFactory);
		logger.debug("Input execution package name ====={}", name);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(ExecutionPackage.class);
		criteria.add(Restrictions.eq("exctnPckgNam", name));
		criteria.setFetchSize(1);

		// List<ExecutionPackage> retValue = (List<ExecutionPackage>)
		// criteria.list();
		ExecutionPackage retValue = (ExecutionPackage) criteria.uniqueResult();
		/*
		 * List<ExecutionPackage> retValue = (List<ExecutionPackage>)
		 * criteria.list(); logger.debug("retValue ====={}", retValue);
		 * logger.debug("retValue ====={}", retValue); ExecutionPackage pkg =
		 * null; if (retValue != null && retValue.size() == 1) {
		 * logger.debug("retValue is not empty"); pkg = retValue.get(0); } else
		 * { // TODO Throw exception }
		 */

		logger.debug("returning package as: {} ", retValue.getExctnPckgId());

		return retValue;
	}

	@Transactional
	@Override
	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO){
		logger.debug("sessionfactory initialized =====" + sessionFactory);
		ExecutionPackage executionPackage = new ExecutionPackage();
		logger.debug("Creating Execution Package");
		executionPackage.setExctnPckgNam(executionPackageDTO.getExctnPckgName());
		executionPackage.setLeadCrewId(executionPackageDTO.getLeadCrew());
		List<WorkOrder> workOrders = executionPackageDTO.getWorkOrders();
		if (workOrders != null && workOrders.size() > 0) {
			logger.debug("work orders size {}", workOrders.size());

			Set<Task> tasks = new HashSet<Task>();
			for (WorkOrder workOrder : workOrders) {
				logger.debug("For each workorder {} corresponding Task is fecthed", workOrder.getWorkOrderId());
				Task task = (Task) sessionFactory.getCurrentSession().get(Task.class, workOrder.getWorkOrderId());
				if (task != null) {
					logger.debug("Task {} is fecthed", task.getTaskId());
					task.setExecutionPackage(executionPackage);
					tasks.add(task);
				}
			}
			executionPackage.setTasks(tasks);

		}
		executionPackage.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setCrtdUsr("N039603");
		executionPackage.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setLstUpdtdUsr("N039603");
		sessionFactory.getCurrentSession().saveOrUpdate(executionPackage);

		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();

		return executionPackageDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.dataservice.ExecutionPackageDao#getWorkOderbyId(java.
	 * lang.String)
	 */
	@org.springframework.transaction.annotation.Transactional
	@Override
	public Task getTaskbyId(String taskId) throws P6DataAccessException {
		logger.debug("fetching Task details with task id # {}", taskId);
		Task task = null;
		try {
			task = (Task) getSession().get(Task.class, taskId);
		} catch (HibernateException e) {
			parseException(e);
		}
		logger.debug("Task details with task id # {} - Task # {} ", taskId, task);
		return task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.dataservice.P6DAOExceptionParser#getSession()
	 */
	@Override
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.dataservice.ExecutionPackageDao#
	 * createOrUpdateExecPackage(au.com.wp.corp.p6.model.ExecutionPackage)
	 */
	@org.springframework.transaction.annotation.Transactional
	@Override
	public boolean createOrUpdateExecPackage(ExecutionPackage executionPackage) throws P6DataAccessException {
		logger.debug("inserting or updating the execution package and task details");
		boolean status = Boolean.FALSE;

		try {
			getSession().saveOrUpdate(executionPackage);
			status = Boolean.TRUE;
		} catch (Exception e) {
			parseException(e);
		}
		logger.debug("inserted or updated the execution package and task details");
		getSession().flush();
		getSession().clear();
		return status;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.dataservice.ExecutionPackageDao#
	 * createOrUpdateExecPackage(au.com.wp.corp.p6.model.ExecutionPackage)
	 */
	@org.springframework.transaction.annotation.Transactional
	@Override
	public boolean createOrUpdateTasks(Set<Task> tasks) throws P6DataAccessException {
		logger.debug("inserting or updating the execution package and task details");
		boolean status = Boolean.FALSE;
		for ( Task task : tasks)
		try {
			getSession().saveOrUpdate(task);
			status = Boolean.TRUE;
		} catch (Exception e) {
			parseException(e);
		}
		logger.debug("inserted or updated the execution package and task details");
		getSession().flush();
		getSession().clear();
		return status;
	}


}
