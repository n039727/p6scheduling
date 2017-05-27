/**
 * 
 */
package au.com.wp.corp.p6.dao;

import java.util.List;

import au.com.wp.corp.p6.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;

/**
 * @author N039126
 *
 */
public interface P6PortalDAO {

	public List<P6ProjWorkgroupDTO> getProjectResourceMappingList() throws P6DataAccessException;
}
