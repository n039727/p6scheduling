/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.ellipse;

import java.util.List;

import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;

/**
 * WSClient to call Ellipse Workorder Task webserivce 
 * 
 * @author N039126
 * @version 1.0
 */
public interface EllipseWSClient {

	public boolean updateActivitiesEllipse(List<EllipseActivityDTO> activities) throws P6ServiceException;
	
	public boolean updateWorkOrderEllipse(List<EllipseActivityDTO> activities, String transId) throws P6ServiceException;

	
}
