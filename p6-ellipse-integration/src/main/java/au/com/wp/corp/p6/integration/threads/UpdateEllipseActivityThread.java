/**
 * 
 */
package au.com.wp.corp.p6.integration.threads;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6ExceptionType;
import au.com.wp.corp.p6.integration.exception.P6IntegrationExceptionHandler;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadWriteProcessStatus;
import au.com.wp.corp.p6.integration.wsclient.ellipse.EllipseWSClient;

/**
 * Thread to initiates job to update the activity (work order task ) in Ellipse
 * 
 * @author N039126
 * @version 1.0
 * 
 */
public class UpdateEllipseActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(UpdateEllipseActivityThread.class);

	private final List<EllipseActivityDTO> updateActivityEllipseSet;

	private final EllipseWSClient ellipseWSClient;

	public UpdateEllipseActivityThread(final List<EllipseActivityDTO> updateActivityEllipseSet,
			final EllipseWSClient ellipseWSClient) {
		this.updateActivityEllipseSet = updateActivityEllipseSet;
		this.ellipseWSClient = ellipseWSClient;
	}

	@Override
	public void run() {
		logger.info("Initiates update Activities in Ellipse thread ....");
		try {
			if (!updateActivityEllipseSet.isEmpty())
				ellipseWSClient.updateActivitiesEllipse(updateActivityEllipseSet);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_UPDATE_STATUS,
					ReadWriteProcessStatus.COMPLETED);
		} catch (P6ServiceException e) {
			logger.error("An error occurs while updating ellipse activity : ", e);
			if (P6ExceptionType.SYSTEM_ERROR.name().equals(e.getMessage())){
				CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_UPDATE_STATUS,
						ReadWriteProcessStatus.FAILED);
			} else{
				CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_UPDATE_STATUS,
						ReadWriteProcessStatus.COMPLETED);
			}
			P6IntegrationExceptionHandler.handleException(e);
		}
	}

}
