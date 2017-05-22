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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.wp.corp.p6.businessservice.DepotTodoService;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.validation.Validator;

@RestController
@RequestMapping("/depot")
public class DepotController {
	
	private static final Logger logger = LoggerFactory.getLogger(DepotController.class);
	@Autowired
	private DepotTodoService dpotTodoService;
	@Autowired
	Validator validator;

	@RequestMapping(value = "/viewTodo", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<ViewToDoStatus> viewDepotToDo(
			RequestEntity<WorkOrderSearchRequest> query) throws P6BaseException {
		logger.info(" viewDepotToDo service is called ....");
		
		return new ResponseEntity<ViewToDoStatus>(
				dpotTodoService.fetchDepotTaskForViewToDoStatus(query.getBody()),
				HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateTodo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ResponseEntity<ViewToDoStatus> updateDepotToDo(RequestEntity<ViewToDoStatus> request)
			throws P6BaseException {
		if (request.getBody() == null) {
			logger.error(" Invalid request - {}", request.getBody());
			throw new P6BaseException("invalid request");
		}
		
		return new ResponseEntity<ViewToDoStatus>(dpotTodoService.UpdateDepotToDo(request.getBody()), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/fetchTaskForUpdateTodo", method = RequestMethod.POST, produces = {
			MediaType.APPLICATION_JSON_VALUE }, consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<List<WorkOrder>> fetchTaskForUpdateTodo(
			RequestEntity<WorkOrderSearchRequest> query) throws P6BaseException {
		logger.info(" viewDepotToDo service is called ....");
		
		return new ResponseEntity<List<WorkOrder>>(
				dpotTodoService.fetchDepotTaskForAddUpdateToDo(query.getBody()),
				HttpStatus.OK);
	}
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(value = "/addTodo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE )
	@ResponseBody
	public ResponseEntity<WorkOrder> saveDepotToDo(RequestEntity<WorkOrder> request)
			throws P6BaseException {
		if (request.getBody() == null) {
			logger.error(" Invalid request - {}", request.getBody());
			throw new P6BaseException("invalid request");
		}
		try {
			logger.debug("The json for save to do depot : {}", mapper.writeValueAsString(request.getBody()));
		} catch (JsonProcessingException e) {
			logger.error("Error occurred while printing json ", e);
			e.printStackTrace();
		}
		return new ResponseEntity<WorkOrder>(dpotTodoService.saveDepotToDo(request.getBody()), HttpStatus.OK);
	}

}
