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
import au.com.wp.corp.p6.wsclient.cleint.qualifier.CreateActivity;

/**
 * @author n039126
 *
 */
@Component
@CreateActivity
public class CreateActivityServiceCall extends ActivityServiceCall<List<Integer>> {
	private static final Logger logger = LoggerFactory.getLogger(CreateActivityServiceCall.class);

	private List<Activity> activities;

	public CreateActivityServiceCall() throws P6ServiceException {
		super();
	}
	
	
	@Override
	protected List<Integer> command() throws P6ServiceException {
		logger.info("Calling activity service to create activity.....");
		List<Integer> activitieIds;
		try {
			activitieIds = servicePort.createActivities(activities);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e.getMessage(), e);
		} finally {
			this.activities =  null;
		}

		return activitieIds;
	}
	
	protected List<Integer> createActivities(List<Activity> activities) throws P6ServiceException{
		this.activities = activities;
		List<Integer> activitieIds = run();
		
		return activitieIds;
	}

	
	
}
