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
 * Thread to initiates job to delete the activity (work order task ) in P6
 * 
 * @author N039126
 * @version 1.0
 * 
 */
public class DeleteP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(DeleteP6ActivityThread.class);

	private final List<P6ActivityDTO> deleteActivityP6Set;

	public DeleteP6ActivityThread(final List<P6ActivityDTO> deleteActivityP6Set) {
		this.deleteActivityP6Set = deleteActivityP6Set;
	}

	@Override
	public void run() {
		logger.info("Initiates delete Activities in P6 thread ....");
		final File file = new File("C:\\test-config\\deleteActivityP6Set.csv");
		CSVWriter.generateCSV(file, deleteActivityP6Set.toArray());
	}

}
