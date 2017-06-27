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

import au.com.wp.corp.p6.integration.business.P6EllipseIntegrationService;
import au.com.wp.corp.p6.integration.exception.P6BaseException;
import au.com.wp.corp.p6.integration.exception.P6ExceptionType;

/**
 * @author N039126
 *
 */
@RestController
@RequestMapping("/integration")
public class P6EllipseIntegrationController {
	private static final Logger logger = LoggerFactory.getLogger(P6EllipseIntegrationController.class);
	
	private static final String STATUS_COMLETED = "OK";
	
	@Autowired 
	P6EllipseIntegrationService p6EllipseService;
	
	@RequestMapping(value = "/p6-ellipse", produces = MediaType.TEXT_PLAIN_VALUE)
	public String startIntegartion (){
		final long startTime = System.currentTimeMillis();
		try {
			p6EllipseService.start();
		} catch (P6BaseException e) {
			logger.error("An error occurs during batch processing - ",e);
			logger.error("error occurs during batch processing - error message# - {}",e.getMessage());
		}
		p6EllipseService.clearApplicationMemory();
		logger.info("Time taken to complete the batch in milisecond - {} ms", System.currentTimeMillis()-startTime);
		return STATUS_COMLETED;
	}
}
