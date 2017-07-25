/**
 * 
 */
package au.com.wp.corp.p6.integration.exception;

import java.text.MessageFormat;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.integration.genos.client.GenosClientService;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6IntegrationExceptionHandlerTest {

	private static final String BUSINESS_NOTIFICATION_EAMIL_COMMENT = "BUSINESS_NOTIFICATION_EAMIL_COMMENT";
	private static final String BUSINESS_NOTIFICATION_EAMIL_CONTEXT = "BUSINESS_NOTIFICATION_EAMIL_CONTEXT";
	private static final String BUSINESS_NOTIFICATION_EAMIL_MSG = "BUSINESS_NOTIFICATION_EAMIL_MSG";

	private static final String MSG_TYPE_ID_MT = "MSG_TYPE_ID_MT";

	@InjectMocks
	P6IntegrationExceptionHandler exceptionHandler;

	@Mock
	GenosClientService genosClientService;

	@Before
	public void setup (){
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void testHandleException1() {
		boolean status = exceptionHandler.handleException(
				new P6BaseException(P6ExceptionType.SYSTEM_ERROR.name(), new Exception("Unable to connect P6")));
		
		Assert.assertTrue(status);
	}

	private final String formatMessage(String pattern, String value) {
		return MessageFormat.format(pattern, value);
	}

	@Test
	public void testHandleException2() {
		exceptionHandler.handleException(new P6BaseException(P6ExceptionType.DATA_ERROR.name(),
				new Exception("Invalid workorder task-4966048595001")));
		if (!CacheManager.getDataErrors().isEmpty()) {
			List<Exception> dataErrors = CacheManager.getDataErrors();
			final StringBuilder errors = new StringBuilder();
			for (Exception e : dataErrors) {
				errors.append(e.getMessage());
				errors.append("<br>");
			}

			final String emailComment = P6ReloadablePropertiesReader
					.getProperty(BUSINESS_NOTIFICATION_EAMIL_COMMENT);

			final String emailMsg = P6ReloadablePropertiesReader.getProperty(BUSINESS_NOTIFICATION_EAMIL_MSG);

			final String emailContext = formatMessage(
					P6ReloadablePropertiesReader.getProperty(BUSINESS_NOTIFICATION_EAMIL_CONTEXT),
					errors.toString());
			final Integer msgTypeID = Integer.parseInt(P6ReloadablePropertiesReader.getProperty(MSG_TYPE_ID_MT));


			Mockito.when(genosClientService.sendMessage(msgTypeID, emailMsg, emailContext, emailComment)).thenReturn(123L);

			Assert.assertEquals(123L, exceptionHandler.handleDataExeception());
		}
	}
}
