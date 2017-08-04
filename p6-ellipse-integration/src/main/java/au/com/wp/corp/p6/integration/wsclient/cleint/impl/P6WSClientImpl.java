/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.integration.exception.P6ExceptionType;
import au.com.wp.corp.p6.integration.exception.P6IntegrationExceptionHandler;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.util.P6Utility;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.CreateActivity;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.CreateUDFValue;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.DeleteActivity;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.DeleteUDFValue;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.ReadActivity;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.ReadUDFValue;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.UpdateActivity;
import au.com.wp.corp.p6.integration.wsclient.cleint.qualifier.UpdateUDFValue;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.ObjectFactory;
import au.com.wp.corp.p6.wsclient.project.Project;
import au.com.wp.corp.p6.wsclient.resource.Resource;
import au.com.wp.corp.p6.wsclient.resourceassignment.ResourceAssignment;
import au.com.wp.corp.p6.wsclient.udftype.UDFType;
import au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * 
 * @author n039126
 * @version 1.0
 */
@Service
public class P6WSClientImpl implements P6WSClient, P6EllipseWSConstants {
	private static final Logger logger = LoggerFactory.getLogger(P6WSClientImpl.class);

	@Autowired
	DateUtil dateUtil;

	@Autowired
	P6IntegrationExceptionHandler exceptionHandler;

	@Autowired
	@CreateActivity
	ActivityServiceCall<List<Integer>> crActivityService;
	
	@Autowired
	@ReadActivity
	ActivityServiceCall<List<Activity>> rdActservice;

	@Autowired
	@UpdateActivity
	ActivityServiceCall<Boolean> updActivityService;
	
	@Autowired
	@DeleteActivity
	ActivityServiceCall<Boolean> delActivityService;

	@Autowired
	@CreateUDFValue
	UDFValueServiceCall<List<au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId>> crdUdfValueService;

	@Autowired
	@UpdateUDFValue
	UDFValueServiceCall<Boolean> updUdfValueService;
	
	@Autowired
	@ReadUDFValue
	UDFValueServiceCall<List<UDFValue>> readUdfValueService;
	
	@Autowired
	@DeleteUDFValue
	UDFValueServiceCall<Boolean> delUdfValueServiceCall;
	
	@Autowired
	ProjectServiceCall projectService;

	@Autowired
	ResourceAssignmentServiceCall<List<ResourceAssignment>> readResourceAssignmentService ;
	
	@Autowired
	AuthenticationService authService;
	
	@Autowired
	UDFTypeServiceCall udfTypeServiceCall;
	
	@Autowired
	ResourceService resourceService;
	
	@Autowired
	LogoutServiceCall logoutService;
	
	@Override
	public Map<String, Integer> readProjects() throws P6ServiceException {
		logger.info("Calling project service in P6 Webservice ...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated();
		final List<Project> projects = projectService.readProjects();
		logger.debug("list of projects from P6#{}", projects);

		Map<String, Integer> projectsMap = new HashMap<>();
		if (null == projects || projects == null) {
			throw new P6ServiceException("No projects available in P6");
		}
		for (Project project : projects)
			projectsMap.put(project.getName(), project.getObjectId());
		return projectsMap;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.wsclient.cleint.P6WSClient#searchWorkOrder(au.com.wp.
	 * corp.p6.model.ActivitySearchRequest)
	 */
	@Override
	public List<P6ActivityDTO> readActivities(final Integer projectId) throws P6ServiceException {
		logger.info("Calling read activity service in P6 Webservice ...");
		getAuthenticated();

		final StringBuilder filter = new StringBuilder();
		if (projectId != null) {
			filter.append("ProjectObjectId IN ");
			filter.append("(");
			filter.append(projectId.intValue());
			filter.append(")");

		}

		final List<Activity> activities = rdActservice
				.readActivities(filter.toString().isEmpty() ? null : filter.toString());
		logger.debug("list of activities from P6#{}", activities);

		final StringBuilder udfValueFilter = createFilters(activities, P6EllipseWSConstants.FOREIGN_OBJECT_ID);

		final Map<Integer, List<UDFValue>> udfValueMap = readUDFvalues(udfValueFilter);
		final StringBuilder resourceAssignmentFilter = createFilters(activities,
				P6EllipseWSConstants.ACTIVITY_OBJECT_ID);
		final Map<Integer, ResourceAssignment> resourceAssignments = readResourceAssignments(resourceAssignmentFilter);

		logger.debug("UDFValues from P6 # {}", udfValueMap);

		final List<P6ActivityDTO> activityDTOs = new ArrayList<>();
		P6ActivityDTO activityDTO;
		List<Activity> activityList = activities;
		int activitySize = activityList.size();
		for (int i = activitySize; --i >= 0;) {
			activityDTO = new P6ActivityDTO();
			Activity activity = activityList.get(i);
			activityDTO.setActivityObjectId(activity.getObjectId());
			activityDTO.setActivityId(activity.getId());
			activityDTO.setActivityName(activity.getName());
			activityDTO.setActivityStatus(activity.getStatus());
			activityDTO.setPlannedStartDate(activity.getPlannedStartDate().toString());

			if (null != activity.getActualStartDate() && null != activity.getActualStartDate().getValue()) {
				activityDTO.setActualStartDate(activity.getActualStartDate().getValue().toString());
			}

			if (null != activity.getActualFinishDate() && null != activity.getActualFinishDate().getValue()) {
				activityDTO.setActualFinishDate(activity.getActualFinishDate().getValue().toString());
			}
			activityDTO.setWorkGroup(activity.getPrimaryResourceId());
			activityDTO.setOriginalDuration(activity.getPlannedDuration());
			activityDTO.setRemainingDuration(activity.getRemainingDuration().getValue());
			activityDTO.setProjectObjectId(activity.getProjectObjectId());
			activityDTO.setEstimatedLabourHours(activity.getPlannedLaborUnits());
			ResourceAssignment resAssign = resourceAssignments.get(activity.getObjectId());
			if (null != resAssign) {
				activityDTO.setEstimatedLabourHours(resAssign.getPlannedDuration().getValue());
				activityDTO.setEstimatedLabourHoursObjectId(resAssign.getObjectId());
			}
			// setting all udf fields
			List<UDFValue> udfValueList = udfValueMap.get(activity.getObjectId());

			if (null != udfValueList) {
				int udfsize = udfValueList.size();
				for (int j = udfsize; --j >= 0;) {
					UDFValue udfValue = udfValueList.get(j);
					setUDFValues(activityDTO, udfValue);
				}
			}

			activityDTOs.add(activityDTO);
		}
		logger.info("P6 Reading is completed .........................");
		return activityDTOs;
	}

	/**
	 * @param activityDTO
	 * @param udfValue
	 */
	private void setUDFValues(P6ActivityDTO activityDTO, UDFValue udfValue) {
		if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_EGI_TITLE))) {
			activityDTO.seteGIUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_EQUIP_CODE_TITLE))) {
			activityDTO.setEquipmentCodeUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_EQUIPMENT_NO_TITLE))) {
			activityDTO.setEquipmentNoUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_FEEEDER_TITLE))) {
			activityDTO.setFeederUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_PICK_ID_TITLE))) {
			activityDTO.setPickIdUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_REQ_BY_DATE_TITLE))) {
			activityDTO.setRequiredByDateUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_STD_JOB_TITLE))) {
			activityDTO.setEllipseStandardJobUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_TASK_USER_STATUS_TITLE))) {
			activityDTO.setTaskUserStatusUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_UPSTREAM_SWITCH_TITLE))) {
			activityDTO.setUpStreamSwitchUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_ADDRESS_TITLE))) {
			activityDTO.setAddressUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_JD_CODE_TITLE))) {
			activityDTO.setActivityJDCodeUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_TASK_DESC_TITLE))) {
			activityDTO.setTaskDescriptionUDF(udfValue.getText());
		} else if (udfValue.getUDFTypeTitle().equals(getProperty(ELLIPSE_EXECUTION_PCKG_TITLE))) {
			activityDTO.setExecutionPckgUDF(udfValue.getText());
		}
	}

	/**
	 * @param activities
	 * @return
	 */
	private StringBuilder createFilters(final List<Activity> activities, String filterParam) {
		int i = 0;
		final StringBuilder filter = new StringBuilder();
		int activitySize = activities.size();
		if (!activities.isEmpty()) {
			filter.append(filterParam + " IN (");
			for (Activity activity : activities) {
				if (i > 0)
					filter.append(",");
				filter.append(activity.getObjectId());
				i++;
				activitySize = activitySize - 1;
				if (i == 999 && activitySize != 0) {
					filter.append(")");
					filter.append(OR);
					filter.append(filterParam + " IN (");
					logger.debug("Filter criteria length for Read Value services # {} ", i);
					i = 0;
				}

			}
			filter.append(")");
		}
		return filter;
	}

	/**
	 * @param trackingId
	 * @param filter
	 * @return
	 * @throws P6ServiceException
	 */
	private Map<Integer, List<UDFValue>> readUDFvalues(final StringBuilder filter) throws P6ServiceException {

		final List<UDFValue> udfValues = readUdfValueService.readUDFValues(filter.toString(), null);

		final Map<Integer, List<UDFValue>> udfValueMap = CacheManager.getUDFValueMap();
		List<UDFValue> udfValueList;
		Integer foreignObjectId;
		for (UDFValue udfValue : udfValues) {
			foreignObjectId = udfValue.getForeignObjectId();
			if (null == udfValueMap.get(foreignObjectId)) {
				udfValueList = new ArrayList<>();
				udfValueMap.put(foreignObjectId, udfValueList);
			} else {
				udfValueList = udfValueMap.get(foreignObjectId);
			}
			udfValueList.add(udfValue);
		}
		return udfValueMap;
	}

	/**
	 * @param trackingId
	 * @param filter
	 * @return
	 * @throws P6ServiceException
	 */
	private Map<Integer, ResourceAssignment> readResourceAssignments(final StringBuilder filter)
			throws P6ServiceException {

		final List<ResourceAssignment> resourceAssignments = readResourceAssignmentService
				.readResourceAssigment(filter.toString());

		final Map<Integer, ResourceAssignment> resourceAssignmenteMap = new HashMap<>();
		Integer activityObjectId;
		for (ResourceAssignment resourceAssignment : resourceAssignments) {
			activityObjectId = resourceAssignment.getActivityObjectId();
			resourceAssignmenteMap.put(activityObjectId, resourceAssignment);
		}
		return resourceAssignmenteMap;
	}

	/**
	 * @param trackingId
	 * @throws P6ServiceException
	 */
	private Boolean getAuthenticated() throws P6ServiceException {
		if (null == CacheManager.getWsHeaders().get(WS_COOKIE)) {
			boolean status = authService.run();
			logger.debug("Is authentication successfull ??  {} ", status);
			return status;
		}

		return false;
	}

	@Override
	public boolean logoutFromP6() {
		final RequestTrackingId trackingId = new RequestTrackingId();
		boolean status = false;
		if (null != CacheManager.getWsHeaders().get(WS_COOKIE)) {
			try {
				status = logoutService.logout();
			} catch (P6ServiceException e) {
				logger.error("Error occurs during logout - ", e);
			}
			logger.info("Is logout successfull ??  {} ", status);
		}

		return status;

	}

	@Override
	public void createActivities(final List<P6ActivityDTO> activities) throws P6ServiceException {
		createOrUpdateActivities(activities, true);
	}

	/**
	 * 
	 * @param activities
	 * @param isCreate
	 * @throws P6ServiceException
	 */
	private void createOrUpdateActivities(final List<P6ActivityDTO> activities, final boolean isCreateActivities)
			throws P6ServiceException {
		if (null == activities || activities.isEmpty()) {
			throw new P6ServiceException("List of activties can't be null or empty");
		}

		logger.info("Create/update activites in P6 ... number of activites # {}", activities.size());
		getAuthenticated();

		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty(NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_P6);
		logger.debug("Defined P6 webservice call trigger value# {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);
		int crdActivitySize = activities.size();
		logger.debug("Number of calls to P6 web service create/update Activity # {}",
				(crdActivitySize / chunkSize) + 1);

		for (int i = 0; i < (crdActivitySize / chunkSize) + 1; i++) {
			try {
				final Map<String, P6ActivityDTO> activityMap = new HashMap<>();

				final List<Activity> crtdActivities = constructActivities(activities, chunkSize, i, activityMap);

				if (isCreateActivities && !crtdActivities.isEmpty()) {
					logger.info("Creating activites service call in P6...................");
					final List<Integer> activityIds = crActivityService.createActivities(crtdActivities);
					logger.debug("list of activities from P6 # {}", activityIds);
					int j = 0;
					final StringBuilder filter = new StringBuilder();
					filter.append("ObjectId IN (");
					for (Integer activityObjectId : activityIds) {
						if (j > 0)
							filter.append(",");
						filter.append(activityObjectId);
						j++;
						if (j == 999) {
							filter.append(")");
							filter.append(OR);
							filter.append("ObjectId IN (");
							logger.debug("Filter criteria length for Read Value services # {} ", i);
							j = 0;
						}

					}

					filter.append(")");
					final List<Activity> newlyCrdActivites = rdActservice.readActivities(filter.toString());

					for (Activity newActivity : newlyCrdActivites) {
						activityMap.get(newActivity.getId()).setActivityObjectId(newActivity.getObjectId());
					}

					final List<P6ActivityDTO> crdActivityFileds = new ArrayList<>();
					crdActivityFileds.addAll(activityMap.values());
					createActivityFieldsUDF(delUdfValueServiceCall, crdUdfValueService, crdActivityFileds);
				} else if (!crtdActivities.isEmpty()) {
					logger.info("Update activity service call in P6 ........................");
					final boolean status = updActivityService.updateActivities(crtdActivities);
					logger.debug("list of activities from P6 # {}", status);
					int startIndex = i * chunkSize;
					int endIndex = ((i + 1) * chunkSize) < activities.size() ? ((i + 1) * chunkSize)
							: activities.size();

					updateActivityFieldsUDF(delUdfValueServiceCall, updUdfValueService, crdUdfValueService,
							activities.subList(startIndex, endIndex), chunkSize);

				}
			} catch (P6ServiceException e) {
				logger.debug("error - ", e);
				if (e.getMessage().equals(P6ExceptionType.DATA_ERROR.name())) {
					int startIndex = i * chunkSize;
					int endIndex = ((i + 1) * chunkSize) < activities.size() ? ((i + 1) * chunkSize)
							: activities.size();

					StringBuilder sb = new StringBuilder();
					sb.append(e.getCause().getMessage());
					sb.append(" for any workorder with in the list [ ");
					for (P6ActivityDTO activity : activities.subList(startIndex, endIndex)) {
						sb.append(activity.getActivityId());
						sb.append(",");
					}
					sb.append("]");

					exceptionHandler.handleException(new P6ServiceException(sb.toString()));
				} else {
					throw e;
				}

			}

		}

	}

	/**
	 * @param activities
	 * @param chunkSize
	 * @param i
	 * @param activityMap
	 * @return
	 */
	private List<Activity> constructActivities(final List<P6ActivityDTO> activities, final int chunkSize, int i,
			final Map<String, P6ActivityDTO> activityMap) {
		ObjectFactory objectFactory = new ObjectFactory();
		final List<Activity> _activities = new ArrayList<>();
		Activity activity;
		int startIndex = i * chunkSize;
		int endIndex = ((i + 1) * chunkSize) < activities.size() ? ((i + 1) * chunkSize) : activities.size();

		logger.debug("constructing activity start index # {}  - end index # {}", startIndex, endIndex);

		for (P6ActivityDTO p6ActivityDTO : activities.subList(startIndex, endIndex)) {
			activity = new Activity();
			activity.setId(p6ActivityDTO.getActivityId());
			activity.setName(p6ActivityDTO.getActivityName());

			if (null != p6ActivityDTO.getActivityStatus() && !p6ActivityDTO.getActivityStatus().trim().isEmpty())
				activity.setStatus(p6ActivityDTO.getActivityStatus());

			if (null != p6ActivityDTO.getPlannedStartDate() && !p6ActivityDTO.getPlannedStartDate().trim().isEmpty()) {
				final XMLGregorianCalendar plannedStartDate = dateUtil
						.convertStringToXMLGregorianClalander(p6ActivityDTO.getPlannedStartDate());
				if (null != plannedStartDate)
					activity.setPlannedStartDate(plannedStartDate);
				else
					logger.error("Invalid planned start date# {}", p6ActivityDTO.getPlannedStartDate());
			}
			if (null != p6ActivityDTO.getActualFinishDate()) {
				String actStartDate = p6ActivityDTO.getActualStartDate();
				if (dateUtil.compare(p6ActivityDTO.getActualStartDate(), DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
						p6ActivityDTO.getActualFinishDate(), DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP) == 1) {

					actStartDate = dateUtil.substractMinuteFromDate(p6ActivityDTO.getActualFinishDate(),
							DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP);
				}

				XMLGregorianCalendar actualStartDate = dateUtil.convertStringToXMLGregorianClalander(actStartDate);
				if (null != actualStartDate) {
					activity.setActualStartDate(objectFactory.createActivityActualStartDate(actualStartDate));
				}
				final XMLGregorianCalendar actualFinishDate = dateUtil
						.convertStringToXMLGregorianClalander(p6ActivityDTO.getActualFinishDate());
				if (null != actualStartDate && null != actualFinishDate) {
					activity.setActualFinishDate(objectFactory.createActivityActualFinishDate(actualFinishDate));
				}
			}

			activity.setProjectObjectId(p6ActivityDTO.getProjectObjectId());
			if (p6ActivityDTO.getActivityObjectId() != null)
				activity.setObjectId(p6ActivityDTO.getActivityObjectId());

			if (p6ActivityDTO.getPrimaryResorceObjectId() != 0)
				activity.setPrimaryResourceObjectId(
						objectFactory.createActivityPrimaryResourceObjectId(p6ActivityDTO.getPrimaryResorceObjectId()));
			activity.setPrimaryResourceId(p6ActivityDTO.getWorkGroup());

			if (p6ActivityDTO.getOriginalDuration() > -1)
				activity.setPlannedDuration(p6ActivityDTO.getOriginalDuration());

			if (p6ActivityDTO.getRemainingDuration() > -1) {
				activity.setRemainingDuration(
						objectFactory.createActivityRemainingDuration(p6ActivityDTO.getRemainingDuration()));
			}

			if (!P6Utility.isEqual(p6ActivityDTO.getEstimatedLabourHours(), -1))
				activity.setPlannedLaborUnits(p6ActivityDTO.getEstimatedLabourHours());

			_activities.add(activity);

			activityMap.put(p6ActivityDTO.getActivityId(), p6ActivityDTO);
		}
		return _activities;
	}

	/**
	 * Prepare and create UDF Values for Activities in P6
	 * 
	 * @param trackingId
	 * @param activityDTOs
	 * @throws P6ServiceException
	 */
	private void createActivityFieldsUDF(final UDFValueServiceCall<Boolean> delUdfValueServiceCall,
			final UDFValueServiceCall<List<au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId>> udfValueService,
			List<P6ActivityDTO> activityDTOs) throws P6ServiceException {
		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty(NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_P6);
		logger.debug("Defined P6 UDFValueService webservice call trigger value # {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);

		final List<List<UDFValue>> udfValueList = new ArrayList<>();
		for (P6ActivityDTO activity : activityDTOs) {
			final List<UDFValue> udfValues = new ArrayList<>();
			findUDFValueForUDFType(delUdfValueServiceCall, udfValues, null, activity);
			udfValueList.add(udfValues);
		}

		int udfValueSize = udfValueList.size();
		logger.debug("Number of calls to P6 UDFValue web service #{}", (udfValueSize / chunkSize) + 1);

		for (int i = 0; i < (udfValueSize / chunkSize) + 1; i++) {
			int startIndex = i * chunkSize;

			int endIndex = ((i + 1) * chunkSize) < udfValueList.size() ? ((i + 1) * chunkSize) : udfValueList.size();

			logger.debug("calling UDFValue service with start index # {}  - end index # {}", startIndex, endIndex);

			List<UDFValue> udfValue1 = new ArrayList<>();
			for (List<UDFValue> udfValue : udfValueList.subList(startIndex, endIndex)) {
				udfValue1.addAll(udfValue);
			}
			if (!udfValue1.isEmpty())
				callUDFvalueService(udfValueService, udfValue1);

		}

	}

	private boolean isExistUDFValue(Integer activityObjectId, Integer udfTypeId) {
		List<UDFValue> udfValues = CacheManager.getUDFValueMap().get(activityObjectId);
		boolean status = false;
		if (null != udfValues) {
			for (UDFValue udfValue : udfValues) {
				if (udfValue.getUDFTypeObjectId().equals(udfTypeId)) {
					status = true;
					break;
				}
			}
		}

		return status;
	}

	/**
	 * @param createUdfValues
	 * @param activity
	 */
	private void findUDFValueForUDFType(final UDFValueServiceCall<Boolean> delUdfValueServiceCall,
			final List<UDFValue> createUdfValues, final List<UDFValue> updateUDFValues, P6ActivityDTO activity) {

		if (null != activity.getActivityJDCodeUDF() && !activity.getActivityJDCodeUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_JD_CODE_TITLE)), SUBJECT_AREA, getProperty(ELLIPSE_JD_CODE_TITLE),
					activity.getActivityJDCodeUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_JD_CODE_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}

		if (null != activity.geteGIUDF()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_EGI_TITLE)), SUBJECT_AREA, getProperty(ELLIPSE_EGI_TITLE),
					activity.geteGIUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_EGI_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}

		if (null != activity.getUpStreamSwitchUDF()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_UPSTREAM_SWITCH_TITLE)), SUBJECT_AREA,
					getProperty(ELLIPSE_UPSTREAM_SWITCH_TITLE), activity.getUpStreamSwitchUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_UPSTREAM_SWITCH_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}

		if (null != activity.getAddressUDF() && !activity.getAddressUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_ADDRESS_TITLE)), SUBJECT_AREA, getProperty(ELLIPSE_ADDRESS_TITLE),
					activity.getAddressUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_ADDRESS_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);

		}
		if (null != activity.getEllipseStandardJobUDF() && !activity.getEllipseStandardJobUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_STD_JOB_TITLE)), SUBJECT_AREA, getProperty(ELLIPSE_STD_JOB_TITLE),
					activity.getEllipseStandardJobUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_STD_JOB_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}
		if (null != activity.getEquipmentCodeUDF() && !activity.getEquipmentCodeUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_EQUIP_CODE_TITLE)), SUBJECT_AREA,
					getProperty(ELLIPSE_EQUIP_CODE_TITLE), activity.getEquipmentCodeUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_EQUIP_CODE_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}
		if (null != activity.getEquipmentNoUDF() && !activity.getEquipmentNoUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_EQUIPMENT_NO_TITLE)), SUBJECT_AREA,
					getProperty(ELLIPSE_EQUIPMENT_NO_TITLE), activity.getEquipmentNoUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_EQUIPMENT_NO_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);

		}

		if (null != activity.getFeederUDF() && !activity.getFeederUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_FEEEDER_TITLE)), SUBJECT_AREA, getProperty(ELLIPSE_FEEEDER_TITLE),
					activity.getFeederUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_FEEEDER_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}
		/**
		 * if (null != activity.getLocationInStreetUDF() &&
		 * !activity.getLocationInStreetUDF().isEmpty()) {
		 *
		 * UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
		 * 1234, SUBJECT_AREA, "", activity.getLocationInStreetUDF(), null); if
		 * (isExistUDFValue(activity.getActivityObjectId(),
		 * getUdfTypeId(getProperty(ELLIPSE_JD_CODE_TITLE))))
		 * updateUDFValues.add(udfValue); else createUdfValues.add(udfValue); }
		 **/
		if (null != activity.getPickIdUDF() && !activity.getPickIdUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_PICK_ID_TITLE)), SUBJECT_AREA, getProperty(ELLIPSE_PICK_ID_TITLE),
					activity.getPickIdUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_PICK_ID_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}
		if (null != activity.getRequiredByDateUDF() && !activity.getRequiredByDateUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_REQ_BY_DATE_TITLE)), SUBJECT_AREA,
					getProperty(ELLIPSE_REQ_BY_DATE_TITLE), activity.getRequiredByDateUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_REQ_BY_DATE_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}
		if (null != activity.getTaskDescriptionUDF() && !activity.getTaskDescriptionUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_TASK_DESC_TITLE)), SUBJECT_AREA,
					getProperty(ELLIPSE_TASK_DESC_TITLE), activity.getTaskDescriptionUDF(), null);
			if (isExistUDFValue(activity.getActivityObjectId(), getUdfTypeId(getProperty(ELLIPSE_TASK_DESC_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}
		if (null != activity.getTaskUserStatusUDF() && !activity.getTaskUserStatusUDF().isEmpty()) {
			UDFValue udfValue = createUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_TASK_USER_STATUS_TITLE)), SUBJECT_AREA,
					getProperty(ELLIPSE_TASK_USER_STATUS_TITLE), activity.getTaskUserStatusUDF(), null);

			if (isExistUDFValue(activity.getActivityObjectId(),
					getUdfTypeId(getProperty(ELLIPSE_TASK_USER_STATUS_TITLE))))
				updateUDFValues.add(udfValue);
			else
				createUdfValues.add(udfValue);
		}

		if (null != activity.getExecutionPckgUDF() && activity.getExecutionPckgUDF().isEmpty()) {
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds = new ArrayList<>();
			P6ActivityDTO excActivity = new P6ActivityDTO();
			excActivity.setActivityId(activity.getActivityId());
			excActivity.setActivityObjectId(activity.getActivityObjectId());
			excActivity.setExecutionPckgUDF(activity.getExecutionPckgUDF());
			prepareDeleteUDFValues(excActivity, objectIds);
			try {
				deleteActivityFieldsUDF(delUdfValueServiceCall, objectIds);
			} catch (P6ServiceException e) {
				logger.error("Unable to delete Execution package UDF for activity Id #{}", activity.getActivityId());
				logger.error("An error occurs while delete execution package UDF", e);
			}
		}
	}

	/**
	 * Making call to P6 Create DUF Value webservice
	 * 
	 * @param trackingId
	 * @param udfValues
	 * @throws P6ServiceException
	 */
	private void callUDFvalueService(
			final UDFValueServiceCall<List<au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId>> udfValueService,
			final List<UDFValue> udfValues) throws P6ServiceException {
		if (null != udfValues && !udfValues.isEmpty()) {
			udfValueService.createUDFValues(udfValues);
		}
	}

	/**
	 * Constructing UDFValue object
	 * 
	 * @param activity
	 * @return {@link UDFValue}
	 */
	private UDFValue createUDFValue(Integer foreignObjectId, Integer udfTypeObjectId, String udfTypeSubjectArea,
			String udfTypetitle, String textValue, Double doubleValue) {
		UDFValue udfValue = new UDFValue();
		udfValue.setForeignObjectId(foreignObjectId);
		udfValue.setUDFTypeObjectId(udfTypeObjectId);
		udfValue.setUDFTypeSubjectArea(udfTypeSubjectArea);
		udfValue.setUDFTypeTitle(udfTypetitle);
		au.com.wp.corp.p6.wsclient.udfvalue.ObjectFactory objectFactory = new au.com.wp.corp.p6.wsclient.udfvalue.ObjectFactory();
		if (textValue != null && !textValue.isEmpty())
			udfValue.setText(textValue);
		else if (doubleValue != null)
			udfValue.setDouble(objectFactory.createUDFValueDouble(doubleValue));

		return udfValue;
	}

	@Override
	public void updateActivities(List<P6ActivityDTO> activities) throws P6ServiceException {
		createOrUpdateActivities(activities, false);

	}

	/**
	 * 
	 * @param trackingId
	 * @param activityDTOs
	 * @param chunkSize
	 * @throws P6ServiceException
	 */
	private void updateActivityFieldsUDF(final UDFValueServiceCall<Boolean> delUdfValueServiceCall,
			UDFValueServiceCall<Boolean> updUdfValueService,
			UDFValueServiceCall<List<au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId>> crdUdfValueService,
			List<P6ActivityDTO> activityDTOs, final int chunkSize) throws P6ServiceException {

		final List<List<UDFValue>> updUDFList = new ArrayList<>();
		final List<List<UDFValue>> crdUDFList = new ArrayList<>();
		for (P6ActivityDTO activity : activityDTOs) {
			final List<UDFValue> updateUDFFields = new ArrayList<>();
			final List<UDFValue> createUDFFields = new ArrayList<>();
			findUDFValueForUDFType(delUdfValueServiceCall, createUDFFields, updateUDFFields, activity);
			if (!updateUDFFields.isEmpty()) {
				updUDFList.add(updateUDFFields);
			}

			if (!createUDFFields.isEmpty()) {
				crdUDFList.add(createUDFFields);
			}
		}

		int createUDFValueSize = crdUDFList.size();
		logger.debug("Number of calls to P6 UDFValue Create web service #{}", (createUDFValueSize / chunkSize) + 1);
		for (int i = 0; i < (createUDFValueSize / chunkSize) + 1; i++) {
			int startIndex = i * chunkSize;

			int endIndex = ((i + 1) * chunkSize) < createUDFValueSize ? ((i + 1) * chunkSize) : createUDFValueSize;
			logger.debug("creating udf values start index # {}  - end index # {}", startIndex, endIndex);

			List<UDFValue> crdUDFs = new ArrayList<>();
			for (List<UDFValue> crdUDF : crdUDFList.subList(startIndex, endIndex)) {
				crdUDFs.addAll(crdUDF);
			}
			if (!crdUDFs.isEmpty()) {
				crdUdfValueService.createUDFValues(crdUDFs);
			}

		}

		int udfValueSize = updUDFList.size();
		logger.debug("Number of calls to P6 UDFValue Update web service #{}", (udfValueSize / chunkSize) + 1);
		for (int i = 0; i < (udfValueSize / chunkSize) + 1; i++) {
			int startIndex = i * chunkSize;
			int endIndex = ((i + 1) * chunkSize) < udfValueSize ? ((i + 1) * chunkSize) : udfValueSize;

			logger.debug("updating udf values start index # {}  - end index # {}", startIndex, endIndex);
			List<UDFValue> updUDFs = new ArrayList<>();
			for (List<UDFValue> updUDF : updUDFList.subList(startIndex, endIndex)) {
				updUDFs.addAll(updUDF);
			}
			if (!updUDFs.isEmpty()) {
				updUdfValueService.updateUDFValues(updUDFs);
			}
		}
	}

	/**
	 * Delete activities from P6
	 * 
	 * @param activities
	 */
	@Override
	public boolean deleteActivities(List<P6ActivityDTO> activities) throws P6ServiceException {
		if (null == activities || activities.isEmpty()) {
			throw new P6ServiceException("List of activties can't be null or empty");
		}
		logger.info("Deleting activites in P6 ... number of activites # {}", activities.size());
		getAuthenticated();

		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty(NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_P6);
		logger.debug("Defined P6 delete activity webservice call trigger value # {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);
		int deleteActivitySize = activities.size();
		logger.debug("Number of calls to P6 delete activity web service #{}", (deleteActivitySize / chunkSize) + 1);
		boolean status = false;
		for (int i = 0; i < (deleteActivitySize / chunkSize) + 1; i++) {

			final List<Integer> activityIds = new ArrayList<>();
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds = new ArrayList<>();
			int startIndex = i * chunkSize;

			int endIndex = endIndex = ((i + 1) * chunkSize) < deleteActivitySize ? ((i + 1) * chunkSize)
					: deleteActivitySize;

			logger.debug("deleting activity start index # {}  - end index # {}", startIndex, endIndex);
			for (P6ActivityDTO p6ActivityDTO : activities.subList(startIndex, endIndex)) {
				activityIds.add(p6ActivityDTO.getActivityObjectId());
			}

			logger.debug("list of activities to be deleted in P6 # {}", activityIds);
			logger.debug("number of activities to be deleted in P6 # {}", activityIds.size());
			status = delActivityService.deleteActivities(activityIds);

		}

		return status;

	}

	/**
	 * Constructing ObjectIds to be deleted in P6
	 * 
	 * @param activity
	 * @param objectIds
	 */
	private void prepareDeleteUDFValues(final P6ActivityDTO activity,
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds) {

		au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId objectId;
		if (null != activity.getExecutionPckgUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_EXECUTION_PCKG_TITLE)));
			objectIds.add(objectId);
		}

	}

	/**
	 * calling Delete UDFValue service in P6
	 * 
	 * @param trackingId
	 * @param objectIds
	 * @return
	 * @throws P6ServiceException
	 */
	private Boolean deleteActivityFieldsUDF(final UDFValueServiceCall<Boolean> delUdfValueServiceCall,
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds)
			throws P6ServiceException {
		return delUdfValueServiceCall.deleteUDFValues(objectIds);

	}

	@Override
	public List<UDFTypeDTO> readUDFTypes() throws P6ServiceException {
		logger.info("Reading UDF type details from P6 ..");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated();
		StringBuilder filter = new StringBuilder();
		filter.append("SubjectArea='Activity'");

		List<UDFType> udfTypes = udfTypeServiceCall.readUDFTypes(filter.toString());
		List<UDFTypeDTO> udfTypeDTOs = new ArrayList<>();
		UDFTypeDTO udfTypeDTO;
		for (UDFType udfType : udfTypes) {
			udfTypeDTO = new UDFTypeDTO();
			udfTypeDTO.setDataType(udfType.getDataType());
			udfTypeDTO.setObjectId(udfType.getObjectId());
			udfTypeDTO.setTitle(udfType.getTitle());
			udfTypeDTOs.add(udfTypeDTO);

		}

		return udfTypeDTOs;
	}

	/**
	 * 
	 * @param udfTitle
	 * @return
	 */
	private Integer getUdfTypeId(String udfTitle) {
		Map<String, UDFTypeDTO> udfTypes = CacheManager.getP6UDFTypeMap();
		UDFTypeDTO udfTypeDTO = udfTypes.get(udfTitle);
		return udfTypeDTO != null ? udfTypeDTO.getObjectId() : null;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	private String getProperty(String key) {
		return P6ReloadablePropertiesReader.getProperty(key);
	}

	@Override
	public Map<String, Integer> readResources() throws P6ServiceException {
		logger.debug("Reading resources from P6....");

		getAuthenticated();
		
		List<Resource> reasources = resourceService.readResource(null, null);

		Map<String, Integer> p6ProjWorkgroupDTOs = new HashMap<>();
		for (Resource resource : reasources) {
			p6ProjWorkgroupDTOs.put(resource.getId(), resource.getObjectId());
		}
		return p6ProjWorkgroupDTOs;

	}
}
