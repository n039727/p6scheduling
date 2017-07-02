/**
 * 
 */
package au.com.wp.corp.p6.integration.exception;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.wp.snow.SnowConnector;

import au.com.wp.corp.integration.genos.client.GenosClientService;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;

/**
 * @author N039126
 *
 */

@Component
public class P6IntegrationExceptionHandler {
	private static Logger logger = LoggerFactory.getLogger(P6IntegrationExceptionHandler.class);
	private static final String ENABLE_BUSINESS_NOTIFICATION = "ENABLE_BUSINESS_NOTIFICATION";

	private static final String ENABLE_BUSINESS_NOTIFICATION_VALUE = "Y";

	private static final String APP_SUPPORT_GROUP = "P6.APP.SUPPORT.GROUP";
	private static final String SERVICE_TICKET_PRIORITY = "SERVICE_TICKET_PRIORITY";
	private static final String EXCEPTION_SHORT_DESC = "EXCEPTION_SHORT_DESC";
	private static final String EXCEPTION_DETAIL_DESC_TEMPLATE = "EXCEPTION_DETAIL_DESC_TEMPLATE";
	private static final String APPLICATION_NAME = "APPLICATION_NAME";
	private static final String BUSINESS_NOTIFICATION_EAMIL_SUBJECT = "BUSINESS_NOTIFICATION_EAMIL_SUBJECT";
	private static final String BUSINESS_NOTIFICATION_EAMIL_BODY = "BUSINESS_NOTIFICATION_EAMIL_BODY";

	private static final String MSG_TYPE_ID_MT = "MSG_TYPE_ID_MT";

	@Autowired
	private GenosClientService genosClientService;

	/**
	 * notify business in case of data error
	 * 
	 * @param e
	 */
	@Async
	public void handleDataExeception() {
		logger.info("sending email to notify business about the data error ... ");
		try {
			if (!CacheManager.getDataErrors().isEmpty()) {
				List<Exception> dataErrors = CacheManager.getDataErrors();
				final StringBuilder errors = new StringBuilder();
				for (Exception e : dataErrors) {
					errors.append(e.getMessage());
					errors.append("<br>");
				}
				
				final String emailSubject = P6ReloadablePropertiesReader
						.getProperty(BUSINESS_NOTIFICATION_EAMIL_SUBJECT);
				final String emailBody = formatMessage(
						P6ReloadablePropertiesReader.getProperty(BUSINESS_NOTIFICATION_EAMIL_BODY), errors.toString());
				logger.info("Data errors - {}", emailBody);
				final Integer msgTypeID = Integer.parseInt(P6ReloadablePropertiesReader.getProperty(MSG_TYPE_ID_MT));
				// Integer msgType, String message, String context, String
				// comment
				long id = genosClientService.sendMessage(msgTypeID, emailSubject, emailBody);

				logger.info("sent email to notify business with the message id # {}", id);

			}
		} catch (Exception e) {
			logger.error("An Error occurs while sending business notification with Genos Email service: ", e);
		} finally {
			CacheManager.getDataErrors().clear();
		}
	}

	/**
	 * creating service ticket in case of system failure
	 * 
	 * @param e
	 */
	@Async
	private void handleTechnicalException(P6BaseException e) {
		logger.debug("Calling SNow API to register a service ticket............... ");
		logger.error("Short description -- {}", e.getCause().getMessage());
		logger.error("Log details ---------------------------- ", e.getCause());
		final String appName = P6ReloadablePropertiesReader.getProperty(APPLICATION_NAME);
		final String sDescPattern = P6ReloadablePropertiesReader.getProperty(EXCEPTION_SHORT_DESC);
		final String sDesc = formatMessage(sDescPattern, e.getCause().getMessage());
		final String descPattern = P6ReloadablePropertiesReader.getProperty(EXCEPTION_DETAIL_DESC_TEMPLATE);
		final String desc = formatMessage(descPattern, e.getCause().getMessage());
		String aGroup = System.getProperty(APP_SUPPORT_GROUP);
		logger.debug(" groups to be assigned the service ticket - {}", aGroup);
		int priority = Integer.parseInt(P6ReloadablePropertiesReader.getProperty(SERVICE_TICKET_PRIORITY));
		SnowConnector snow = new SnowConnector();
		String status = snow.create(appName, sDesc, desc, aGroup, priority);
		logger.debug("Service Now Status With ticket no # {}", status);

	}

	private final String formatMessage(String pattern, String value) {
		return MessageFormat.format(pattern, value);
	}

	/**
	 * handling exceptions
	 * 
	 * @param e
	 */
	public void handleException(P6BaseException e) {
		if (P6ExceptionType.SYSTEM_ERROR.name().equals(e.getMessage())) {
			handleTechnicalException(e);
		} else {
			final String enableBusinessNotification = P6ReloadablePropertiesReader
					.getProperty(ENABLE_BUSINESS_NOTIFICATION);
			if (ENABLE_BUSINESS_NOTIFICATION_VALUE.equalsIgnoreCase(enableBusinessNotification)) {
				CacheManager.getDataErrors().add(e);
			}

		}
	}

}
