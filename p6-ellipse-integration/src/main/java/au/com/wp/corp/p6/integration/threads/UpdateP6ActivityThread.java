/**
 * 
 */
package au.com.wp.corp.p6.integration.threads;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6IntegrationExceptionHandler;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadWriteProcessStatus;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;

/**
 * @author N039126
 *
 */
public class UpdateP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(UpdateP6ActivityThread.class);

	private final List<P6ActivityDTO> updateActivityP6Set;

	private final P6WSClient p6WSClient;
	
	private final P6IntegrationExceptionHandler exceptionHandler;
	

	public UpdateP6ActivityThread(final List<P6ActivityDTO> updateActivityP6Set, final P6WSClient p6WSClient, final P6IntegrationExceptionHandler exceptionHandler) {
		this.updateActivityP6Set = updateActivityP6Set;
		this.p6WSClient = p6WSClient;
		this.exceptionHandler = exceptionHandler;
		CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS,
				ReadWriteProcessStatus.STARTED);
	}

	@Override
	public void run() {
		logger.info("Initiates Update Activities in P6 thread ....");
		try {
			if (!updateActivityP6Set.isEmpty())
				p6WSClient.updateActivities(updateActivityP6Set);
		} catch (P6ServiceException e) {
			logger.error("An error occur while updating activities in P6 ", e);
			exceptionHandler.handleException(e);
		}finally {
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS,
					ReadWriteProcessStatus.COMPLETED);
		}
	}

}
