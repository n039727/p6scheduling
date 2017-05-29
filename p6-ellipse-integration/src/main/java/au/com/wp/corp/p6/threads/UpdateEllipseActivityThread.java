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

	public UpdateEllipseActivityThread(final List<EllipseActivityDTO> updateActivityEllipseSet) {
		this.updateActivityEllipseSet = updateActivityEllipseSet;
	}

	@Override
	public void run() {
		logger.info("Initiates update Activities in Ellipse thread ....");
		File file = new File("C:\\test-config\\updateActivityEllipseSet.csv");
		CSVWriter.generateCSV(file, updateActivityEllipseSet.toArray());
	}

}
