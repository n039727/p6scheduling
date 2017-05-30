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
	
	public static final String POLING_TIME_TO_CHECK_READ_STATUS_INMILI = "POLING_TIME_TO_CHECK_READ_STATUS_INMILI";
	
	public static final String USER_STATUS_AL = "AL";
	
	public List<P6ActivityDTO> startEllipseToP6Integration() throws P6BusinessException;

	public void clearApplicationMemory();

}
