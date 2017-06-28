/**
 * 
 */
package au.com.wp.corp.p6.integration.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wp.snow.SnowConnector;

import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;

/**
 * @author N039126
 *
 */

public class P6IntegrationExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(P6IntegrationExceptionHandler.class);

	private static final String APP_SUPPORT_GROUP = "APP_SUPPORT_GROUP";
	private static final String SERVICE_TICKET_PRIORITY = "SERVICE_TICKET_PRIORITY";
	private static final String EXCEPTION_SHORT_DESC = "EXCEPTION_SHORT_DESC";
	private static final String EXCEPTION_DETAIL_DESC_TEMPLATE = "EXCEPTION_DETAIL_DESC_TEMPLATE";

	public static void handleDataExeception(P6BaseException e) {

	}

	/**
	 * 
	 * @param e
	 */
	public static void handleTechnicalException ( P6BaseException e ){
		logger.debug("Calling SNow API to register a service ticket............... ");
		logger.error("Short description -- {}", e.getCause().getMessage());
		logger.error("Log details ---------------------------- ", e.getCause());
		final String appName= "P6-Ellipse Integration"; 
		final String sDescPattern = P6ReloadablePropertiesReader.getProperty(EXCEPTION_SHORT_DESC);
		final String sDesc = formatMessage(sDescPattern, e.getCause().getMessage());
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		final String descPattern = P6ReloadablePropertiesReader.getProperty(EXCEPTION_DETAIL_DESC_TEMPLATE);
		final String desc = formatMessage(descPattern, sw.toString());	
		String aGroup = P6ReloadablePropertiesReader.getProperty(APP_SUPPORT_GROUP);
		int priority = Integer.parseInt(P6ReloadablePropertiesReader.getProperty(SERVICE_TICKET_PRIORITY));
		SnowConnector snow = new SnowConnector();
		String status = snow.create(appName,  sDesc,  desc,  aGroup,  priority);
		logger.debug("Service Now Status With ticket no # {}", status);
		
	}

	private static final String formatMessage(String pattern, String value) {
		return MessageFormat.format(pattern, value);
	}

	public static void handleException(P6BaseException e) {
		if (P6ExceptionType.SYSTEM_ERROR.name().equals(e.getMessage())) {
			handleTechnicalException(e);
		} else {
			handleDataExeception(e);
		}
	}

}
