/**
 * 
 */
package au.com.wp.corp.p6.dao;

import java.util.List;

import au.com.wp.corp.p6.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;

/**
 * @author N039126
 *
 */
@FunctionalInterface
public interface P6EllipseDAO {

	public List<EllipseActivityDTO> readElipseWorkorderDetails(List<String> workgroupList) throws P6DataAccessException; 
}
