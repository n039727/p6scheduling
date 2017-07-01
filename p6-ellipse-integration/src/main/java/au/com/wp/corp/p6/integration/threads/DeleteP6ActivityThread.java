/**
 * 
 */
package au.com.wp.corp.p6.integration.threads;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6ExceptionType;
import au.com.wp.corp.p6.integration.exception.P6IntegrationExceptionHandler;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadWriteProcessStatus;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;

/**
 * Thread to initiates job to delete the activity (work order task ) in P6
 * 
 * @author N039126
 * @version 1.0
 * 
 */
public class DeleteP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(DeleteP6ActivityThread.class);

	private final List<P6ActivityDTO> deleteActivityP6Set;

	private final P6WSClient p6WSClient;
	
	private final P6IntegrationExceptionHandler exceptionHandler;

	public DeleteP6ActivityThread(final List<P6ActivityDTO> deleteActivityP6Set, final P6WSClient p6WSClient, final P6IntegrationExceptionHandler exceptionHandler) {
		this.deleteActivityP6Set = deleteActivityP6Set;
		this.p6WSClient = p6WSClient;
		this.exceptionHandler = exceptionHandler;
		CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_DELETE_STATUS,
				ReadWriteProcessStatus.STARTED);
	}

	@Override
	public void run() {
		logger.info("Initiates delete Activities in P6 thread ....");
		try {
			if (!deleteActivityP6Set.isEmpty())
				p6WSClient.deleteActivities(deleteActivityP6Set);
		} catch (P6ServiceException e) {
			logger.error("An error occur while deleting activities in P6 ", e);
						
			exceptionHandler.handleDataExeception(e);
		} finally{
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_DELETE_STATUS,
					ReadWriteProcessStatus.COMPLETED);
		}

	}

}
