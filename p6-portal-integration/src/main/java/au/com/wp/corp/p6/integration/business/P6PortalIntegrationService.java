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
public interface P6PortalIntegrationService {
	
	public void clearApplicationMemory();

	public boolean startPortalToP6Integration() throws P6BusinessException;

	boolean readUDFTypeMapping() throws P6BusinessException;

}
