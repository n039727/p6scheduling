/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.IntegrationFault;

/**
 * @author n039126
 *
 */
public class CreateActivityServiceCall extends ActivityServiceCall<List<Integer>> {
	private static final Logger logger = LoggerFactory.getLogger(CreateActivityServiceCall.class);

	private final List<Activity> activities;

	public CreateActivityServiceCall(final RequestTrackingId trackingId, final List<Activity> activities) {
		super(trackingId);
		this.activities = activities;
	}

	@Override
	protected Holder<List<Integer>> command() throws P6ServiceException {
		logger.info("Calling activity service to create activity.....");
		List<Integer> activitieIds;
		try {
			activitieIds = servicePort.createActivities(activities);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e.getMessage(), e);
		}

		return new Holder<>(activitieIds);
	}

}
