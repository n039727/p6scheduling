/**
 * 
 */
package au.com.wp.corp.p6.integration.business;

import java.util.List;

import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;

/**
 * @author N039126
 *
 */
public interface P6EllipseIntegrationService {
	
	public void clearApplicationMemory();

	public boolean readUDFTypeMapping() throws P6BusinessException;

	public boolean readProjectWorkgroupMapping() throws P6BusinessException;

	public boolean start() throws P6BusinessException;

	public List<P6ActivityDTO> startEllipseToP6Integration(List<String> workgroupList, final Integer projectObjectId) throws P6BusinessException;

}
