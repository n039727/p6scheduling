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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import au.com.wp.corp.p6.dto.ErrorResponse;

/**
 * @author n039126
 *
 */
@PropertySource("file:/${properties.dir}/p6portal.properties")
@RestControllerAdvice
public class P6ControllerAdvice {
	
	private static final Logger logger = LoggerFactory.getLogger(P6ControllerAdvice.class);
	
	@Autowired
	private Environment environment; 
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
		error.setErrorCode(ex.getMessage().split(":")[1].trim());
		error.setErrorMessage((String) configuration.getProperty(ex.getMessage().split(":")[1].trim()));
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
