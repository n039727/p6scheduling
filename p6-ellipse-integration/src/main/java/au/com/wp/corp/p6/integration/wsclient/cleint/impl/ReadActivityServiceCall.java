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
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.ActivityFieldType;
import au.com.wp.corp.p6.wsclient.activity.IntegrationFault;

/**
 * @author n039126
 *
 */
public class ReadActivityServiceCall extends ActivityServiceCall<List<Activity>> {
	private static final Logger logger = LoggerFactory.getLogger(ReadActivityServiceCall.class);
	
	private final String filter;
	
	public ReadActivityServiceCall(final RequestTrackingId trackingId, final String filter) {
		super(trackingId);
		this.filter = filter;
	}

	@Override
	protected Holder<List<Activity>> command() throws P6ServiceException {
		logger.info("Calling activity service to read activities ");
		List<ActivityFieldType> fields = new ArrayList<>();
		
		fields.add(ActivityFieldType.PLANNED_START_DATE );
		//fields.add(ActivityFieldType.PLANNED_FINISH_DATE );
		fields.add(ActivityFieldType.ID);
		fields.add(ActivityFieldType.OBJECT_ID);
		fields.add(ActivityFieldType.NAME);
		//fields.add(ActivityFieldType.PRIMARY_RESOURCE_OBJECT_ID);
		//fields.add(ActivityFieldType.PRIMARY_RESOURCE_NAME);
		fields.add(ActivityFieldType.PROJECT_OBJECT_ID);
		fields.add(ActivityFieldType.PRIMARY_RESOURCE_ID);
		fields.add(ActivityFieldType.PLANNED_DURATION);
		fields.add(ActivityFieldType.REMAINING_DURATION);
		fields.add(ActivityFieldType.STATUS);
		
		List<Activity> activities;
		try {
			activities = servicePort.readActivities(fields, filter, null);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}
		
		return new Holder<>(activities);
	}


}
