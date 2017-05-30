/**
 * 
 */
package au.com.wp.corp.p6.scheduling.auth;

import org.springframework.stereotype.Component;

/**
 * @author n039619
 *
 */
@Component
public class UserRoleExtractor {

	
	public String extract(String userName) {
		return "P6_TEM_LEDR_SCHDLR";
	}
}
