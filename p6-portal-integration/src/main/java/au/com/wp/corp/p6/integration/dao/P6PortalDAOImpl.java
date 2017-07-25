/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.ResourceDetail;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;

/**
 * P6portalDAO to read all project resource mapping data from p6 portal DB
 * 
 * @author N039126
 * @version 1.0
 * 
 */
@Repository
public class P6PortalDAOImpl implements P6PortalDAO {

	private static final Logger logger = LoggerFactory.getLogger(P6PortalDAOImpl.class);

	@Autowired
	SessionFactory sessionFactory;

	private volatile Map<String, List<String>> depotCrewMap = null;

	@SuppressWarnings("unchecked")
	@Transactional
	@Override
	public List<Task> getALlTasks() throws P6DataAccessException {
		logger.debug("sessionfactory initialized ====={}", sessionFactory);
		List<Task> listTasks = null;
		try {
			listTasks = (List<Task>) getSession().createCriteria(Task.class)
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		} catch (Exception e) {
			parseException(e);
		}

		return listTasks;
	}


	@Override
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	@Transactional
	public Task saveTask(Task task) throws P6DataAccessException {
		try {
			logger.debug("Current User: {} ", task.getCrtdUsr());
			logger.debug("Current session: {} ", getSession().getTransaction());
			long currentTime = System.currentTimeMillis();
			if (task.getCrtdTs() == null) {
				task.setCrtdTs(new Timestamp(currentTime));
			}
			task.setLstUpdtdTs(new Timestamp(currentTime));
			if (task.getTodoAssignments() != null) {
				for (TodoAssignment todo: task.getTodoAssignments()) {
					if (todo.getCrtdTs() == null) {
						todo.setCrtdTs(new Timestamp(currentTime));
						todo.setCrtdUsr(task.getCrtdUsr()); // update the user name here
					}
					todo.setLstUpdtdTs(new Timestamp(currentTime));
					todo.setLstUpdtdUsr(task.getLstUpdtdUsr()); // update the user name here
				}
			}

			sessionFactory.getCurrentSession().saveOrUpdate(task);
		} catch (HibernateException e) {
			parseException(e);
		}
		return task;
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
		return status;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional
	@Override
	public Map<String, List<String>> fetchAllResourceDetail() throws P6DataAccessException {
		try{
			if(null == depotCrewMap){
				depotCrewMap = new HashMap();
				List<ResourceDetail> resourceDetails = (List<ResourceDetail>) getSession()
						.createCriteria(ResourceDetail.class).addOrder(org.hibernate.criterion.Order.asc("depotNam"))
						.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();

				List<String> crewList = null;
				for (ResourceDetail resource:resourceDetails) {

					if(!depotCrewMap.containsKey(resource.getDepotNam())){
						crewList = new ArrayList();
						crewList.add(resource.getRsrcNam());
						depotCrewMap.put(resource.getDepotNam(), crewList);
					}
					else{
						depotCrewMap.get(resource.getDepotNam()).add(resource.getRsrcNam());
					}

				}
			}
		}catch(Exception e){
			parseException(e);
		}
		return depotCrewMap;
	}


	@Override
	@Transactional
	public void removeTask(Task task) throws P6DataAccessException {
		try{
			logger.debug("Current User: {} ", task.getCrtdUsr());
			logger.debug("Current session: {} ", getSession().getTransaction());
			long currentTime = System.currentTimeMillis();
			sessionFactory.getCurrentSession().delete(task);
		}catch (HibernateException e) {
			parseException(e);
		}	

	}

}
