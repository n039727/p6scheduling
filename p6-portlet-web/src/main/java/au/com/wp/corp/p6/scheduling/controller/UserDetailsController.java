/**
 * 
 */
package au.com.wp.corp.p6.scheduling.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.scheduling.auth.UserRoleExtractor;
import au.com.wp.corp.p6.scheduling.businessservice.UserAuthorizationService;
import au.com.wp.corp.p6.scheduling.dto.UserAuthorizationDTO;
import au.com.wp.corp.p6.scheduling.dto.UserDetails;

/**
 * @author n039126
 *
 */
@RestController
@PropertySource("file:/${properties.dir}/p6portal.properties")
public class UserDetailsController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserDetailsController.class);
	
	@Autowired
	private UserAuthorizationService authService;
	@Autowired
	private UserRoleExtractor userRoleExtractor;
	@Value("${P6_AUTH_ENABLED:true}")
	private String authEnabledStr;

	@RequestMapping(value= "/user/name", method = RequestMethod.GET)
	public UserDetails getUserName ( HttpServletRequest request ){
		 /*Map<String, String>  userName = new HashMap<>();
		 userName.put("userName", request.getRemoteUser());*/
		Principal userPrincipal = request.getUserPrincipal();
		List<String> roleNames = userRoleExtractor.extract(request);
		List<UserAuthorizationDTO> userAccessList = authService.getAccess(roleNames);
		Map<String, UserAuthorizationDTO> accessMap = new HashMap<String, UserAuthorizationDTO>();
		
		if (userAccessList != null && !userAccessList.isEmpty()) {
			userAccessList.forEach(useraccess ->{
				String functionname = useraccess.getFunctionName();
				if(accessMap.containsKey(functionname)){
					if(!accessMap.get(functionname).isAccess()){
						accessMap.put(functionname, useraccess);
					}
				}else{
					accessMap.put(functionname, useraccess);
				}
			});
			/*accessMap = userAccessList.stream().collect(Collectors.toMap(UserAuthorizationDTO::getFunctionName,
                    Function.identity()));*/
		}
		
		UserDetails userDetails = new UserDetails();
		userDetails.setUserName(userPrincipal.getName());
		userDetails.setAccessMap(accessMap);
		userDetails.setRoles(roleNames);
		
		boolean authEnabled = true;
		try {
			authEnabled = Boolean.valueOf(authEnabledStr);
			logger.debug("Auth enabled is set as: {}", authEnabled);
		} catch (Exception e) {
			logger.error("Error occurred while parsing auth enabled.", e);
		}
		
		userDetails.setAuthEnabled(authEnabled);
		
		return userDetails;
	}

}
