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

import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.model.Task;

/**
 * @author N039603
 *
 */
@Repository
public class WorkOrderDAOImpl implements WorkOrderDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(TaskDAO.class);
	@Autowired
	SessionFactory sessionFactory;

	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.dataservice.WorkOrderDAO#fetchWorkOrdersForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchInput)
	 */
	@Override
	@Transactional
	public List<Task> fetchWorkOrdersForViewToDoStatus(WorkOrderSearchInput query) {
logger.debug("sessionfactory initialized ====="+sessionFactory);
		
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Task.class);
        if(null != query.getDepotList() && !query.getDepotList().isEmpty()){
        	logger.debug("DEPOT_ID>>>>{}", query.getDepotList());
        	criteria.add(Restrictions.in("depotId", query.getDepotList()));
        }
        if(null != query.getCrewList() && !query.getCrewList().isEmpty()){
        	logger.debug("CREW_ID>>>>{}", query.getCrewList());
        	criteria.add(Restrictions.in("crewId", query.getCrewList()));
        }
        //TODO check for start and end date
        criteria.add(Restrictions.between("schdDt", query.getFromDate(), query.getToDate()));
        logger.debug("SCHD_DT>>>>{}{}", query.getFromDate(), query.getToDate());
        //criteria.add(Restrictions.eq("TASK_ID", query.getWorkOrderId()));
        
		@SuppressWarnings("unchecked")
		List<Task> listTask = (List<Task>) criteria
                  .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
 
        return listTask;
	}

}
