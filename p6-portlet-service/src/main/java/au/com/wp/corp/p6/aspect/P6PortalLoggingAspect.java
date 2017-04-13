package au.com.wp.corp.p6.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class P6PortalLoggingAspect {
	private static final Logger logger = LoggerFactory.getLogger(P6PortalLoggingAspect.class);
	/*   @Pointcut("within(au.com.wp.corp.p6..*) && !within(au.com.wp.corp.p6.config.AppConfig*) ")
	    private void logAround(){}*/

	    // Use "logAround" pointcut declaration in the advice
	   @Before("within(au.com.wp.corp.p6..*.*) && !within(au.com.wp.corp.p6.config..*)")
	    public void logBefore(JoinPoint joinPoint) {
	    	String nameOfMethod = joinPoint.getSignature().getName();
	    	String arguments = Arrays.toString(joinPoint.getArgs());
	        logger.info("Before1: " + nameOfMethod + " gets called with " + arguments);
	    }
	    
	    // Use "logAround" pointcut declaration in the advice
	   @After("within(au.com.wp.corp.p6..*.*) && !within(au.com.wp.corp.p6.config..*)")
	    public void logAfterReturning(JoinPoint joinPoint) {
	    	String nameOfMethod = joinPoint.getSignature().getName();
	    	String arguments = Arrays.toString(joinPoint.getArgs());
	        logger.info("AfterReturning1: " + nameOfMethod + " gets called with " + arguments);
	    }
	  
}
