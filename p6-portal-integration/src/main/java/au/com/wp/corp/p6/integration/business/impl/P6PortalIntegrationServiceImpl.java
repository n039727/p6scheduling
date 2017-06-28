/**
 * 
 */
package au.com.wp.corp.p6.integration.business.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.integration.business.P6PortalIntegrationService;
import au.com.wp.corp.p6.integration.dao.P6PortalDAO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;

/**
 * @author N039126
 *
 */
@Service
public class P6PortalIntegrationServiceImpl implements P6PortalIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(P6PortalIntegrationServiceImpl.class);

	public static final String POLING_TIME_TO_CHECK_READ_STATUS_INMILI = "POLING_TIME_TO_CHECK_READ_STATUS_INMILI";

	public static final String INTEGRATION_RUN_STARTEGY = "INTEGRATION_RUN_STARTEGY";

	public static final String USER_STATUS_AL = "AL";

	public static final String TASK_STATUS_COMPLETED = "Completed";

	
  
	@Autowired
	P6WSClient p6WSClient;

	@Autowired
	P6PortalDAO p6PortalDAO;

	@Autowired
	DateUtil dateUtil;

	

	/**
	 * 
	 * @return
	 * @throws P6BusinessException
	 */
	@Override
	public boolean readUDFTypeMapping() throws P6BusinessException {
		logger.info("Initiates P6 Portal Reading thread ....");
		boolean status = false;
		Map<String, UDFTypeDTO> udfTypeMap = CacheManager.getP6UDFTypeMap();

		try {
			for (UDFTypeDTO udfType : p6WSClient.readUDFTypes()) {
				udfTypeMap.put(udfType.getTitle(), udfType);
			}
			status = true;
			logger.debug("Size of udf type list from P6 # {}", udfTypeMap.size());
		} catch (P6ServiceException e) {
			logger.error("An error occurs while readeing Project Resource/workgroup mapping from P6 Portal:", e);
			throw e;
		}

		return status;

	}


	
	@Override
	public void clearApplicationMemory() {
		logger.debug("Clearing cache memory........");
		CacheManager.getEllipseActivitiesMap().clear();
		CacheManager.getP6ActivitiesMap().clear();
		CacheManager.getP6ProjectWorkgroupMap().clear();
		CacheManager.getProjectWorkgroupListMap().clear();
		CacheManager.getSystemReadWriteStatusMap().clear();
		CacheManager.getWsHeaders().clear();
		CacheManager.getProjectsMap().clear();
	}



	@Override
	public boolean startPortalToP6Integration()
			throws P6BusinessException {
		// TODO Auto-generated method stub
		return Boolean.FALSE;
	}

}
