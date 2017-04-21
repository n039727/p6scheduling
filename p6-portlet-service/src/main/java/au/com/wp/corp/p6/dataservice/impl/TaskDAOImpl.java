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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
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

	@Transactional
	@Override
	public List<ExecutionPackage> listExecutionPackages() {
		logger.debug("sessionfactory initialized =====" + sessionFactory);
		List<ExecutionPackage> listExecPakgs = (List<ExecutionPackage>) sessionFactory.getCurrentSession()
				.createCriteria(ExecutionPackage.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

		return listExecPakgs;
	}

	@Transactional
	@Override
	public ExecutionPackageDTO saveExecutionPackage(ExecutionPackageDTO executionPackageDTO) {
		logger.debug("sessionfactory initialized =====" + sessionFactory);
		ExecutionPackage executionPackage = new ExecutionPackage();
		logger.debug("Creating Execution Package");
		executionPackage.setExctnPckgNam(executionPackageDTO.getExctnPckgNam());
		executionPackage.setLeadCrewId(executionPackageDTO.getLeadCrew());
		String scheduleDate = executionPackageDTO.getScheduleDate();
		List<String> workOrders = executionPackageDTO.getWorkOrders();
		if (workOrders != null && workOrders.size() > 0) {
			logger.debug("work orders size {}", workOrders.size());
			Set<Task> tasks = new HashSet<Task>();
			for (String workOrder : workOrders) {
				logger.debug("For each workorder {} corresponding Task is fecthed", workOrder);
				Task task = (Task) sessionFactory.getCurrentSession().get(Task.class, workOrder);
				if (task != null) {
					logger.debug("Task {} is fecthed", task.getTaskId());
					task.setExecutionPackage(executionPackage);
					tasks.add(task);
				} 
			}
			executionPackage.setTasks(tasks);

		}
		executionPackage.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setCrtdUsr("test user");
		executionPackage.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setLstUpdtdUsr("test user");
		sessionFactory.getCurrentSession().saveOrUpdate(executionPackage);

		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();

		return executionPackageDTO;
	}
}
	

