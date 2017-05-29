package au.com.wp.corp.p6.dataservice.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.FunctionAccessDAO;
import au.com.wp.corp.p6.model.FunctionAccess;

@Repository
public class FunctionAccessDAOImpl implements FunctionAccessDAO {

	
	private static final Logger logger = LoggerFactory.getLogger(ExecutionPackageDao.class);
	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FunctionAccess> getAccess(String roleName) {
		logger.debug("sessionfactory initialized ====={}", sessionFactory);
		logger.debug("Input execution package name ====={}", roleName);
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(FunctionAccess.class);
		criteria.add(Restrictions.eq("roleNam", roleName));
		List<FunctionAccess> accesses = (List<FunctionAccess>) criteria
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		return accesses;
	}

}
