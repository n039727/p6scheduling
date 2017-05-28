package au.com.wp.corp.p6.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.businessservice.UserAuthorizationService;
import au.com.wp.corp.p6.dto.UserAuthorizationDTO;
import au.com.wp.corp.p6.exception.P6BaseException;

@RestController
@RequestMapping("/userAuth")
public class UserAuthorizationController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExecutionPackageContoller.class);
	@Autowired
	private UserAuthorizationService userAuthorizationService;
	
	@RequestMapping(value = "/searchByRoleName", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ResponseEntity<List<UserAuthorizationDTO>> searchByRoleName(RequestEntity<String> request)
			throws P6BaseException {
		if (request.getBody() == null) {
			logger.error(" Invalid request - {}", request.getBody());
			throw new P6BaseException("invalid request");
		}
		logger.info("Search String >>{}", request.getBody());
		return new ResponseEntity<List<UserAuthorizationDTO>>(userAuthorizationService.getAccess(request.getBody()), HttpStatus.OK);
	}

}
