/**
 * 
 */
package au.com.wp.corp.p6.scheduling.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author n039126
 *
 */
@RestController
public class UserDetailsController {

	@RequestMapping(value= "/user/name", method = RequestMethod.GET)
	public Map<String, String> getUserName ( HttpServletRequest request ){
		 Map<String, String>  userName = new HashMap<>();
		 userName.put("userName", request.getRemoteUser());
		return userName;
	}

}
