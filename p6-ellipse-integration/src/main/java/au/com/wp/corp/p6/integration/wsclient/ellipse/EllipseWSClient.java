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
@FunctionalInterface
public interface EllipseWSClient {

	public void updateActivitiesEllipse(List<EllipseActivityDTO> activities, String transactionId) throws P6ServiceException;
}
