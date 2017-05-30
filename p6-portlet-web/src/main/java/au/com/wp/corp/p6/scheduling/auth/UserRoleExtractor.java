/**
 * 
 */
package au.com.wp.corp.p6.scheduling.auth;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.scheduling.dao.FunctionAccessDAO;

/**
 * @author n039619
 *
 */
@Component
public class UserRoleExtractor {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRoleExtractor.class);

	@Autowired
	private FunctionAccessDAO functionAccessDao;
	
	public String extract(HttpServletRequest request) {
		
		List<String> userRoles = functionAccessDao.fetchAllRole();
		String roleName = userRoles.stream().filter(role -> request.isUserInRole(role)).findFirst().get();
		logger.debug("Extracted role name: " + roleName);
		return roleName;
	}
}
