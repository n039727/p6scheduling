/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint;

import java.util.List;
import java.util.Map;

import au.com.wp.corp.p6.integration.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.integration.dto.WorkOrder;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;

/**
 * @author n039126
 * @version 1.0
 *
 */
public interface P6WSClient {

	public static final String WS_AUTH_SERVICE_CALL_TIME = "P6_WS_AUTH_SERVICE_CALL_TIME";
	public static final String AND = " AND ";
	public static final String OR = " OR ";



	public List<UDFTypeDTO> readUDFTypes() throws P6ServiceException;



	List<WorkOrder> readActivities(List<String> taskId) throws P6ServiceException;


	boolean logoutFromP6();


	Map<String, Integer> getWorkOrderIdMap();


	public Boolean removeExecutionPackage(List<Integer> listOfObjectId, boolean b) throws P6ServiceException;



	Boolean updateExecutionPackage(List<ExecutionPackageCreateRequest> request) throws P6ServiceException;
	
}
