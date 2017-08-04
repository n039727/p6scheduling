/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.IntegrationFault;
import au.com.wp.corp.p6.wsclient.cleint.qualifier.UpdateActivity;

/**
 * @author n039126
 *
 */
@Component
@UpdateActivity
public class UpdateActivityServiceCall extends ActivityServiceCall<Boolean> {
	private static final Logger logger1 = LoggerFactory.getLogger(UpdateActivityServiceCall.class);

	private List<Activity> activities;

	public UpdateActivityServiceCall() throws P6ServiceException{
		super();
	}

	@Override
	protected Boolean command() throws P6ServiceException {
		logger1.info("Calling activity service to update activity.....");
		boolean status;
		try {
			status = servicePort.updateActivities(activities);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		} finally {
			activities = null;
		}

		return status;
	}
	
	@Override
	public boolean updateActivities (List<Activity> activities) throws P6ServiceException {
		this.activities = activities;
		boolean status = run();
		return status;
	}

}
