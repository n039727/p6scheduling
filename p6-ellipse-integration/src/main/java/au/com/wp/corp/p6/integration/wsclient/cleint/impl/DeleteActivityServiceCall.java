/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.DeleteActivity;
import au.com.wp.corp.p6.wsclient.activity.IntegrationFault;

/**
 * @author n039126
 *
 */
@Component
@DeleteActivity
public class DeleteActivityServiceCall extends ActivityServiceCall<Boolean> {
	

	private static final Logger logger = LoggerFactory.getLogger(DeleteActivityServiceCall.class);

	private List<Integer> activitieIds;
	
	public DeleteActivityServiceCall() throws P6ServiceException {
		super();
	}
	
	

	@Override
	protected Boolean command() throws P6ServiceException {
		logger.info("Calling Activity Service to delete activities...");
		boolean status;
		try {
			status = servicePort.deleteActivities(activitieIds);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		} finally{
			this.activitieIds = null;
		}

		return status;
	}
	
	
	@Override
	protected boolean deleteActivities(List<Integer> activitieIds) throws P6ServiceException{
		this.activitieIds = activitieIds;
		
		return run();
	}

}
