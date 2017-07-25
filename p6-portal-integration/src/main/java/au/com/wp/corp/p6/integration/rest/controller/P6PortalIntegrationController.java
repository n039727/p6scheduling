/**
 * 
 */
package au.com.wp.corp.p6.integration.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.integration.business.P6PortalIntegrationService;
import au.com.wp.corp.p6.integration.exception.P6BaseException;

/**
 * @author N039126
 *
 */
@RestController
@RequestMapping("/PortalIntegration")
public class P6PortalIntegrationController {
	private static final Logger logger = LoggerFactory.getLogger(P6PortalIntegrationController.class);
	
	private static final String STATUS_COMLETED = "OK";
	
	private static final String STATUS_FAILED = "NOTOK";
	
	@Autowired 
	P6PortalIntegrationService p6PortalService;
	
	
	
	@RequestMapping(value = "/p6-portal", produces = MediaType.TEXT_PLAIN_VALUE)
	public String startIntegartion (){
		final long startTime = System.currentTimeMillis();
		try {
			boolean status = p6PortalService.startPortalToP6Integration();
			if ( ! status )
				return STATUS_FAILED;
		} catch (P6BaseException e) {
			logger.error("An error occurs during batch processing - ",e);
			return STATUS_FAILED;
		}
		p6PortalService.clearApplicationMemory();
		logger.info("Time taken to complete the batch in milisecond - {} ms", System.currentTimeMillis()-startTime);
		return STATUS_COMLETED;
	}
}
