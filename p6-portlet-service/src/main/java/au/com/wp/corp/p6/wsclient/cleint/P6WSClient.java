/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint;

import java.util.List;

import au.com.wp.corp.p6.dto.Crew;
import au.com.wp.corp.p6.dto.ResourceSearchRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.model.ActivitySearchRequest;

/**
 * @author n039126
 * @version 1.0
 *
 */
public interface P6WSClient {
	
	public static final String WS_AUTH_SERVICE_CALL_TIME = "P6_WS_AUTH_SERVICE_CALL_TIME";
	public static final String AND = " AND ";
	public static final String OR = " OR ";
	
	public List<WorkOrder> searchWorkOrder (ActivitySearchRequest searchRequest) throws P6ServiceException;
	public List<Crew> searchCrew (ResourceSearchRequest searchRequest) throws P6ServiceException;
}
