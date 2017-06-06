/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.activity.IntegrationFault;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;

/**
 * @author n039126
 *
 */
public class DeleteActivityServiceCall extends ActivityServiceCall<Boolean> {
	private static final Logger logger = LoggerFactory.getLogger(DeleteActivityServiceCall.class);

	private final List<Integer> activitieIds;

	public DeleteActivityServiceCall(final RequestTrackingId trackingId, final List<Integer> activitieIds) {
		super(trackingId);
		this.activitieIds = activitieIds;
	}

	@Override
	protected Holder<Boolean> command() throws P6ServiceException {
		logger.info("Calling Activity Service to delete activities...");
		boolean status;
		try {
			status = servicePort.deleteActivities(activitieIds);
		} catch (IntegrationFault e) {
			throw new P6ServiceException(e);
		}

		return new Holder<>(status);
	}

}
