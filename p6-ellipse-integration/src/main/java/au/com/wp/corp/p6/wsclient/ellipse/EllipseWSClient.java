/**
 * 
 */
package au.com.wp.corp.p6.wsclient.ellipse;

import java.util.List;

import au.com.wp.corp.p6.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;

/**
 * WSClient to call Ellipse Workorder Task webserivce 
 * 
 * @author N039126
 * @version 1.0
 */
@FunctionalInterface
public interface EllipseWSClient {

	public void updateActivitiesEllipse ( final List<EllipseActivityDTO> activities ) throws P6ServiceException;
}
