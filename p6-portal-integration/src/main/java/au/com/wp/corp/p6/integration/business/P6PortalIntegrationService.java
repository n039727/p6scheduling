/**
 * 
 */
package au.com.wp.corp.p6.integration.business;

import au.com.wp.corp.p6.integration.dao.P6DAOExceptionParser;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.exception.P6BusinessExceptionParser;

/**
 * @author N039126
 *
 */
public interface P6PortalIntegrationService extends P6BusinessExceptionParser{
	
	public void clearApplicationMemory();

	public boolean startPortalToP6Integration() throws P6BusinessException;

}
