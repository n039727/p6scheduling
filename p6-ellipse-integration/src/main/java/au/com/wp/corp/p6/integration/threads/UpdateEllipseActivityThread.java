/**
 * 
 */
package au.com.wp.corp.p6.integration.threads;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadProcessStatus;
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

	public UpdateEllipseActivityThread(final List<EllipseActivityDTO> updateActivityEllipseSet, final EllipseWSClient ellipseWSClient) {
		this.updateActivityEllipseSet = updateActivityEllipseSet;
		this.ellipseWSClient = ellipseWSClient;
	}

	@Override
	public void run() {
		logger.info("Initiates update Activities in Ellipse thread ....");
//		File file = new File("C:\\test-config\\updateActivityEllipseSet.csv");
//		CSVWriter.generateCSV(file, updateActivityEllipseSet.toArray());
//		CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_UPDATE_STATUS,ReadProcessStatus.COMPLETED );
		
		try {
			if ( !updateActivityEllipseSet.isEmpty() )
			ellipseWSClient.updateActivitiesEllipse(updateActivityEllipseSet, null);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_UPDATE_STATUS,ReadProcessStatus.COMPLETED );
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An error occurs while updating ellipse activity : ", e);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_UPDATE_STATUS, ReadProcessStatus.FAILED);	
		}
	}

}
