/**
 * 
 */
package au.com.wp.corp.p6.threads;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.csv.CSVWriter;
import au.com.wp.corp.p6.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.util.CacheManager;
import au.com.wp.corp.p6.util.ProcessStatus;
import au.com.wp.corp.p6.util.ReadProcessStatus;
import au.com.wp.corp.p6.wsclient.ellipse.EllipseWSClient;

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
		File file = new File("C:\\test-config\\updateActivityEllipseSet.csv");
		CSVWriter.generateCSV(file, updateActivityEllipseSet.toArray());
		CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_UPDATE_STATUS,ReadProcessStatus.COMPLETED );
		
		try {
			ellipseWSClient.updateActivitiesEllipse(updateActivityEllipseSet.subList(0, 2));
		} catch (P6ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
