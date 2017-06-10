/**
 * 
 */
package au.com.wp.corp.p6.threads;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.csv.CSVWriter;
import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.util.CacheManager;
import au.com.wp.corp.p6.util.ProcessStatus;
import au.com.wp.corp.p6.util.ReadProcessStatus;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;

/**
 * @author N039126
 *
 */
public class UpdateP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(UpdateP6ActivityThread.class);
	
	private final List<P6ActivityDTO> updateActivityP6Set;
	
	private final P6WSClient p6WSClient;
	
	public UpdateP6ActivityThread(final List<P6ActivityDTO> updateActivityP6Set , final P6WSClient p6WSClient) {
		this.updateActivityP6Set = updateActivityP6Set;
		this.p6WSClient = p6WSClient;
	}
	
	@Override
	public void run() {
		logger.info ("Initiates Update Activities in P6 thread ....");
		File file = new File(System.getProperty("properties.dir")+"\\updateActivityP6Set.csv");
		List<P6ActivityDTO> subList;
		if ( updateActivityP6Set.size() > 2)
			subList = updateActivityP6Set.subList(0, 3);
		else
			subList = updateActivityP6Set;
			
		CSVWriter.generateCSV(file, subList.toArray());
		
		try {
			p6WSClient.updateActivities(subList);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS,ReadProcessStatus.COMPLETED );
		} catch (P6ServiceException e) {
			logger.error("An error occur while deleting activities in P6 ", e);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS,ReadProcessStatus.FAILED );
		}
	}

}
