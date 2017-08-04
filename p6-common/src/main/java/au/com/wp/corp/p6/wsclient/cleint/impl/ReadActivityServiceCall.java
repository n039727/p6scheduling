/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.ActivityFieldType;
import au.com.wp.corp.p6.wsclient.activity.IntegrationFault;
import au.com.wp.corp.p6.wsclient.cleint.qualifier.ReadActivity;

/**
 * @author n039126
 *
 */
@Component
@ReadActivity
public class ReadActivityServiceCall extends ActivityServiceCall<List<Activity>> {
	private static final Logger logger = LoggerFactory.getLogger(ReadActivityServiceCall.class);

	private String filter;

	public ReadActivityServiceCall() throws P6ServiceException {
		super();
	}

	@Override
	protected List<Activity> command() throws P6ServiceException {
		logger.info("Calling activity service to read activities ");
		List<ActivityFieldType> fields = new ArrayList<>();

		fields.add(ActivityFieldType.PLANNED_START_DATE);
		fields.add(ActivityFieldType.ID);
		fields.add(ActivityFieldType.OBJECT_ID);
		fields.add(ActivityFieldType.NAME);
		fields.add(ActivityFieldType.ACTUAL_FINISH_DATE);
		fields.add(ActivityFieldType.ACTUAL_START_DATE);
		fields.add(ActivityFieldType.PROJECT_OBJECT_ID);
		fields.add(ActivityFieldType.PRIMARY_RESOURCE_ID);
		fields.add(ActivityFieldType.PLANNED_DURATION);
		fields.add(ActivityFieldType.REMAINING_DURATION);
		fields.add(ActivityFieldType.PLANNED_LABOR_UNITS);
		fields.add(ActivityFieldType.STATUS);

		List<Activity> activities;
		try {
			activities = servicePort.readActivities(fields, filter, null);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		} finally {
			this.filter = null;
		}

		return activities;
	}

	@Override
	public List<Activity> readActivities(final String filter) throws P6ServiceException {
		this.filter = filter;
		return run();
	}

}
