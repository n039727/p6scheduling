/**
 * 
 */
package au.com.wp.corp.p6.dataservice.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.model.ExecutionPackage;

/**
 * @author n039619
 *
 */
@Repository
public class ExecutionPackageDaoImpl implements ExecutionPackageDao {
	
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

}
