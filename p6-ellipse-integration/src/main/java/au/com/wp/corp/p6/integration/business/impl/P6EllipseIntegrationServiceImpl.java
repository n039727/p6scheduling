/**
 * 
 */
package au.com.wp.corp.p6.integration.business.impl;

import java.util.ArrayList;
import java.util.Date;
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
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.threads.CreateP6ActivityThread;
import au.com.wp.corp.p6.integration.threads.DeleteP6ActivityThread;
import au.com.wp.corp.p6.integration.threads.ReadEllipseThread;
import au.com.wp.corp.p6.integration.threads.ReadP6ActivityThread;
import au.com.wp.corp.p6.integration.threads.UpdateEllipseActivityThread;
import au.com.wp.corp.p6.integration.threads.UpdateP6ActivityThread;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.util.P6Utility;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadProcessStatus;
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

	public static final String USER_STATUS_AL = "AL";

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
			throw e;
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

		Map<String, Integer> projWorkgroupDTOs = p6WSClient.readResources();

		logger.debug("List of resource from P6# {}", projWorkgroupDTOs.keySet());

		Map<String, P6ProjWorkgroupDTO> projectWorkgroupMap = CacheManager.getP6ProjectWorkgroupMap();

		Map<String, List<String>> projectWorkgroupListMap = CacheManager.getProjectWorkgroupListMap();

		try {
			List<String> projects;
			for (P6ProjWorkgroupDTO projectWG : p6PortalDAO.getProjectResourceMappingList()) {

				if (null == projectWorkgroupListMap.get(projectWG.getProjectName())) {
					projects = new ArrayList<>();
					projectWorkgroupListMap.put(projectWG.getProjectName(), projects);
				} else {
					projects = projectWorkgroupListMap.get(projectWG.getProjectName());
				}

				logger.debug("primary resource - resource id # {}", projectWG.getPrimaryResourceId());
				if (null != projectWG.getPrimaryResourceId()
						&& null != projWorkgroupDTOs.get(projectWG.getPrimaryResourceId())) {
					projectWG.setPrimaryResourceObjectId(projWorkgroupDTOs.get(projectWG.getPrimaryResourceId()));
					projects.add(projectWG.getPrimaryResourceId());
					projectWorkgroupMap.put(projectWG.getPrimaryResourceId(), projectWG);
				}

			}
			status = true;
			logger.debug("Size of project resource/workgroup mapping from P6 Portal# {}", projectWorkgroupMap.size());
			logger.debug("Size of project resource/workgroup mapping List from P6 Portal# {}",
					projectWorkgroupListMap.size());

			logger.debug("Time taken to read record from P6 Portal # {} ", System.currentTimeMillis() - startTime);
		} catch (P6DataAccessException e) {
			logger.error("An error occurs while readeing Project Resource/workgroup mapping from P6 Portal:", e);
			throw e;

		}

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
	public void createActivityInP6(final List<P6ActivityDTO> createActivityP6Set) {
		logger.debug("create activites in p6 - number of activities # {}", createActivityP6Set.size());
		CreateP6ActivityThread thread = new CreateP6ActivityThread(createActivityP6Set, p6WSClient);
		new Thread(thread).start();

	}

	@Override
	public List<P6ActivityDTO> startEllipseToP6Integration() throws P6BusinessException {
		readUDFTypeMapping();
		readProjectWorkgroupMapping();
		logger.info("Starting all rading threads ....");
		final String sleepTime = P6ReloadablePropertiesReader.getProperty(POLING_TIME_TO_CHECK_READ_STATUS_INMILI);
		long sleepTimeLong;
		try {
			sleepTimeLong = Long.valueOf(sleepTime);
		} catch (NumberFormatException e1) {
			throw new P6BusinessException("Polling time to check read status should be a number ", e1);
		}

		final ReadEllipseThread readEllipse = new ReadEllipseThread(p6EllipseDAO);
		final ReadP6ActivityThread readP6Activity = new ReadP6ActivityThread(p6WSClient);
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
					.get(ProcessStatus.ELLIPSE_READ_STATUS) == ReadProcessStatus.COMPLETED
					&& CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_READ_STATUS) == ReadProcessStatus.COMPLETED) {
				ellipseActivites = CacheManager.getEllipseActivitiesMap();
				p6Activites = CacheManager.getP6ActivitiesMap();
				projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();
				break;
			} else if (CacheManager.getSystemReadWriteStatusMap()
					.get(ProcessStatus.ELLIPSE_READ_STATUS) == ReadProcessStatus.FAILED
					|| CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_READ_STATUS) == ReadProcessStatus.FAILED) {
				throw new P6BusinessException("An error occurs while reading data");
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

		Set<String> ellipseActivityIds = ellipseActivites.keySet();
		for (String activityId : ellipseActivityIds) {
			EllipseActivityDTO ellipseActivity = ellipseActivites.get(activityId);
			if (!p6Activites.containsKey(activityId)) {
				// Identifying the activities which will be created in P6
				P6ActivityDTO p6Activity = constructP6ActivityDTO(ellipseActivity, projectWorkGropMap);
				createActivityP6Set.add(p6Activity);

			} else if (p6Activites.containsKey(activityId)) {
				logger.debug("Ellipse activity id #{},  - ellipse workgroup # {},  - p6 workgroup # {}", activityId,
						ellipseActivity.getWorkGroup(), p6Activites.get(activityId).getWorkGroup());
				/*
				 * if the assigned resource got changed in ellipse, then create
				 * it under the project where the assigned resource belongs to
				 * and remove it from old project
				 */

				if (null != ellipseActivity.getWorkGroup()
						&& !ellipseActivity.getWorkGroup().equals(p6Activites.get(activityId).getWorkGroup())
						&& null != projectWorkGropMap.get(ellipseActivity.getWorkGroup())
						&& null != p6Activites.get(activityId).getWorkGroup()
						&& null != projectWorkGropMap.get(p6Activites.get(activityId).getWorkGroup())
						&& projectWorkGropMap.get(ellipseActivity.getWorkGroup())
								.getProjectObjectId() != projectWorkGropMap
										.get(p6Activites.get(activityId).getWorkGroup()).getProjectObjectId()) {
					P6ActivityDTO p6Activity = constructP6ActivityDTO(ellipseActivity, projectWorkGropMap);
					createActivityP6Set.add(p6Activity);
					deleteActivityP6Set.add(p6Activites.get(activityId));
				} else {
					logger.debug(
							"Ellipse and P6 activity id matched ... updating P6 and ellipse based on master information");

					// Identifying the activities which will be created in P6
					final P6ActivityDTO p6ActivityDTO = syncEllipseP6Activity(p6Activites.get(activityId),
							ellipseActivites.get(activityId), projectWorkGropMap);
					if (null != p6ActivityDTO)
						updateActivityP6Set.add(p6ActivityDTO);

					final EllipseActivityDTO ellipseActivityDTO = syncP6EllipseActivity(p6Activites.get(activityId),
							ellipseActivites.get(activityId), projectWorkGropMap);
					if (null != ellipseActivityDTO)
						updateActivityEllipseSet.add(ellipseActivityDTO);

				}
				p6Activites.remove(activityId, p6Activites.get(activityId));
			}
		}

		deleteActivityP6Set.addAll(p6Activites.values());

		createActivityInP6(createActivityP6Set);
		updateActivitiesInP6(updateActivityP6Set);
		deleteActivityInP6(deleteActivityP6Set);
		updateActivitiesInEllipse(updateActivityEllipseSet);

		// Waiting threads to complete read processes
		while (true) {
			if (CacheManager.getSystemReadWriteStatusMap()
					.get(ProcessStatus.ELLIPSE_UPDATE_STATUS) == ReadProcessStatus.COMPLETED
					&& CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_CREATE_STATUS) == ReadProcessStatus.COMPLETED
					&& CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS) == ReadProcessStatus.COMPLETED
					&& CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_DELETE_STATUS) == ReadProcessStatus.COMPLETED) {
				break;
			} else if (CacheManager.getSystemReadWriteStatusMap()
					.get(ProcessStatus.ELLIPSE_UPDATE_STATUS) == ReadProcessStatus.FAILED
					|| CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_CREATE_STATUS) == ReadProcessStatus.FAILED
					|| CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_UPDATE_STATUS) == ReadProcessStatus.FAILED
					|| CacheManager.getSystemReadWriteStatusMap()
							.get(ProcessStatus.P6_ACTIVITY_DELETE_STATUS) == ReadProcessStatus.FAILED) {
				throw new P6BusinessException("An error occurs while create /update/ delete  data");
			}

			try {
				Thread.currentThread().sleep(sleepTimeLong);
			} catch (InterruptedException e) {
				logger.error("the current thread has been interupted while writting ellipse and P6");
				Thread.currentThread().interrupt();
				throw new P6BusinessException("Current thread gets interrupted ", e);
			}

		}
		logger.info("Ellipse,P6 integration is completed........... ");
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
		logger.debug("Indentifying activities to update in ellipse....");
		logger.debug("Activity details in P6 - activity Id # {}, - workgroup # {}, - planned start date # {}",
				p6Activity.getActivityId(), p6Activity.getWorkGroup(), p6Activity.getPlannedStartDate());

		logger.debug("Activity details in P6 - activity Id # {}, - workgroup # {}, - planned start date # {}",
				p6Activity.getActivityId(), p6Activity.getWorkGroup(), p6Activity.getPlannedStartDate());

		final EllipseActivityDTO ellipseActivityUpd = new EllipseActivityDTO();
		boolean isUpdateReq = false;
		ellipseActivityUpd.setWorkOrderTaskId(ellipseActivity.getWorkOrderTaskId());

		/*
		 * in P6 activity Work group is changed when comparing the data between
		 * Ellipse and P6 then update the Crew from P6 to Work Group in Ellipse.
		 */
		if (null != p6Activity.getWorkGroup() && !p6Activity.getWorkGroup().equals(ellipseActivity.getWorkGroup())) {
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
				&& projectWorkgroup.get(p6Activity.getWorkGroup()).getSchedulerinbox().equals(P6EllipseWSConstants.Y)) {
			ellipseActivityUpd.setPlannedStartDate(null);
			isUpdateReq = true;

		} else if (null == ellipseActivity.getPlannedStartDate() || ellipseActivity.getPlannedStartDate().isEmpty()
				|| (null != p6Activity.getPlannedStartDate() && !p6Activity.getPlannedStartDate()
						.equals(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
								DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
								DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP)))) {
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
				|| (null != p6Activity.getPlannedStartDate() && !p6Activity.getPlannedStartDate()
						.equals(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
								DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
								DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP)))) {
			ellipseActivityUpd.setTaskUserStatus("AL");
			isUpdateReq = true;
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

		p6ActivityUpd.setActivityObjectId(p6Activity.getActivityObjectId());
		p6ActivityUpd.setActivityId(p6Activity.getActivityId());

		if (null != ellipseActivity.getWorkGroup()
				&& !ellipseActivity.getWorkGroup().equals(p6Activity.getWorkGroup())) {
			p6ActivityUpd.setWorkGroup(ellipseActivity.getWorkGroup());
			p6ActivityUpd.setPrimaryResorceObjectId(
					projectWorkgroup.get(ellipseActivity.getWorkGroup()).getPrimaryResourceObjectId());
			isUpdateReq = true;
		}

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

		if (null != ellipseActivity.getJdCode()
				&& !ellipseActivity.getJdCode().equals(p6Activity.getActivityJDCodeUDF())) {
			p6ActivityUpd.setActivityJDCodeUDF(ellipseActivity.getJdCode());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getEquipmentNo()
				&& !ellipseActivity.getEquipmentNo().equals(p6Activity.getEquipmentNoUDF())) {
			p6ActivityUpd.setEquipmentNoUDF(ellipseActivity.getEquipmentNo());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getPlantNoOrPickId()
				&& !ellipseActivity.getPlantNoOrPickId().equals(p6Activity.getPickIdUDF())) {
			p6ActivityUpd.setPickIdUDF(ellipseActivity.getPlantNoOrPickId());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getEGI() && !ellipseActivity.getEGI().equals(p6Activity.geteGIUDF())) {
			p6ActivityUpd.seteGIUDF(ellipseActivity.getEGI());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getEquipmentCode()
				&& !ellipseActivity.getEquipmentCode().equals(p6Activity.getEquipmentCodeUDF())) {
			p6ActivityUpd.setEquipmentCodeUDF(ellipseActivity.getEquipmentCode());
			isUpdateReq = true;
		}

		if (!P6Utility.isEqual(p6Activity.getOriginalDuration(), ellipseActivity.getOriginalDuration())) {
			p6ActivityUpd.setOriginalDuration(ellipseActivity.getOriginalDuration());
			isUpdateReq = true;
		}

		if (!P6Utility.isEqual(p6Activity.getRemainingDuration(), ellipseActivity.getRemainingDuration())) {
			p6ActivityUpd.setRemainingDuration(ellipseActivity.getRemainingDuration());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getRequiredByDate()
				&& !ellipseActivity.getRequiredByDate().equals(p6Activity.getRequiredByDateUDF())) {
			p6ActivityUpd.setRequiredByDateUDF(ellipseActivity.getRequiredByDate());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getEllipseStandardJob()
				&& !ellipseActivity.getEllipseStandardJob().equals(p6Activity.getEllipseStandardJobUDF())) {
			p6ActivityUpd.setEllipseStandardJobUDF(ellipseActivity.getEllipseStandardJob());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getFeeder() && !ellipseActivity.getFeeder().equals(p6Activity.getFeederUDF())) {
			p6ActivityUpd.setFeederUDF(ellipseActivity.getFeeder());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getUpStreamSwitch()
				&& !ellipseActivity.getUpStreamSwitch().equals(p6Activity.getUpStreamSwitchUDF())) {
			p6ActivityUpd.setUpStreamSwitchUDF(ellipseActivity.getUpStreamSwitch());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getEstimatedLabourHours()
				&& !P6Utility.isEqual(P6Utility.covertStringToDouble(ellipseActivity.getEstimatedLabourHours()),
						p6Activity.getEstimatedLabourHours())) {
			p6ActivityUpd
					.setEstimatedLabourHours(P6Utility.covertStringToDouble(ellipseActivity.getEstimatedLabourHours()));
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getAddress() && !ellipseActivity.getAddress().equals(p6Activity.getAddressUDF())) {
			p6ActivityUpd.setAddressUDF(ellipseActivity.getAddress());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getLocationInStreet()
				&& !ellipseActivity.getLocationInStreet().equals(p6Activity.getLocationInStreetUDF())) {
			p6ActivityUpd.setLocationInStreetUDF(ellipseActivity.getLocationInStreet());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getTaskDescription()
				&& !ellipseActivity.getTaskDescription().equals(p6Activity.getTaskDescriptionUDF())) {
			p6ActivityUpd.setTaskDescriptionUDF(ellipseActivity.getTaskDescription());
			isUpdateReq = true;
		}

		if (null != ellipseActivity.getTaskStatus()
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
		logger.debug("Planned start date in ellipse # {}", ellipseActivity.getPlannedStartDate());
		if (null != ellipseActivity.getTaskUserStatus() && null != ellipseActivity.getPlannedStartDate()
				&& !ellipseActivity.getTaskUserStatus().equals(P6EllipseWSConstants.RR)
				&& !dateUtil.isCurrentDate(ellipseActivity.getPlannedStartDate())) {
			p6ActivityUpd.setTaskUserStatusUDF(ellipseActivity.getTaskUserStatus());
			isUpdateReq = true;
		}

		/*
		 * WO task user group and Work group against activity in P6 is assigned
		 * to the Scheduler Inbox when comparing the data between P6 and Ellipse
		 * then the Execution Package field in P6 should be updated to blank. 
		 */

		if (null != projectWorkgroup.get(ellipseActivity.getWorkGroup())
				&& null != projectWorkgroup.get(ellipseActivity.getWorkGroup()).getSchedulerinbox() && projectWorkgroup
						.get(ellipseActivity.getWorkGroup()).getSchedulerinbox().equals(P6EllipseWSConstants.Y)) {
			p6ActivityUpd.setExecutionPckgUDF("");
		} else {
			p6ActivityUpd.setExecutionPckgUDF(p6Activity.getExecutionPckgUDF());
		}

		/*
		 * The activity is getting created in P6 when Planned start date is
		 * blank in Ellipse then the Planned Start date in P6 will be updated to
		 * 1st of the financial year (1-July) based on the required by date in
		 * Ellipse
		 */
		if (null == ellipseActivity.getPlannedStartDate() || ellipseActivity.getPlannedStartDate().isEmpty()) {
			p6ActivityUpd.setPlannedStartDate(dateUtil.getStartDateOfFiscalYear(dateUtil.getCurrentDate(),
					DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP));

		}

		/*
		 * the WO task is read from Ellipse when the corresponding activity
		 * needs to be created in P6 with all the details then it should be
		 * created in the project based on the work group that is assigned on
		 * the task
		 */
		final int projectObjectId = projectWorkgroup.get(ellipseActivity.getWorkGroup()) != null
				? projectWorkgroup.get(ellipseActivity.getWorkGroup()).getProjectObjectId() : 0;

		if (projectObjectId != 0) {
			p6ActivityUpd.setProjectObjectId(projectObjectId);
		}

		if (null != ellipseActivity.getActualStartDate() && !ellipseActivity.getActualStartDate().trim().isEmpty()) {
			p6ActivityUpd.setActualStartDate(dateUtil.convertDateToString(ellipseActivity.getActualStartDate(),
					DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		}

		if (null != ellipseActivity.getActualFinishDate() && !ellipseActivity.getActualFinishDate().trim().isEmpty()) {
			p6ActivityUpd.setActualFinishDate(dateUtil.convertDateToString(ellipseActivity.getActualFinishDate(),
					DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
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
			Map<String, P6ProjWorkgroupDTO> projectWorkgroup) throws P6BusinessException {
		logger.debug("Constructing new activity for p6...");
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
		if (null == ellipseActivity.getPlannedStartDate() || ellipseActivity.getPlannedStartDate().isEmpty()) {
			p6Activity.setPlannedStartDate(dateUtil.getStartDateOfFiscalYear(new Date()));

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
		p6Activity.setWorkGroup(ellipseActivity.getWorkGroup());
		p6Activity.setPrimaryResorceObjectId(
				projectWorkgroup.get(ellipseActivity.getWorkGroup()).getPrimaryResourceObjectId());
		/*
		 * the WO task is read from Ellipse when the corresponding activity
		 * needs to be created in P6 with all the details then it should be
		 * created in the project based on the work group that is assigned on
		 * the task
		 */
		final int projectObjectId = projectWorkgroup.get(ellipseActivity.getWorkGroup()) != null
				? projectWorkgroup.get(ellipseActivity.getWorkGroup()).getProjectObjectId() : 0;

		if (projectObjectId != 0) {
			p6Activity.setProjectObjectId(projectObjectId);
		}
		return p6Activity;
	}

	@Override
	public void clearApplicationMemory() {
		CacheManager.getEllipseActivitiesMap().clear();
		CacheManager.getP6ActivitiesMap().clear();
		CacheManager.getP6ProjectWorkgroupMap().clear();
		CacheManager.getProjectWorkgroupListMap().clear();
		CacheManager.getSystemReadWriteStatusMap().clear();
		CacheManager.getWsHeaders().clear();
	}

}
