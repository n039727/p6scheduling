/**
 * 
 */
package au.com.wp.corp.p6.integration.threads;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.wp.corp.p6.integration.dao.P6EllipseDAO;
import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadWriteProcessStatus;

/**
 * Thread to initiates job to read the activity (work order task ) in Ellipse
 * 
 * @author N039126
 * @version 1.0
 */
public class ReadEllipseThread implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ReadEllipseThread.class);
	private P6EllipseDAO p6EllipseDAO;
	private final List<String> workgroupList;

	public ReadEllipseThread(final P6EllipseDAO p6EllipseDAO, final List<String> workgroupList) {
		this.p6EllipseDAO = p6EllipseDAO;
		this.workgroupList = workgroupList;
		CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_READ_STATUS, ReadWriteProcessStatus.STARTED);
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		logger.info("Initiates Ellipse Reading thread ....");
		final long startTime = System.currentTimeMillis();
		Map<String, EllipseActivityDTO> activities = CacheManager.getEllipseActivitiesMap();

		final String readingStrategy = P6ReloadablePropertiesReader.getProperty("ELLIPSE_READING_STRATEGY");

		logger.debug("Ellipse reading strategy # {}", readingStrategy);

		
		readEllipse(startTime, activities, workgroupList);
		
		/**
		if (readingStrategy.equals(EllipseReadParameter.ALL.name())) {
			final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet();
			for (String key : keys) {
				workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
			}
			readEllipse(startTime, activities, workgroupList);
		} else if (readingStrategy.equals(EllipseReadParameter.INDIVIDUAL.name())) {
			final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet();
			for (String key : keys) {
				readEllipse(startTime, activities, CacheManager.getProjectWorkgroupListMap().get(key));
			}
		} else {
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_READ_STATUS, ReadProcessStatus.FAILED);
		} **/
	}

	/**
	 * @param startTime
	 * @param activities
	 */
	private void readEllipse(final long startTime, Map<String, EllipseActivityDTO> activities,
			final List<String> workgroupList) {
		try {
			for (EllipseActivityDTO activityDTO : p6EllipseDAO.readElipseWorkorderDetails(workgroupList)) {
				activities.put(activityDTO.getWorkOrderTaskId(), activityDTO);
			}
			logger.debug("Size of activities from Ellipse # {}", activities.size());
			logger.debug("Time taken to read record from Ellipse # {} ", System.currentTimeMillis() - startTime);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_READ_STATUS,
					ReadWriteProcessStatus.COMPLETED);
		} catch (P6DataAccessException e) {
			logger.error("An error occurs while reading record from Ellipse : ", e);
			CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.ELLIPSE_READ_STATUS, ReadWriteProcessStatus.FAILED);
		}
	}

}
