/**
 * 
 */
package au.com.wp.corp.p6.dao.mapper;

import java.util.List;

import au.com.wp.corp.p6.dto.P6ProjWorkgroupDTO;

/**
 * Mapper class to provide functionality to map the result set
 * 
 * @author N039126
 * @version 1.0
 */
public interface P6PortalMapper {
	/**
	 * retrieve project resource details from portal DB
	 * 
	 * @return list {@link List}
	 */
	public List<P6ProjWorkgroupDTO> getProjectResourceMappingList();

}
