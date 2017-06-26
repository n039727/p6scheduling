/**
 * 
 */
package au.com.wp.corp.p6.integration.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.integration.business.P6EllipseIntegrationService;
import au.com.wp.corp.p6.integration.dao.P6EllipseDAO;
import au.com.wp.corp.p6.integration.dao.P6PortalDAO;
import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.integration.exception.P6ExceptionType;
import au.com.wp.corp.p6.integration.exception.P6IntegrationExceptionHandler;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.threads.CreateP6ActivityThread;
import au.com.wp.corp.p6.integration.threads.DeleteP6ActivityThread;
import au.com.wp.corp.p6.integration.threads.ReadEllipseThread;
import au.com.wp.corp.p6.integration.threads.ReadP6ActivityThread;
import au.com.wp.corp.p6.integration.threads.UpdateEllipseActivityThread;
import au.com.wp.corp.p6.integration.threads.UpdateP6ActivityThread;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.util.EllipseReadParameter;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.util.P6Utility;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadWriteProcessStatus;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.ellipse.EllipseWSClient;

/**
 * @author N039126
 *
 */
@Service
public class P6EllipseIntegrationServiceImpl implements P6EllipseIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(P6EllipseIntegrationServiceImpl.class);

	public static final String POLING_TIME_TO_CHECK_READ_STATUS_INMILI = "POLING_TIME_TO_CHECK_READ_STATUS_INMILI";

	public static final String INTEGRATION_RUN_STARTEGY = "INTEGRATION_RUN_STARTEGY";

	public static final String USER_STATUS_AL = "AL";

	public static final String TASK_STATUS_COMPLETED = "Completed";

	@Autowired
	P6EllipseDAO p6EllipseDAO;

	@Autowired
	P6WSClient p6WSClient;

	@Autowired
	P6PortalDAO p6PortalDAO;

	@Autowired
	DateUtil dateUtil;

	@Autowired
	EllipseWSClient ellipseWSClient;

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
			throw new P6BusinessException(P6ExceptionType.SYSTEM_ERROR.name(), e.getCause());
		}

		return status;

	}

	/**
	 * 
	 * @return
	 * @throws P6BusinessException
	 */
	@Override
	public boolean readProjectWorkgroupMapping() throws P6BusinessException {
		logger.info("Initiates P6 Portal Reading thread ....");
		final long startTime = System.currentTimeMillis();
		boolean status = false;

		Map<String, Integer> projWorkgroupDTOs = null;
		Map<String, Integer> projectsMap = null;
		List<P6ProjWorkgroupDTO> p6ProjWorkgroupDTOs = null;
		try {
			projWorkgroupDTOs = p6WSClient.readResources();
			projectsMap = p6WSClient.readProjects();
			p6ProjWorkgroupDTOs = p6PortalDAO.getProjectResourceMappingList();
		} catch (P6DataAccessException e) {
			logger.error("An error occurs while readeing Project Resource/workgroup mapping from P6 Portal:", e);
			throw new P6BusinessException(P6ExceptionType.SYSTEM_ERROR.name(), e.getCause());

		}

		logger.debug("List of resource from P6# {}", projWorkgroupDTOs.keySet());

		CacheManager.getProjectsMap().putAll(projectsMap);

		Map<String, P6ProjWorkgroupDTO> projectWorkgroupMap = CacheManager.getP6ProjectWorkgroupMap();

		Map<String, List<String>> projectWorkgroupListMap = CacheManager.getProjectWorkgroupListMap();

		List<String> primaryResIds;
		for (P6ProjWorkgroupDTO projectWG : p6ProjWorkgroupDTOs) {

			if (null == projectWorkgroupListMap.get(projectWG.getProjectName())) {
				primaryResIds = new ArrayList<>();
				projectWorkgroupListMap.put(projectWG.getProjectName(), primaryResIds);
			} else {
				primaryResIds = projectWorkgroupListMap.get(projectWG.getProjectName());
			}

			logger.debug("primary resource - resource id # {}", projectWG.getPrimaryResourceId());
			if (null != projectWG.getPrimaryResourceId()
					&& null != projWorkgroupDTOs.get(projectWG.getPrimaryResourceId())) {
				projectWG.setPrimaryResourceObjectId(projWorkgroupDTOs.get(projectWG.getPrimaryResourceId()));
				projectWG.setProjectObjectId(projectsMap.get(projectWG.getProjectName().trim()));
				primaryResIds.add(projectWG.getPrimaryResourceId());
				projectWorkgroupMap.put(projectWG.getPrimaryResourceId(), projectWG);
			}

		}
		status = true;
		logger.debug("Size of project resource/workgroup mapping from P6 Portal# {}", projectWorkgroupMap.size());
		logger.debug("Size of project resource/workgroup mapping List from P6 Portal# {}",
				projectWorkgroupListMap.size());

		logger.debug("Time taken to read record from P6 Portal # {} ", System.currentTimeMillis() - startTime);

		return status;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.business.P6EllipseIntegrationService#
	 * getActivityTobeUpdatedInP6()
	 */
	public void updateActivitiesInP6(final List<P6ActivityDTO> updateActivityP6Set) {
		logger.debug("update activites in p6 - number of activities # {}", updateActivityP6Set.size());
		UpdateP6ActivityThread thread = new UpdateP6ActivityThread(updateActivityP6Set, p6WSClient);
		new Thread(thread).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.business.P6EllipseIntegrationService#
	 * getActivityTobeDeletedInP6()
	 */
	public void deleteActivityInP6(final List<P6ActivityDTO> deleteActivityP6Set) {
		logger.debug("delete activites in p6 - number of activities # {}", deleteActivityP6Set.size());
		DeleteP6ActivityThread thread = new DeleteP6ActivityThread(deleteActivityP6Set, p6WSClient);
		new Thread(thread).start();

	}

	public void updateActivitiesInEllipse(final List<EllipseActivityDTO> updateActivityEllipseSet) {
		logger.debug("update activites in Ellipse - number of activities # {}", updateActivityEllipseSet.size());
		UpdateEllipseActivityThread thread = new UpdateEllipseActivityThread(updateActivityEllipseSet, ellipseWSClient);
		new Thread(thread).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.business.P6EllipseIntegrationService#
	 * getActivityTobeCreatedInP6()
	 */
	public void createActivityInP6(final List<P6ActivityDTO> createActivityP6Set,
			final List<P6ActivityDTO> deleteActivityP6BforCreate) {
		logger.debug("create activites in p6 - number of activities # {} and delete activites in p6 - {} ",
				createActivityP6Set.size(), deleteActivityP6BforCreate.size());
		CreateP6ActivityThread thread = new CreateP6ActivityThread(createActivityP6Set, deleteActivityP6BforCreate,
				p6WSClient);
		new Thread(thread).start();

	}

	@Override
	public boolean start() throws P6BusinessException {
		boolean status = Boolean.FALSE;
		clearApplicationMemory();

		try {
			readUDFTypeMapping();
			readProjectWorkgroupMapping();
		} catch (P6BusinessException e) {
			P6IntegrationExceptionHandler.handleException(e);
			throw e;
		}

		try {
			final String integrationRunStartegy = P6ReloadablePropertiesReader.getProperty(INTEGRATION_RUN_STARTEGY);

			if (null == integrationRunStartegy || integrationRunStartegy.isEmpty()) {
				throw new P6BusinessException("INTEGRATION_RUN_STARTEGY can't be null");
			}
			logger.info("Batch run strategy # {}", integrationRunStartegy);
			final List<String> workgroupList = new ArrayList<>();
			final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet();
			if (integrationRunStartegy.equals(EllipseReadParameter.ALL.name())) {
				for (String key : keys) {
					workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
				}
				startEllipseToP6Integration(workgroupList, null);
				status = Boolean.TRUE;
			} else if (integrationRunStartegy.equals(EllipseReadParameter.INDIVIDUAL.name())) {
				for (String key : keys) {
					workgroupList.clear();
					CacheManager.getEllipseActivitiesMap().clear();
					CacheManager.getP6ActivitiesMap().clear();
					CacheManager.getSystemReadWriteStatusMap().clear();
					workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
					startEllipseToP6Integration(workgroupList, CacheManager.getProjectsMap().get(key));
				}
				status = Boolean.TRUE;
			}
		} catch (P6BusinessException e) {
			status = Boolean.FALSE;
			logger.debug("error- ", e);
			throw e;
		} finally {
			p6WSClient.logoutFromP6();
			clearApplicationMemory();
		}
		logger.info("Ellipse,P6 integration is completed with status # {} ", status);
		return status;

	}

	@Override
	public List<P6ActivityDTO> startEllipseToP6Integration(final List<String> workgroupList, final Integer projectId)
			throws P6BusinessException {

		logger.info("Starting all rading threads ....");
		final String sleepTime = P6ReloadablePropertiesReader.getProperty(POLING_TIME_TO_CHECK_READ_STATUS_INMILI);
		long sleepTimeLong;
		try {
			sleepTimeLong = Long.valueOf(sleepTime);
		} catch (NumberFormatException e1) {
			throw new P6BusinessException("Polling time to check read status should be a number ", e1);
		}

		final ReadEllipseThread readEllipse = new ReadEllipseThread(p6EllipseDAO, workgroupList);
		final ReadP6ActivityThread readP6Activity = new ReadP6ActivityThread(p6WSClient, projectId);
		final Thread readEllipseThread = new Thread(readEllipse);
		final Thread readP6ActivityThread = new Thread(readP6Activity);
		readEllipseThread.start();
		readP6ActivityThread.start();

		Map<String, EllipseActivityDTO> ellipseActivites = null;
		Map<String, P6ActivityDTO> p6Activites = null;
		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = null;

		// Waiting threads to complete read processes
		while (true) {
			if (CacheManager.getSystemReadWriteStatusMap()
					.get(ProcessStatus.ELLIPSE_READ_STATUS) == ReadWriteProcessStatus.COMPLETED
					&& CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_READ_STATUS) == ReadWriteProcessStatus.COMPLETED) {
				ellipseActivites = CacheManager.getEllipseActivitiesMap();
				p6Activites = CacheManager.getP6ActivitiesMap();
				projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();
				break;
			} else if (CacheManager.getSystemReadWriteStatusMap()
					.get(ProcessStatus.ELLIPSE_READ_STATUS) == ReadWriteProcessStatus.FAILED
					|| CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_READ_STATUS) == ReadWriteProcessStatus.FAILED) {
				throw new P6BusinessException(P6ExceptionType.SYSTEM_ERROR.name());
			}

			try {
				Thread.currentThread().sleep(sleepTimeLong);
			} catch (InterruptedException e) {
				logger.error("the current thread has been interupted while reading ellipse and P6");
				Thread.currentThread().interrupt();
				throw new P6BusinessException("Current thread gets interrupted ", e);
			}

		}

		logger.info("list of work groups # {}", projectWorkGropMap);
		final List<P6ActivityDTO> createActivityP6Set = new ArrayList<>();

		final List<P6ActivityDTO> updateActivityP6Set = new ArrayList<>();

		final List<P6ActivityDTO> deleteActivityP6Set = new ArrayList<>();

		final List<EllipseActivityDTO> updateActivityEllipseSet = new ArrayList<>();

		final List<P6ActivityDTO> deleteActivityP6BforCreate = new ArrayList<>();

		Set<String> ellipseActivityIds = ellipseActivites.keySet();
		for (String activityId : ellipseActivityIds) {
			EllipseActivityDTO ellipseActivity = ellipseActivites.get(activityId);
			if (!p6Activites.containsKey(activityId)) {
				// Identifying the activities which will be created in P6
				P6ActivityDTO p6Activity = constructP6ActivityDTO(ellipseActivity, projectWorkGropMap, null);
				createActivityP6Set.add(p6Activity);

			} else if (p6Activites.containsKey(activityId)) {
				/*
				 * if the assigned resource got changed in ellipse, then create
				 * it under the project where the assigned resource belongs to
				 * and remove it from old project
				 */

				if (null != ellipseActivity.getWorkGroup()
						&& null != projectWorkGropMap.get(ellipseActivity.getWorkGroup())
						&& null != p6Activites.get(activityId).getWorkGroup()
						&& null != projectWorkGropMap.get(p6Activites.get(activityId).getWorkGroup())
						&& (projectWorkGropMap.get(ellipseActivity.getWorkGroup())
								.getProjectObjectId() != projectWorkGropMap
										.get(p6Activites.get(activityId).getWorkGroup()).getProjectObjectId()
								|| projectWorkGropMap.get(p6Activites.get(activityId).getWorkGroup())
										.getProjectObjectId() != p6Activites.get(activityId).getProjectObjectId())) {
					P6ActivityDTO p6Activity = constructP6ActivityDTO(ellipseActivity, projectWorkGropMap,
							p6Activites.get(activityId).getWorkGroup());
					createActivityP6Set.add(p6Activity);
					final P6ActivityDTO delActivity = p6Activites.get(activityId);
					deleteActivityP6BforCreate.add(delActivity);

				} else {
					// Identifying the activities which will be created in P6
					final P6ActivityDTO p6ActivityDTO = syncEllipseP6Activity(p6Activites.get(activityId),
							ellipseActivites.get(activityId), projectWorkGropMap);
					if (null != p6ActivityDTO)
						updateActivityP6Set.add(p6ActivityDTO);

				}

				final EllipseActivityDTO ellipseActivityDTO = syncP6EllipseActivity(p6Activites.get(activityId),
						ellipseActivites.get(activityId), projectWorkGropMap);
				if (null != ellipseActivityDTO) {
					updateActivityEllipseSet.add(ellipseActivityDTO);
				}

				p6Activites.remove(activityId, p6Activites.get(activityId));
			}
		}

		deleteActivityP6Set.addAll(p6Activites.values());

		createActivityInP6(createActivityP6Set, deleteActivityP6BforCreate);
		updateActivitiesInP6(updateActivityP6Set);
		deleteActivityInP6(deleteActivityP6Set);
		updateActivitiesInEllipse(updateActivityEllipseSet);

		// Waiting threads to complete read processes
		while (true) {
			if (CacheManager.getSystemReadWriteStatusMap()
					.get(ProcessStatus.ELLIPSE_UPDATE_STATUS) == ReadWriteProcessStatus.COMPLETED
					&& CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_CREATE_STATUS) == ReadWriteProcessStatus.COMPLETED
					&& CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS) == ReadWriteProcessStatus.COMPLETED
					&& CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_DELETE_STATUS) == ReadWriteProcessStatus.COMPLETED) {
				break;
			} else if ((CacheManager.getSystemReadWriteStatusMap()
					.get(ProcessStatus.ELLIPSE_UPDATE_STATUS) == ReadWriteProcessStatus.COMPLETED
					|| CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.ELLIPSE_UPDATE_STATUS) == ReadWriteProcessStatus.FAILED)
					&& (CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_CREATE_STATUS) == ReadWriteProcessStatus.FAILED
							|| CacheManager.getSystemReadWriteStatusMap()
									.get(ProcessStatus.P6_ACTIVITY_CREATE_STATUS) == ReadWriteProcessStatus.COMPLETED)
					&& (CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS) == ReadWriteProcessStatus.FAILED
							|| CacheManager.getSystemReadWriteStatusMap()
									.get(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS) == ReadWriteProcessStatus.COMPLETED)
					&& (CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_DELETE_STATUS) == ReadWriteProcessStatus.FAILED
							|| CacheManager.getSystemReadWriteStatusMap().get(
									ProcessStatus.P6_ACTIVITY_DELETE_STATUS) == ReadWriteProcessStatus.COMPLETED)) {

				throw new P6BusinessException(P6ExceptionType.SYSTEM_ERROR.name());
			}

			try {
				Thread.currentThread().sleep(sleepTimeLong);
			} catch (InterruptedException e) {
				logger.error("the current thread has been interupted while writting ellipse and P6");
				Thread.currentThread().interrupt();
				throw new P6BusinessException("Current thread gets interrupted ", e);
			}

		}

		return deleteActivityP6Set;
	}

	/**
	 * 
	 * @param p6Activity
	 * @param ellipseActivity
	 * @return
	 */
	private EllipseActivityDTO syncP6EllipseActivity(P6ActivityDTO p6Activity, EllipseActivityDTO ellipseActivity,
			Map<String, P6ProjWorkgroupDTO> projectWorkgroup) {

		final EllipseActivityDTO ellipseActivityUpd = new EllipseActivityDTO();
		boolean isUpdateReq = false;
		/*
		 * in P6 activity Work group is changed when comparing the data between
		 * Ellipse and P6 then update the Crew from P6 to Work Group in Ellipse.
		 */
		if (null != p6Activity.getWorkGroup()
				&& !p6Activity.getWorkGroup().equals(ellipseActivity.getWorkGroup().trim())) {
			ellipseActivityUpd.setWorkGroup(p6Activity.getWorkGroup());
			isUpdateReq = true;
		}

		/*
		 * in P6 the activity is in Scheduling Inbox when comparing the data
		 * between Ellipse and P6 then update the Planned Start date in Ellipse
		 * as blank and leave the Planned start date in P6 as it is
		 */
		if (null != projectWorkgroup.get(p6Activity.getWorkGroup())
				&& null != projectWorkgroup.get(p6Activity.getWorkGroup()).getSchedulerinbox()
				&& projectWorkgroup.get(p6Activity.getWorkGroup()).getSchedulerinbox().equals(P6EllipseWSConstants.Y)
				&& !ellipseActivity.getPlannedStartDate().isEmpty()) {
			ellipseActivityUpd.setPlannedStartDate(null);
			ellipseActivityUpd.setPlannedFinishDate(null);
			isUpdateReq = true;

		} else if ((null != projectWorkgroup.get(p6Activity.getWorkGroup())
				&& null != projectWorkgroup.get(p6Activity.getWorkGroup()).getSchedulerinbox()
				&& !projectWorkgroup.get(p6Activity.getWorkGroup()).getSchedulerinbox().equals(P6EllipseWSConstants.Y))
				&& null != p6Activity.getPlannedStartDate()
				&& !dateUtil.isSameDate(p6Activity.getPlannedStartDate(), DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
						ellipseActivity.getPlannedStartDate(), DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP)) {
			/*
			 * in P6 activity is in Crew Work group when while comparing the
			 * Planned Start Date between Ellipse and P6 is different then
			 * update the planned start date from P6 to Ellipse.
			 */
			ellipseActivityUpd.setPlannedStartDate(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
					DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP));
			isUpdateReq = true;
		}
		/*
		 * If the P6 Planned start or the P6 Work group changes then Update the
		 * User Status in Ellipse as 'AL'. The User Status in P6 will remain the
		 * same for the 1st Job run and next time the job runs it will update
		 * the status from Ellipse to P6
		 */
		if ((null != p6Activity.getWorkGroup() && !p6Activity.getWorkGroup().equals(ellipseActivity.getWorkGroup()))
				|| (null != p6Activity.getPlannedStartDate() && !dateUtil.isSameDate(p6Activity.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, ellipseActivity.getPlannedStartDate(),
						DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
						&& !ellipseActivity.getTaskUserStatus().equals(USER_STATUS_AL)) {
			ellipseActivityUpd.setTaskUserStatus(USER_STATUS_AL);
			isUpdateReq = true;
		}

		if (isUpdateReq) {
			ellipseActivityUpd.setWorkOrderTaskId(ellipseActivity.getWorkOrderTaskId());
		}

		return isUpdateReq ? ellipseActivityUpd : null;
	}

	/**
	 * the activity in P6 to be updated with correct data from Ellipse if their
	 * are any changes from the earlier data in P6 so that correct information
	 * is present in P6 and activities can be scheduled effectively
	 * 
	 * @param p6Activity
	 * @param ellipseActivity
	 * @return
	 */
	private P6ActivityDTO syncEllipseP6Activity(P6ActivityDTO p6Activity, EllipseActivityDTO ellipseActivity,
			Map<String, P6ProjWorkgroupDTO> projectWorkgroup) throws P6BusinessException {

		final P6ActivityDTO p6ActivityUpd = new P6ActivityDTO();
		boolean isUpdateReq = false;

		if (!ellipseActivity.getWorkOrderDescription().equals(p6Activity.getActivityName())) {
			p6ActivityUpd.setActivityName(ellipseActivity.getWorkOrderDescription());
			isUpdateReq = true;

		}

		/*
		 * Activity Status will updated in P6 based on the current status of WO
		 * task in Ellipse. If the WO task is not closed in Ellipse then in P6
		 * this field will be updated to Not-Started else if it is closed then
		 * update in P6 as Completed
		 */

		if (!ellipseActivity.getJdCode().isEmpty()
				&& !ellipseActivity.getJdCode().equals(p6Activity.getActivityJDCodeUDF())) {
			p6ActivityUpd.setActivityJDCodeUDF(ellipseActivity.getJdCode());
			isUpdateReq = true;

		}

		if (!ellipseActivity.getEquipmentNo().isEmpty()
				&& !ellipseActivity.getEquipmentNo().equals(p6Activity.getEquipmentNoUDF())) {
			p6ActivityUpd.setEquipmentNoUDF(ellipseActivity.getEquipmentNo());
			isUpdateReq = true;

		}

		if (!ellipseActivity.getPlantNoOrPickId().isEmpty()
				&& !ellipseActivity.getPlantNoOrPickId().equals(p6Activity.getPickIdUDF())) {
			p6ActivityUpd.setPickIdUDF(ellipseActivity.getPlantNoOrPickId());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getEGI().isEmpty() && !ellipseActivity.getEGI().equals(p6Activity.geteGIUDF())) {
			p6ActivityUpd.seteGIUDF(ellipseActivity.getEGI());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getEquipmentCode().isEmpty()
				&& !ellipseActivity.getEquipmentCode().equals(p6Activity.getEquipmentCodeUDF())) {
			p6ActivityUpd.setEquipmentCodeUDF(ellipseActivity.getEquipmentCode());
			isUpdateReq = true;
		}

		if (!P6Utility.isEqual(p6Activity.getOriginalDuration(), ellipseActivity.getOriginalDuration())) {
			p6ActivityUpd.setOriginalDuration(ellipseActivity.getOriginalDuration());
			isUpdateReq = true;
		}

		if (!P6Utility.isEqual(p6Activity.getRemainingDuration(), ellipseActivity.getRemainingDuration())
				&& !ellipseActivity.getTaskStatus().equals(TASK_STATUS_COMPLETED)) {
			p6ActivityUpd.setRemainingDuration(ellipseActivity.getRemainingDuration());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getRequiredByDate().isEmpty()
				&& !ellipseActivity.getRequiredByDate().equals(p6Activity.getRequiredByDateUDF())) {
			p6ActivityUpd.setRequiredByDateUDF(ellipseActivity.getRequiredByDate());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getEllipseStandardJob().isEmpty()
				&& !ellipseActivity.getEllipseStandardJob().equals(p6Activity.getEllipseStandardJobUDF())) {
			p6ActivityUpd.setEllipseStandardJobUDF(ellipseActivity.getEllipseStandardJob());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getFeeder().isEmpty() && !ellipseActivity.getFeeder().equals(p6Activity.getFeederUDF())) {
			p6ActivityUpd.setFeederUDF(ellipseActivity.getFeeder());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getUpStreamSwitch().isEmpty()
				&& !ellipseActivity.getUpStreamSwitch().equals(p6Activity.getUpStreamSwitchUDF())) {
			p6ActivityUpd.setUpStreamSwitchUDF(ellipseActivity.getUpStreamSwitch());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getEstimatedLabourHours().isEmpty()
				&& !P6Utility.isEqual(P6Utility.covertStringToDouble(ellipseActivity.getEstimatedLabourHours()),
						p6Activity.getEstimatedLabourHours())) {
			p6ActivityUpd
					.setEstimatedLabourHours(P6Utility.covertStringToDouble(ellipseActivity.getEstimatedLabourHours()));
			isUpdateReq = true;
		}

		if (!ellipseActivity.getAddress().isEmpty()
				&& !ellipseActivity.getAddress().equals(p6Activity.getAddressUDF())) {
			p6ActivityUpd.setAddressUDF(ellipseActivity.getAddress());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getLocationInStreet().isEmpty()
				&& !ellipseActivity.getLocationInStreet().equals(p6Activity.getLocationInStreetUDF())) {
			p6ActivityUpd.setLocationInStreetUDF(ellipseActivity.getLocationInStreet());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getTaskDescription().isEmpty()
				&& !ellipseActivity.getTaskDescription().equals(p6Activity.getTaskDescriptionUDF())) {
			p6ActivityUpd.setTaskDescriptionUDF(ellipseActivity.getTaskDescription());
			isUpdateReq = true;
		}

		if (!ellipseActivity.getTaskStatus().isEmpty()
				&& !ellipseActivity.getTaskStatus().equals(p6Activity.getActivityStatus())) {
			p6ActivityUpd.setActivityStatus(ellipseActivity.getTaskStatus());
			isUpdateReq = true;
		}

		/*
		 * If the WO task ='RR' and Planned Start Date = 'Todays Date' in
		 * Ellipse when comparing the data between P6 and Ellipse then user
		 * status in P6 will not be updated and it is left as it is else update
		 * the user status from Ellipse
		 */
		if (!ellipseActivity.getTaskUserStatus().equals(p6Activity.getTaskUserStatusUDF())
				&& ((!ellipseActivity.getTaskUserStatus().equals(P6EllipseWSConstants.RR)
						&& !dateUtil.isCurrentDate(ellipseActivity.getPlannedStartDate()))
						|| (ellipseActivity.getTaskUserStatus().equals(P6EllipseWSConstants.RR)
								&& !dateUtil.isCurrentDate(ellipseActivity.getPlannedStartDate()))
						|| (!ellipseActivity.getTaskUserStatus().equals(P6EllipseWSConstants.RR)
								&& dateUtil.isCurrentDate(ellipseActivity.getPlannedStartDate())))) {
			p6ActivityUpd.setTaskUserStatusUDF(ellipseActivity.getTaskUserStatus());
			isUpdateReq = true;
		}

		/*
		 * WO task user group and Work group against activity in P6 is assigned
		 * to the Scheduler Inbox when comparing the data between P6 and Ellipse
		 * then the Execution Package field in P6 should be updated to blank. 
		 */

		if (null != projectWorkgroup.get(ellipseActivity.getWorkGroup())
				&& null != projectWorkgroup.get(ellipseActivity.getWorkGroup()).getSchedulerinbox()
				&& projectWorkgroup.get(ellipseActivity.getWorkGroup()).getSchedulerinbox().toUpperCase()
						.equals(P6EllipseWSConstants.Y)
				&& (null != p6Activity.getExecutionPckgUDF() && !p6Activity.getExecutionPckgUDF().trim().isEmpty())) {
			p6ActivityUpd.setExecutionPckgUDF("");
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getActualStartDate() && !ellipseActivity.getActualStartDate().trim().isEmpty()) {
			p6ActivityUpd.setActualStartDate(dateUtil.convertDateToString(ellipseActivity.getActualStartDate(),
					DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getActualFinishDate() && !ellipseActivity.getActualFinishDate().isEmpty()
				&& !dateUtil.isSameDate(ellipseActivity.getActualFinishDate(),
						DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, p6Activity.getActualFinishDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP)) {
			p6ActivityUpd.setActualFinishDate(dateUtil.convertDateToString(ellipseActivity.getActualFinishDate(),
					DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
			isUpdateReq = true;

		}

		/*
		 * the WO task is read from Ellipse when the corresponding activity
		 * needs to be created in P6 with all the details then it should be
		 * created in the project based on the work group that is assigned on
		 * the task
		 */
		final int projectObjectId = projectWorkgroup.get(ellipseActivity.getWorkGroup()) != null
				? projectWorkgroup.get(ellipseActivity.getWorkGroup()).getProjectObjectId() : 0;

		if (isUpdateReq && projectObjectId != 0) {
			p6ActivityUpd.setProjectObjectId(projectObjectId);
		}

		if (isUpdateReq) {
			p6ActivityUpd.setActivityObjectId(p6Activity.getActivityObjectId());
			p6ActivityUpd.setActivityId(p6Activity.getActivityId());
		}

		return isUpdateReq ? p6ActivityUpd : null;
	}

	/**
	 * WO task is in Authorised status and assigned to the list of work group
	 * when the WO task is read from Ellipse and corresponding activity is not
	 * present in P6 then a new activity will be created in P6 with all the
	 * required details including Planned start date
	 * 
	 * @param ellipseActivity
	 * @return
	 */
	private P6ActivityDTO constructP6ActivityDTO(EllipseActivityDTO ellipseActivity,
			Map<String, P6ProjWorkgroupDTO> projectWorkgroup, final String woGroup) throws P6BusinessException {
		logger.debug("Constructing new activity for p6...");
		final String workGroup = null == woGroup ? ellipseActivity.getWorkGroup() : woGroup;

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId(ellipseActivity.getWorkOrderTaskId());
		p6Activity.setActivityJDCodeUDF(ellipseActivity.getJdCode());
		p6Activity.setActivityName(ellipseActivity.getWorkOrderDescription());
		p6Activity.setActivityStatus(ellipseActivity.getTaskStatus());
		p6Activity.setAddressUDF(ellipseActivity.getAddress());
		p6Activity.seteGIUDF(ellipseActivity.getEGI());
		p6Activity.setEllipseStandardJobUDF(ellipseActivity.getEllipseStandardJob());
		p6Activity.setEquipmentCodeUDF(ellipseActivity.getEquipmentCode());
		p6Activity.setEquipmentNoUDF(ellipseActivity.getEquipmentNo());
		p6Activity.setEstimatedLabourHours(P6Utility.covertStringToDouble(ellipseActivity.getEstimatedLabourHours()));
		p6Activity.setFeederUDF(ellipseActivity.getFeeder());
		p6Activity.setLocationInStreetUDF(ellipseActivity.getLocationInStreet());
		p6Activity.setOriginalDuration(ellipseActivity.getOriginalDuration());
		p6Activity.setPickIdUDF(ellipseActivity.getPlantNoOrPickId());

		/*
		 * The activity is getting created in P6 when Planned start date is
		 * blank in Ellipse then the Planned Start date in P6 will be updated to
		 * 1st of the financial year (1-July) based on the required by date in
		 * Ellipse
		 */
		if ((null == ellipseActivity.getPlannedStartDate() || ellipseActivity.getPlannedStartDate().isEmpty())
				&& (null == ellipseActivity.getRequiredByDate() || !ellipseActivity.getRequiredByDate().isEmpty())) {
			p6Activity.setPlannedStartDate(dateUtil.getStartDateOfFiscalYear(ellipseActivity.getRequiredByDate(),
					DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		} else {
			p6Activity.setPlannedStartDate(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
					DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		}
		p6Activity.setRemainingDuration(ellipseActivity.getRemainingDuration());
		p6Activity.setRequiredByDateUDF(ellipseActivity.getRequiredByDate());
		p6Activity.setSlippageCodeUDF(ellipseActivity.getSlippageCode());
		p6Activity.setTaskDescriptionUDF(ellipseActivity.getTaskDescription());
		p6Activity.setTaskUserStatusUDF(ellipseActivity.getTaskUserStatus());
		p6Activity.setUpStreamSwitchUDF(ellipseActivity.getUpStreamSwitch());
		p6Activity.setWorkGroup(workGroup);
		p6Activity.setPrimaryResorceObjectId(projectWorkgroup.get(workGroup).getPrimaryResourceObjectId());
		/*
		 * the WO task is read from Ellipse when the corresponding activity
		 * needs to be created in P6 with all the details then it should be
		 * created in the project based on the work group that is assigned on
		 * the task
		 */

		final int projectObjectId = projectWorkgroup.get(workGroup) != null
				? projectWorkgroup.get(workGroup).getProjectObjectId() : 0;

		if (projectObjectId != 0) {
			p6Activity.setProjectObjectId(projectObjectId);
		}
		return p6Activity;
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

}
