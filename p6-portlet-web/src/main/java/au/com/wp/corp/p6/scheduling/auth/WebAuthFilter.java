/**
 * 
 */
package au.com.wp.corp.p6.scheduling.auth;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author n039619
 *
 */
public class WebAuthFilter implements Filter {
	
	
	private static final Logger logger = LoggerFactory.getLogger(WebAuthFilter.class);

	private static final String AUTH_TOKEN_HEADER = "AUTH_TOKEN";
	
	private AuthTokenBuilder authTokenBuilder = new AuthTokenBuilder(); 

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request; 
		
		logger.debug("Calling WebAuthFilter for request uri: {}", httpRequest.getRequestURI());
		
		String authToken = null;
		Principal userPrincipal = null;
		if ((userPrincipal = httpRequest.getUserPrincipal()) != null) {
			logger.debug("********* The user name: {}", userPrincipal.getName());
			try {
				authToken = authTokenBuilder.build(userPrincipal);
			} catch (AuthException e) {
				logger.error("Could not build Auth token ", e);
			}
		} else {
			logger.debug("********* The user principal is null");
		}
		
		if (!StringUtils.isEmpty(authToken)) {
			logger.debug("Setting auth header as {}", authToken);
			((HttpServletResponse)response).addHeader(AUTH_TOKEN_HEADER, authToken);
		}
		
		chain.doFilter(request, response);
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
