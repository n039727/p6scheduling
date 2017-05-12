/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint;

import java.util.List;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.model.ActivitySearchRequest;

/**
 * @author n039126
 * @version 1.0
 *
 */
public interface P6WSClient {
	public List<WorkOrder> searchWorkOrder (ActivitySearchRequest searchRequest) throws P6ServiceException;
}
