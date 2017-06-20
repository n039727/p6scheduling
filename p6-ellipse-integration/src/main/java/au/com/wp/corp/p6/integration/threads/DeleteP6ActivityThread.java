/**
 * 
 */
package au.com.wp.corp.p6.integration.threads;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
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

	public DeleteP6ActivityThread(final List<P6ActivityDTO> deleteActivityP6Set, final P6WSClient p6WSClient) {
		this.deleteActivityP6Set = deleteActivityP6Set;
		this.p6WSClient = p6WSClient;
	}

	@Override
	public void run() {
		logger.info("Initiates delete Activities in P6 thread ....");
		/**
		 * final File file = new
		 * File("C:\\test-config\\deleteActivityP6Set.csv"); List<P6ActivityDTO>
		 * subList; if ( deleteActivityP6Set.size() > 1) subList =
		 * deleteActivityP6Set.subList(0, 1); else subList =
		 * deleteActivityP6Set;
		 * 
		 * CSVWriter.generateCSV(file, subList.toArray());
		 **/
		try {
			if (!deleteActivityP6Set.isEmpty())
				p6WSClient.deleteActivities(deleteActivityP6Set);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_DELETE_STATUS,
					ReadWriteProcessStatus.COMPLETED);
		} catch (Exception e) {
			logger.error("An error occur while deleting activities in P6 ", e);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_DELETE_STATUS,
					ReadWriteProcessStatus.FAILED);
		}

	}

}
