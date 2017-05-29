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
 * Thread to initiates job to create the activity (work order task ) in P6
 * 
 * @author N039126
 * @version 1.0
 * 
 */
public class CreateP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CreateP6ActivityThread.class);

	private final List<P6ActivityDTO> createActivityP6Set;

	public CreateP6ActivityThread(final List<P6ActivityDTO> createActivityP6Set) {
		this.createActivityP6Set = createActivityP6Set;
	}

	@Override
	public void run() {
		logger.info("Initiates Create Activities in P6 thread ....");
		final File file = new File("C:\\test-config\\createActivityP6Set.csv");
		CSVWriter.generateCSV(file, createActivityP6Set.toArray());
	}

}
