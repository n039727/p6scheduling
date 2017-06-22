/**
 * 
 */
package au.com.wp.corp.p6.integration.threads;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6ExceptionType;
import au.com.wp.corp.p6.integration.exception.P6IntegrationExceptionHandler;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadWriteProcessStatus;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;

/**
 * @author N039126
 *
 */
public class ReadP6ActivityThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ReadP6ActivityThread.class);
	private final P6WSClient p6WSClient;
	private final Integer projectId;
	
	public ReadP6ActivityThread(final P6WSClient p6WSClient, final Integer projectId) {
		this.p6WSClient = p6WSClient;
		this.projectId = projectId;
		CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_READ_STATUS, ReadWriteProcessStatus.STARTED);
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
			for (P6ActivityDTO activityDTO : p6WSClient.readActivities(projectId)) {
				activities.put(activityDTO.getActivityId(), activityDTO);
			}
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_READ_STATUS, ReadWriteProcessStatus.COMPLETED);
		} catch (P6ServiceException e) {
			logger.error("An error occurs while reading P6 activity : ", e);
			if (P6ExceptionType.SYSTEM_ERROR.name().equals(e.getMessage())){
				CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_READ_STATUS, ReadWriteProcessStatus.FAILED);
			} else{
				CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_READ_STATUS, ReadWriteProcessStatus.COMPLETED);
			}
			
			P6IntegrationExceptionHandler.handleException(e);
		}
		logger.debug("Size of activities from P6 # {}", activities.size());
		logger.debug("Time taken to read record from P6 # {} ", System.currentTimeMillis() - startTime);
		
		

	}

}
