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
import au.com.wp.corp.p6.integration.util.ReadProcessStatus;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;

/**
 * Thread to initiates job to create the activity (work order task ) in P6
 * 
 * @author N039126
 * @version 1.0
 * 
 */
public class CreateP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CreateP6ActivityThread.class);

	private final List<P6ActivityDTO> createActivityP6Set;

	private final List<P6ActivityDTO> deleteActivityP6BforCreate;
	
	private final P6WSClient p6WSClient;

	public CreateP6ActivityThread(final List<P6ActivityDTO> createActivityP6Set, final List<P6ActivityDTO> deleteActivityP6BforCreate, final P6WSClient p6WSClient) {
		this.createActivityP6Set = createActivityP6Set;
		this.deleteActivityP6BforCreate = deleteActivityP6BforCreate;
		this.p6WSClient = p6WSClient;
	}

	@Override
	public void run() {
		logger.info("Initiates Create Activities in P6 thread ....");

		try {
			boolean deleteStatus = false;
			if ( !deleteActivityP6BforCreate.isEmpty())
			{
				deleteStatus = p6WSClient.deleteActivities(deleteActivityP6BforCreate);
			}
			if (!createActivityP6Set.isEmpty() && deleteStatus)
				p6WSClient.createActivities(createActivityP6Set);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_CREATE_STATUS,
					ReadProcessStatus.COMPLETED);
		} catch (Exception e) {
			logger.error("An error occur while creating activities in P6 ", e);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_CREATE_STATUS,
					ReadProcessStatus.FAILED);
		}
	}

}
