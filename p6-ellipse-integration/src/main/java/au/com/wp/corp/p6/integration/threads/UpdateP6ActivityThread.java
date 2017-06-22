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
 * @author N039126
 *
 */
public class UpdateP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(UpdateP6ActivityThread.class);

	private final List<P6ActivityDTO> updateActivityP6Set;

	private final P6WSClient p6WSClient;

	public UpdateP6ActivityThread(final List<P6ActivityDTO> updateActivityP6Set, final P6WSClient p6WSClient) {
		this.updateActivityP6Set = updateActivityP6Set;
		this.p6WSClient = p6WSClient;
	}

	@Override
	public void run() {
		logger.info("Initiates Update Activities in P6 thread ....");
		/**
		 * File file = new
		 * File(System.getProperty("properties.dir")+"\\updateActivityP6Set.csv");
		 * List<P6ActivityDTO> subList; if ( updateActivityP6Set.size() > 2)
		 * subList = updateActivityP6Set.subList(0, 3); else subList =
		 * updateActivityP6Set;
		 * 
		 * CSVWriter.generateCSV(file, subList.toArray());
		 **/
		try {
			if (!updateActivityP6Set.isEmpty())
				p6WSClient.updateActivities(updateActivityP6Set);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS,
					ReadWriteProcessStatus.COMPLETED);
		} catch (P6ServiceException e) {
			logger.error("An error occur while updating activities in P6 ", e);
			if (P6ExceptionType.SYSTEM_ERROR.name().equals(e.getMessage())) {
				CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS,
						ReadWriteProcessStatus.FAILED);
			} else {
				CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS,
						ReadWriteProcessStatus.COMPLETED);
			}
			P6IntegrationExceptionHandler.handleDataExeception(e);
		}
	}

}
