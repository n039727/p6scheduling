/**
 * 
 */
package au.com.wp.corp.p6.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.business.P6EllipseIntegrationService;
import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.exception.P6BaseException;

/**
 * @author N039126
 *
 */
@RestController
@RequestMapping("/integration")
public class P6EllipseIntegrationController {
	private static final String STATUS_COMLETED = "OK";
	
	private static final String STATUS_FAILED = "NOTOK";
	
	@Autowired 
	P6EllipseIntegrationService p6EllipseService;
	
	@RequestMapping(value = "/p6-ellipse", produces = MediaType.TEXT_PLAIN_VALUE)
	public String startIntegartion (){
		P6ActivityDTO activityDTO = new P6ActivityDTO();
		activityDTO.setActivityId("WO111");
		try {
			p6EllipseService.startEllipseToP6Integration();
		} catch (P6BaseException e) {
			return STATUS_FAILED;
		}
		return STATUS_COMLETED;
	}
}
