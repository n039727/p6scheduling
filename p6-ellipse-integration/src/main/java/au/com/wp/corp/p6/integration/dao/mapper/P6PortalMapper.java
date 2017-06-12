/**
 * 
 */
package au.com.wp.corp.p6.integration.dao.mapper;

import java.util.List;

import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;

/**
 * Mapper class to provide functionality to map the result set
 * 
 * @author N039126
 * @version 1.0
 */
@FunctionalInterface
public interface P6PortalMapper {
	/**
	 * retrieve project resource details from portal DB
	 * 
	 * @return list {@link List}
	 */
	public List<P6ProjWorkgroupDTO> getProjectResourceMappingList();

}
