/**
 * 
 */
package au.com.wp.corp.p6.scheduling.controller;

import javax.servlet.http.HttpServletRequest;

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
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(HttpServletRequest request) {
		
		logger.debug("Enter into P6 Scheduling Application ...");
		
		return new ModelAndView("redirect:/web/home");
	}

	@RequestMapping(value = "/web/home", method = RequestMethod.GET)
	public String mediahome(HttpServletRequest request) {

		logger.debug("request attributes - is user role 'amr_users_non_prod'- {} , user name- {}, session id - {} ",
				request.isUserInRole("amr_users_non_prod"), request.getRemoteUser(), request.getSession().getId());
		return "welcome";
	}

}
