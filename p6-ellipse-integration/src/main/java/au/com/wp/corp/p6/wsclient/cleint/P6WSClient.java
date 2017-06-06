/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint;

import java.util.List;
import java.util.Map;

import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.dto.UDFTypeDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;

/**
 * @author n039126
 * @version 1.0
 *
 */
public interface P6WSClient {

	public static final String WS_AUTH_SERVICE_CALL_TIME = "P6_WS_AUTH_SERVICE_CALL_TIME";
	public static final String AND = " AND ";
	public static final String OR = " OR ";

	public List<P6ActivityDTO> readActivities() throws P6ServiceException;

	public void updateActivities(List<P6ActivityDTO> activities) throws P6ServiceException;

	public boolean deleteActivities(List<P6ActivityDTO> activities) throws P6ServiceException;

	public void createActivities(List<P6ActivityDTO> activities) throws P6ServiceException;

	public List<UDFTypeDTO> readUDFTypes() throws P6ServiceException;

	public Map<String, Integer> readResources() throws P6ServiceException;
	
}
