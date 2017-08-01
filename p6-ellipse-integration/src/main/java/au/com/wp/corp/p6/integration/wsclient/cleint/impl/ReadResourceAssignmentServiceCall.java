/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.resourceassignment.IntegrationFault;
import au.com.wp.corp.p6.wsclient.resourceassignment.ResourceAssignment;
import au.com.wp.corp.p6.wsclient.resourceassignment.ResourceAssignmentFieldType;

/**
 * @author n039126
 *
 */
@Component
public class ReadResourceAssignmentServiceCall extends ResourceAssignmentServiceCall<List<ResourceAssignment>> {
	private static final Logger logger = LoggerFactory.getLogger(ReadResourceAssignmentServiceCall.class);
	
	private String filter;
	
	public ReadResourceAssignmentServiceCall() throws P6ServiceException {
		super();
		
	}

	@Override
	protected List<ResourceAssignment> command() throws P6ServiceException {
		logger.info("Calling activity service to read activities ");
		List<ResourceAssignmentFieldType> fields = new ArrayList<>();
		
		fields.add(ResourceAssignmentFieldType.OBJECT_ID );
		fields.add(ResourceAssignmentFieldType.PLANNED_DURATION);
		
		List<ResourceAssignment> activities;
		try {
			activities = servicePort.readResourceAssignments(fields, filter, null);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		} finally{
			this.filter = null;
		}
		
		return activities;
	}

	@Override
	public List<ResourceAssignment> readResourceAssigment(final String filter) throws P6ServiceException{
		this.filter = filter;
		return run();
	}

}
