/**
 * 
 */
package au.com.wp.corp.p6.scheduling.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author n039126
 *
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String mediahome(HttpServletRequest request) {

		logger.debug("request attributes - is user role 'amr_users_non_prod'- {} , user name- {}, session id - {} ",
				request.isUserInRole("p6_portal_users_non_prod"), request.getRemoteUser(), request.getSession().getId());
		return "welcome";
	}
	
	
	@RequestMapping("/auth/login")
	public String login(Map<String, Object> map) {

		return "login";
	}
	
	@RequestMapping("/auth/error")
	public String error(Map<String, Object> map) {

		return "error";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(HttpServletRequest request) {
		
		HttpSession session = request.getSession(false);
		if(session != null)
		    session.invalidate();
	
		return new ModelAndView("redirect:/web/home");
	}

}
