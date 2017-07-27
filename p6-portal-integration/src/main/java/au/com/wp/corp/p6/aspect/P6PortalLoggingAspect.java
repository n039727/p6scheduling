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
	
	    // Use "logAround" pointcut declaration in the advice
	   @Before("within(au.com.wp.corp.p6..*) && !within(au.com.wp.corp.p6.config..*) && !within(au.com.wp.corp.p6.utils.DateUtils*) "
	   		+ "&& !within(au.com.wp.corp.p6.integration.util.DateUtil*)"
	   		+ "&& !within(au.com.wp.corp.p6.integration.wsclient.cleint.impl.P6WSClientImpl.getWorkOrderIdMap()*)"
	   		+ "&& !within(au.com.wp.corp.p6.dto..*)")
	    public void logBefore(JoinPoint joinPoint) {
		   String[] retval = returnMetaValues(joinPoint);
			logger.info("After returning: {} gets called with {} from {}", retval[0], retval[1],retval[2]);
	    }
	    
	    // Use "logAround" pointcut declaration in the advice
	   @After("within(au.com.wp.corp.p6..*) && !within(au.com.wp.corp.p6.config..*) && !within(au.com.wp.corp.p6.utils.DateUtils*) "
	   		+ "&& !within(au.com.wp.corp.p6.dto..*)")
	    public void logAfterReturning(JoinPoint joinPoint) {
		   
	    	String[] retval = returnMetaValues(joinPoint);
			logger.info("After returning: {} gets called with {} from {}", retval[0], retval[1],retval[2]);
	    }
	   
	  private String[] returnMetaValues(JoinPoint joinPoint){
		  String nameOfClass = joinPoint.getSignature().getDeclaringTypeName();
	    	String nameOfMethod = joinPoint.getSignature().getName();
	    	String arguments = Arrays.toString(joinPoint.getArgs());
	    	String retval[] = new  String[] {nameOfClass , nameOfMethod , arguments};
	    	
	    	return retval;
	  }
}
