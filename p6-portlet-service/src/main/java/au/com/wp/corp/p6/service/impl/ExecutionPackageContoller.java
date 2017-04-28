/**
 * 
 */
package au.com.wp.corp.p6.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.businessservice.IExecutionPackageService;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.validation.Validator;

/**
 * Exposes the create / update execution package service
 * 
 * @author n039126
 * @version 1.0
 */
@RestController
@RequestMapping("/scheduler")
public class ExecutionPackageContoller {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionPackageContoller.class);
	@Autowired
	private IExecutionPackageService executionPackageService;
	@Autowired
	Validator validator;

	@RequestMapping(value = "/executionpackage/createOrUpdate", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ExecutionPackageDTO> createOrUpdateExecutionPackages(
			RequestEntity<ExecutionPackageDTO> executionPackageDTO, HttpServletRequest request) throws P6BaseException {
		logger.info(" create or update service is called ....");
		String userName = "N039126";
		validator.validate(executionPackageDTO.getBody());
		return new ResponseEntity<ExecutionPackageDTO>(
				executionPackageService.createOrUpdateExecutionPackage(executionPackageDTO.getBody(), userName),
				HttpStatus.CREATED);
	}

}
