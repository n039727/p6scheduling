/**
 * 
 */
package au.com.wp.corp.p6.integration.exception;

import com.wp.snow.SnowConnector;

/**
 * @author N039126
 *
 */
public class P6IntegrationExceptionHandler {

	
	
	public void handleDataExeception ( P6BaseException e){
		
	}
	
	
	
	public void handleTechnicalException ( P6BaseException e ){
		final String appName= "P6-Ellipse Integration"; 
		String sDesc = "";
		String desc = "";
		String aGroup = "";
		int priority = 1;
		SnowConnector snow = new SnowConnector();
		snow.create(appName,  sDesc,  desc,  aGroup,  priority);
	}
	
	
}
