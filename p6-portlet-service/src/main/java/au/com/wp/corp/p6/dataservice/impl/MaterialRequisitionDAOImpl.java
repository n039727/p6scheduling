package au.com.wp.corp.p6.dataservice.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.MaterialRequisitionDAO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.elipse.MaterialRequisition;
@Repository
public class MaterialRequisitionDAOImpl implements MaterialRequisitionDAO {

	@Autowired
	SessionFactory elipsSessionFactory;
	@Override
	public Session getSession() {
		return elipsSessionFactory.getCurrentSession();
	}

	@Override
	public List<MaterialRequisition> listMetReq(Object[] workOrderId) throws P6DataAccessException {
		Criteria criteria = elipsSessionFactory.getCurrentSession().createCriteria(MaterialRequisition.class);
		criteria.add(Restrictions.in("workOrder", workOrderId));
		@SuppressWarnings("unchecked")
		List<MaterialRequisition> resultList = (List<MaterialRequisition>) criteria.list();
		return resultList;
	}
}