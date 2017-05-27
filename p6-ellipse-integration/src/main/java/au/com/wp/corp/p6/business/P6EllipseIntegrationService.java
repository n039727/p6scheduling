/**
 * 
 */
package au.com.wp.corp.p6.business;

import java.util.List;

import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.exception.P6BusinessException;

/**
 * @author N039126
 *
 */
public interface P6EllipseIntegrationService {
	
	public List<P6ActivityDTO> startEllipseToP6Integration() throws P6BusinessException;

}
