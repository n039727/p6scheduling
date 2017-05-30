/**
 * 
 */
package au.com.wp.corp.p6.scheduling.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.scheduling.businessservice.UserAuthorizationService;
import au.com.wp.corp.p6.scheduling.dto.UserAuthorizationDTO;
import au.com.wp.corp.p6.scheduling.dto.UserDetails;

/**
 * @author n039126
 *
 */
@RestController
public class UserDetailsController {
	
	@Autowired
	private UserAuthorizationService authService;

	@RequestMapping(value= "/user/name", method = RequestMethod.GET)
	public UserDetails getUserName ( HttpServletRequest request ){
		 /*Map<String, String>  userName = new HashMap<>();
		 userName.put("userName", request.getRemoteUser());*/
		Principal userPrincipal = request.getUserPrincipal();
		List<UserAuthorizationDTO> userAccessList = authService.getAccess(userPrincipal.getName());
		Map<String, UserAuthorizationDTO> accessMap = new HashMap<String, UserAuthorizationDTO>();
		
		if (userAccessList != null && !userAccessList.isEmpty()) {
			accessMap = userAccessList.stream().collect(Collectors.toMap(UserAuthorizationDTO::getFunctionName,
                    Function.identity()));
		}
		
		UserDetails userDetails = new UserDetails();
		userDetails.setUserName(userPrincipal.getName());
		userDetails.setAccessMap(accessMap);
		
		return userDetails;
	}

}
