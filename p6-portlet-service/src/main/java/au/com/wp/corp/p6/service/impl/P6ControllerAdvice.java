/**
 * 
 */
package au.com.wp.corp.p6.service.impl;

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
	
	@Autowired
	private Environment environment; 
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode(ex.getMessage().split(":")[1].trim());
		error.setErrorMessage(environment.getProperty(ex.getMessage().split(":")[1].trim()));
		return new ResponseEntity<>(error, HttpStatus.OK);
	}

}
