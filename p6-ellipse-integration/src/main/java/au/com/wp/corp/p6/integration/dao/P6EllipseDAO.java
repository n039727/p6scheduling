/**
 * 
 */
package au.com.wp.corp.p6.integration.dao;

import java.util.List;

import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;

/**
 * @author N039126
 *
 */
@FunctionalInterface
public interface P6EllipseDAO extends P6IntegrationDAOExceptionParser{
	public List<EllipseActivityDTO> readElipseWorkorderDetails(List<String> workgroupList) throws P6DataAccessException;
	
	
}
