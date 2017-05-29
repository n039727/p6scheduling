/**
 * 
 */
package au.com.wp.corp.p6.threads;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class ReadP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ReadP6ActivityThread.class);
	private P6WSClient p6WSClient;
	
	public ReadP6ActivityThread(final P6WSClient p6WSClient) {
		this.p6WSClient = p6WSClient;
		CacheManager.getSystemReadStatusMap().put(ProcessStatus.P6_ACTIVITY_READ_STATUS, ReadProcessStatus.STARTED);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run(){
		logger.info ("Initiates P6 Activity Reading thread ....");
		final long startTime = System.currentTimeMillis();
		Map<String, P6ActivityDTO> activities = CacheManager.getP6ActivitiesMap();

		try {
			for (P6ActivityDTO activityDTO : p6WSClient.readActivities()) {
				activities.put(activityDTO.getActivityId(), activityDTO);
			}
		} catch (P6ServiceException e) {
			logger.error("An error occurs while reading P6 activity : ", e);
			CacheManager.getSystemReadStatusMap().put(ProcessStatus.P6_ACTIVITY_READ_STATUS, ReadProcessStatus.FAILED);			
			
		}
		logger.debug("Size of activities from P6 # {}", activities.size());
		logger.debug("Time taken to read record from P6 # {} ", System.currentTimeMillis() - startTime);
		CacheManager.getSystemReadStatusMap().put(ProcessStatus.P6_ACTIVITY_READ_STATUS, ReadProcessStatus.COMPLETED);
		

	}

}
