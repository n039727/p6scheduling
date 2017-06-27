/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import java.util.List;

import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;

/**
 * @author N039126
 *
 */
@FunctionalInterface
public interface P6PortalDAO{
	public List<P6ProjWorkgroupDTO> getProjectResourceMappingList() throws P6DataAccessException;
	
}
