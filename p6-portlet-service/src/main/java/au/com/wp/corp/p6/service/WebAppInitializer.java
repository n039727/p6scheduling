package au.com.wp.corp.p6.service;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import au.com.wp.corp.p6.config.AppConfig;

/**
 * @author Saikat Ganguly
 *
 */
public class WebAppInitializer implements WebApplicationInitializer {
	
	private static final Logger logger = LoggerFactory.getLogger(WebAppInitializer.class);
	public void onStartup(ServletContext servletContext) throws ServletException {
		logger.debug("Starting up web application");
		AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
		ctx.setServletContext(servletContext);
		ctx.register(AppConfig.class);
		servletContext.addListener(RequestContextListener.class);
		Dynamic dynamic = servletContext.addServlet("dispatcher", new DispatcherServlet(ctx));
		dynamic.addMapping("/");
		dynamic.setLoadOnStartup(1);
		logger.debug("Started up web application");
		/*javax.servlet.FilterRegistration.Dynamic filter = servletContext.addFilter("hibernateFilter", org.springframework.orm.hibernate4.support.OpenSessionInViewFilter.class);
		filter.addMappingForUrlPatterns(null, true, "/*");	
		filter.setInitParameter("sessionFactoryBeanName", "sessionFactory");
	
	    logger.debug("Filter for User token registered up web application");*/
		
		servletContext.addFilter("userTokenFilter", new DelegatingFilterProxy("userTokenFilter")).
			addMappingForUrlPatterns(null, true, "/*");
		
		
	}
}