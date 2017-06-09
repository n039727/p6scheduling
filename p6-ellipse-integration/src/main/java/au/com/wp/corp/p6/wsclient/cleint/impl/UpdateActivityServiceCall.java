/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.IntegrationFault;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;

/**
 * @author n039126
 *
 */
public class UpdateActivityServiceCall extends ActivityServiceCall<Boolean> {
	private static final Logger logger1 = LoggerFactory.getLogger(UpdateActivityServiceCall.class);

	private final List<Activity> activities;

	public UpdateActivityServiceCall(final RequestTrackingId trackingId, final List<Activity> activities) {
		super(trackingId);
		this.activities = activities;
	}

	@Override
	protected Holder<Boolean> command() throws P6ServiceException {
		logger1.info("Calling activity service to update activity.....");
		boolean status;
		try {
			status = servicePort.updateActivities(activities);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}

		return new Holder<>(status);
	}

}
