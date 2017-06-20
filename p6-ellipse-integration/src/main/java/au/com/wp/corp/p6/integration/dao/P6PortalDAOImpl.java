/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import au.com.wp.corp.p6.integration.dao.mapper.P6PortalMapper;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;

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
	P6PortalMapper p6PortalDataMapper;

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.dao.P6PortalDAO#getProjectResourceMappingList()
	 */
	@Override
	public List<P6ProjWorkgroupDTO> getProjectResourceMappingList() throws P6DataAccessException{
		logger.debug("Reading project resource mapping details from p6 portal ........");
		List<P6ProjWorkgroupDTO> projectWorkgroupMapping = null;
		try {
			projectWorkgroupMapping = p6PortalDataMapper.getProjectResourceMappingList();
			logger.debug("Read project resource mapping details from p6 portal {}", projectWorkgroupMapping);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An error occurs while reading project resource mapping details from p6 portal : ", e);
			throw new P6DataAccessException (e);
		}

		return projectWorkgroupMapping;
	}

}
