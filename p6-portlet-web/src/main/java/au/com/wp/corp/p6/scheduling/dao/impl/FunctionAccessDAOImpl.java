package au.com.wp.corp.p6.scheduling.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.scheduling.dao.FunctionAccessDAO;
import au.com.wp.corp.p6.scheduling.model.FunctionAccess;

@Repository
public class FunctionAccessDAOImpl implements FunctionAccessDAO {

	
	private static final Logger logger = LoggerFactory.getLogger(FunctionAccessDAOImpl.class);
	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FunctionAccess> getAccess(List<String> roleNames) {
		logger.debug("sessionfactory initialized ====={}", sessionFactory);
		logger.debug("Input execution package name ====={}", roleNames);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FunctionAccess.class);
		//criteria.add(Restrictions.eq("roleNam", roleName));
		criteria.add(Restrictions.in("roleNam", roleNames));
		List<FunctionAccess> accesses = (List<FunctionAccess>) criteria
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		return accesses;
	}

	@Transactional
	@Override
	public List<String> fetchAllRole() {
		
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FunctionAccess.class);
	       criteria.setProjection(
	                 Projections.distinct(Projections.property("roleNam"))).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
	       List<String> roles = criteria.list();
	       
	       //List<FunctionAccess> roles = criteria.list();
			/*@SuppressWarnings("unchecked")
			List<FunctionAccess> roles = (List<FunctionAccess>) getSession()
					.createCriteria(FunctionAccess.class)
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();*/
						

		return roles;
		
	}
}
