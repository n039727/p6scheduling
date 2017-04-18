/**
 * 
 */
package au.com.wp.corp.p6.dataservice.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		logger.debug("sessionfactory initialized ====="+sessionFactory);
		
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Task.class);
        logger.debug("DEPOT_ID>>>>{}", query.getDepotList());
        if(null != query.getDepotList() && !query.getDepotList().isEmpty()){
        	criteria.add(Restrictions.in("depotId", query.getDepotList()));
        }
        logger.debug("CREW_ID>>>>{}", query.getCrewList());
        if(null != query.getCrewList() && !query.getCrewList().isEmpty()){
        	criteria.add(Restrictions.in("crewId", query.getCrewList()));
        }
        //TODO check for start and end date
        logger.debug("SCHD_DT>>>>{}  {}", query.getFromDate(), query.getToDate());
      
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date startDate = null;
        Date endDate = null;
		try {
			if(null != query.getFromDate() && null != query.getToDate()){
				startDate = simpleDateFormat.parse(query.getFromDate());
				endDate = simpleDateFormat.parse(query.getToDate());
				criteria.add(Restrictions.between("schdDt", startDate, endDate));
			}
			else if(null != query.getFromDate()){
				startDate = simpleDateFormat.parse(query.getFromDate());
				 criteria.add(Restrictions.ge("schdDt", startDate));
				 //criteria.add(Restrictions.le("schdDt", query.getFromDate()));
			}
			else if(null != query.getToDate()){
				endDate = simpleDateFormat.parse(query.getToDate());
				criteria.add(Restrictions.le("schdDt", endDate));
				//criteria.add(Restrictions.ge("schdDt", query.getToDate()));
			}
			
		} catch (ParseException e) {
		//TODO Auto-generated catch block
			e.printStackTrace();
		}
		//criteria.add(Restrictions.eq("TASK_ID", query.getWorkOrderId()));
   
		/*List list = criteria.list();
		logger.info("size={}",list.size());*/
		@SuppressWarnings("unchecked")
		List<Task> listTask = (List<Task>) criteria
                  .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		logger.info("size={}",listTask.size());
        return listTask;
	}

}
