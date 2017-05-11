package au.com.wp.corp.p6.service;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import au.com.wp.corp.p6.dto.UserTokenRequest;

public class UserTokenFilter implements Filter {
	private Logger logger = null;
	
	@Autowired
	UserTokenRequest userTokenRequest;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		Principal principal = httpServletRequest.getUserPrincipal();
		String userName = "";
		//logger.debug("requesting path for {}",httpServletRequest.getRequestURI());
		if(principal != null){
			userName = httpServletRequest.getRemoteUser();
			userTokenRequest.setUserPrincipal(userName);
			logger.debug("User logged in as {}", userName);
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		logger = LoggerFactory.getLogger(getClass());
		logger.info("UserTokenFilter initiated");
		
	}

	
	
	
}
