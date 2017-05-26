package au.com.wp.corp.p6.dataservice.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.MaterialRequisitionDAO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.elipse.MaterialRequisition;
@Repository
public class MaterialRequisitionDAOImpl implements MaterialRequisitionDAO {

	@Autowired
	SessionFactory elipsSessionFactory;
	private volatile List<MaterialRequisition> metReqList = null;
	private Object lock = new Object();
	@Override
	public Session getSession() {
		return elipsSessionFactory.getCurrentSession();
	}

	@Override
	public List<MaterialRequisition> listMetReq(Object[] workOrderId) throws P6DataAccessException {
		if (metReqList == null) {
			if (metReqList == null) {
				/*Criteria criteria = elipsSessionFactory.getCurrentSession().createCriteria(MaterialRequisition.class);
					criteria.add(Restrictions.in("workOrder", workOrderId));
					@SuppressWarnings("unchecked")
					List<MaterialRequisition> resultList = (List<MaterialRequisition>) criteria.list();
					metReqList = resultList;*/
				String hql = " from "+ MaterialRequisition.class.getName() +" where  dstrctCode ='CORP' and workOrder in (:workOrder)";
				Query q = elipsSessionFactory.getCurrentSession().createQuery(hql).setParameterList("workOrder", workOrderId);
				List<MaterialRequisition> resultList= q.list();
				metReqList = resultList;
			}
		}
		return metReqList;
	}
}