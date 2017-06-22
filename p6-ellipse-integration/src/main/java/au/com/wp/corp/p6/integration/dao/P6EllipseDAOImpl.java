/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.integration.dao.mapper.P6EllipseMapper;
import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;

/**
 * @author N039126
 * @version 1.0
 */
@Repository
public class P6EllipseDAOImpl implements P6EllipseDAO{

	private static final Logger logger = LoggerFactory.getLogger(P6EllipseDAOImpl.class);
	
	@Autowired
	P6EllipseMapper ellipseDataMapper;
	
	@Override
	public List<EllipseActivityDTO> readElipseWorkorderDetails(final List<String> workgroupList) throws P6DataAccessException {
		logger.debug("Reading data from ellipse ........");
		List<EllipseActivityDTO> activities = null;
		try {
			activities = ellipseDataMapper.readElipseWorkorderDetails(workgroupList);
			logger.debug("Read data from ellipse with activities {}", activities);
		}  
		catch (Exception e) {
			logger.error("An error occurs while reading data from ellipse with activities : ", e);
			throw new P6DataAccessException (e);
		}
		
		return activities;
	}

}
