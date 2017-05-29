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

/**
 * @author N039126
 *
 */
public class UpdateP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(UpdateP6ActivityThread.class);
	
	private final List<P6ActivityDTO> updateActivityP6Set;
	
	public UpdateP6ActivityThread(final List<P6ActivityDTO> updateActivityP6Set ) {
		this.updateActivityP6Set = updateActivityP6Set;
	}
	
	@Override
	public void run() {
		logger.info ("Initiates Update Activities in P6 thread ....");
		File file = new File("C:\\test-config\\updateActivityP6Set.csv");
		CSVWriter.generateCSV(file, updateActivityP6Set.toArray());
	}

}
