/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.resourceassignment.IntegrationFault;
import au.com.wp.corp.p6.wsclient.resourceassignment.ResourceAssignment;
import au.com.wp.corp.p6.wsclient.resourceassignment.ResourceAssignmentFieldType;

/**
 * @author n039126
 *
 */
public class ReadResourceAssignmentServiceCall extends ResourceAssignmentServiceCall<List<ResourceAssignment>> {
	private static final Logger logger = LoggerFactory.getLogger(ReadResourceAssignmentServiceCall.class);
	
	private final String filter;
	
	public ReadResourceAssignmentServiceCall(final RequestTrackingId trackingId, final String filter) {
		super(trackingId);
		this.filter = filter;
	}

	@Override
	protected Holder<List<ResourceAssignment>> command() throws P6ServiceException {
		logger.info("Calling activity service to read activities ");
		List<ResourceAssignmentFieldType> fields = new ArrayList<>();
		
		fields.add(ResourceAssignmentFieldType.OBJECT_ID );
		fields.add(ResourceAssignmentFieldType.PLANNED_DURATION);
		
		List<ResourceAssignment> activities;
		try {
			activities = servicePort.readResourceAssignments(fields, filter, null);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}
		
		return new Holder<>(activities);
	}


}
