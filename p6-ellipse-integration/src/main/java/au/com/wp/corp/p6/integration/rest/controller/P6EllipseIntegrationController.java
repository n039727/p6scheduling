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

/**
 * @author N039126
 *
 */
@RestController
@RequestMapping("/integration")
public class P6EllipseIntegrationController {
	private static final Logger logger = LoggerFactory.getLogger(P6EllipseIntegrationController.class);
	
	private static final String STATUS_COMLETED = "OK";
	
	private static final String STATUS_FAILED = "NOTOK";
	
	@Autowired 
	P6EllipseIntegrationService p6EllipseService;
	
	@RequestMapping(value = "/p6-ellipse", produces = MediaType.TEXT_PLAIN_VALUE)
	public String startIntegartion (){
		try {
			p6EllipseService.startEllipseToP6Integration();
		} catch (P6BaseException e) {
			logger.error("An error occurs during batch processing - ",e);
			p6EllipseService.clearApplicationMemory();
			return STATUS_FAILED;
		}
		p6EllipseService.clearApplicationMemory();
		return STATUS_COMLETED;
	}
}
