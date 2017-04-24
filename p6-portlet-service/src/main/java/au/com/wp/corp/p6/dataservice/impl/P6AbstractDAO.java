/**
 * 
 */
package au.com.wp.corp.p6.dataservice.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author n039126
 *
 */
@Repository
public abstract class P6AbstractDAO {
	private static final Logger logger = LoggerFactory.getLogger(P6AbstractDAO.class);
	
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * returns current session 
	 * 
	 * @return currentSession {@link Session}
	 */
	protected Session getSession (){
		return sessionFactory.getCurrentSession();
	}

	/**
	 * flushing and clearing current session
	 */
	protected void clearSession () {
		getSession().flush();
		getSession().clear();
	}
	
	
	@Transactional
	protected void save ( Object obj){
		getSession().saveOrUpdate(obj);
	}
	
}
