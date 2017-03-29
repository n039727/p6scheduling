/**
 * 
 */
package au.com.wp.corp.p6.scheduling.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author n039126
 *
 */
@Controller
public class HomeController {
	
	@RequestMapping(value={"/","/web/home"}, method=RequestMethod.GET)
	public String welcome(HttpServletRequest request) {
		return "welcome";
	}
}
