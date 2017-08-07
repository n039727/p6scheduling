/**
 * 
 */
package au.com.wp.corp.p6.service.impl;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import au.com.wp.corp.p6.dto.ErrorResponse;
import au.com.wp.corp.p6.exception.P6ExceptionMapper;

/**
 * @author n039126
 *
 */
@PropertySource("file:/${properties.dir}/p6portal.properties")
@RestControllerAdvice
public class P6ControllerAdvice {
	
	private static final Logger logger = LoggerFactory.getLogger(P6ControllerAdvice.class);
	private static PropertiesConfiguration configuration = null;
	static {
		try {
			final String propFilePath = System.getProperty("properties.dir");
			configuration = new PropertiesConfiguration(propFilePath + File.separator + "p6portal.properties");
		} catch (ConfigurationException e) {
			logger.debug("An error ocurrs while reading properties file : ", e);
		}
		configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
		
		logger.error("The following error occurred: ", ex);
		
		ErrorResponse error = new ErrorResponse();
		if(ex != null && ex.getMessage() != null)
		{
			if(null !=  configuration.getProperty(ex.getMessage())){
				error.setErrorCode(ex.getMessage().trim());
				error.setErrorMessage((String) configuration.getProperty(ex.getMessage().trim()));
			}
			else{
				error.setErrorCode(P6ExceptionMapper.INTERNAL_APPLICATION_ERROR);
				error.setErrorMessage((String) configuration.getProperty(P6ExceptionMapper.INTERNAL_APPLICATION_ERROR));
			}
		}else{
			error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
			error.setErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
		}
		
		return new ResponseEntity<ErrorResponse>(error, HttpStatus.PARTIAL_CONTENT);
	}

}
