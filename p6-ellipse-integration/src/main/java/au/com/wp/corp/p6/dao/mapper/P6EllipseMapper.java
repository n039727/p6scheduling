/**
 * 
 */
package au.com.wp.corp.p6.dao.mapper;

import java.util.List;

import au.com.wp.corp.p6.dto.EllipseActivityDTO;

/**
 * @author N039126
 *
 */
@FunctionalInterface
public interface P6EllipseMapper {

	public List<EllipseActivityDTO> readElipseWorkorderDetails(final List<String> workgroupList);

}