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
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dto.EPCreateDTO;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
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


	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.dataservice.ExecutionPackageDao#fetch(java.lang.String)
	 */
	@Override
	@Transactional
	public ExecutionPackage fetch(String name) {
		Criteria criteria = sessionFactory.getCurrentSession().
				 createCriteria(ExecutionPackage.class);
		criteria.add(Restrictions.eq("exctnPckgNam", name));
		criteria.setFetchSize(1);
		List<ExecutionPackage> retValue =  (List<ExecutionPackage>) criteria.list();
		ExecutionPackage pkg = null;
		if (retValue != null && retValue.size() == 1) {
			pkg = retValue.get(0);
		} else {
			// TODO Throw exception
		}
		
		return pkg;
	}
	
	@Transactional
	@Override
	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO) throws P6DataAccessException{
		logger.debug("sessionfactory initialized =====" + sessionFactory);
		ExecutionPackage executionPackage = new ExecutionPackage();
		logger.debug("Creating Execution Package");
		executionPackage.setExctnPckgNam(executionPackageDTO.getExctnPckgNam());
		executionPackage.setLeadCrewId(executionPackageDTO.getLeadCrew());
		List<EPCreateDTO> workOrders = executionPackageDTO.getCreateDTO();
		if (workOrders != null && workOrders.size() > 0) {
			logger.debug("work orders size {}", workOrders.size());
			
			Set<Task> tasks = new HashSet<Task>();
			for (EPCreateDTO workOrder : workOrders) {
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

}
