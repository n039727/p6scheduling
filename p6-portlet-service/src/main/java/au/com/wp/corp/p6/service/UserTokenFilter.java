package au.com.wp.corp.p6.service;

import java.io.IOException;

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
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.dto.UserTokenRequest;

@Component("userTokenFilter")
public class UserTokenFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(UserTokenFilter.class);
	
	@Autowired
	private UserTokenRequest userTokenRequest;
	
	private static final String AUTH_TOKEN_HEADER = "AUTH_TOKEN";
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		String authToken = httpServletRequest.getHeader(AUTH_TOKEN_HEADER);
		logger.debug("User logged in (From token) as {}", authToken);
		
			userTokenRequest.setUserPrincipal(authToken);
			
			chain.doFilter(request, response);
		
	}
	

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
	
	
	
}
