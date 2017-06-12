/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.activity.ObjectFactory;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.wsclient.cleint.P6WSClient#searchWorkOrder(au.com.wp.
	 * corp.p6.model.ActivitySearchRequest)
	 */
	@Override
	public List<P6ActivityDTO> readActivities() throws P6ServiceException {
		CacheManager.getWsHeaders().remove("WS_COOKIE");
		logger.info("Calling activity service in P6 Webservice ...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		final ActivityServiceCall<List<Activity>> activityService = new ReadActivityServiceCall(trackingId, null);

		final Holder<List<Activity>> activities = activityService.run();
		logger.debug("list of activities from P6#{}", activities);

		final StringBuilder udfValueFilter = createFilters(activities, P6EllipseWSConstants.FOREIGN_OBJECT_ID);

		final Map<Integer, List<UDFValue>> udfValueMap = readUDFvalues(trackingId, udfValueFilter);
		final StringBuilder resourceAssignmentFilter = createFilters(activities,
				P6EllipseWSConstants.ACTIVITY_OBJECT_ID);
		final Map<Integer, ResourceAssignment> resourceAssignments = readResourceAssignments(trackingId,
				resourceAssignmentFilter);

		logger.debug("UDFValues from P6 # {}", udfValueMap);

		final List<P6ActivityDTO> activityDTOs = new ArrayList<>();
		P6ActivityDTO activityDTO;
		for (Activity activity : activities.value) {
			activityDTO = new P6ActivityDTO();
			activityDTO.setActivityObjectId(activity.getObjectId());
			activityDTO.setActivityId(activity.getId());
			activityDTO.setActivityName(activity.getName());
			activityDTO.setActivityStatus(activity.getStatus());
			activityDTO.setPlannedStartDate(activity.getPlannedStartDate().toString());
			activityDTO.setWorkGroup(activity.getPrimaryResourceId());
			activityDTO.setOriginalDuration(activity.getPlannedDuration());
			activityDTO.setRemainingDuration(activity.getRemainingDuration().getValue());
			activityDTO.setProjectObjectId(activity.getProjectObjectId());
			ResourceAssignment resAssign = resourceAssignments.get(activity.getObjectId());
			if (null != resAssign) {
				activityDTO.setEstimatedLabourHours(resAssign.getPlannedDuration().getValue());
				activityDTO.setEstimatedLabourHoursObjectId(resAssign.getObjectId());
			}
			// setting all udf fields
			List<UDFValue> udfValueList = udfValueMap.get(activity.getObjectId());

			if (null != udfValueList)
				for (UDFValue udfValue : udfValueList) {
					setUDFValues(activityDTO, udfValue);
				}

			activityDTOs.add(activityDTO);
		}

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
	private StringBuilder createFilters(final Holder<List<Activity>> activities, String filterParam) {
		int i = 0;
		final StringBuilder filter = new StringBuilder();
		filter.append(filterParam + " IN (");
		for (Activity activity : activities.value) {
			if (i > 0)
				filter.append(",");
			filter.append(activity.getObjectId());
			i++;
			if (i == 999) {
				filter.append(")");
				filter.append(OR);
				filter.append(filterParam + " IN (");
				logger.debug("Filter criteria length for Read Value services # {} ", i);
				i = 0;
			}
		}
		filter.append(")");
		return filter;
	}

	/**
	 * @param trackingId
	 * @param filter
	 * @return
	 * @throws P6ServiceException
	 */
	private Map<Integer, List<UDFValue>> readUDFvalues(final RequestTrackingId trackingId, final StringBuilder filter)
			throws P6ServiceException {
		final UDFValueServiceCall<List<UDFValue>> udfValueService = new ReadUDFValueServiceCall(trackingId,
				filter.toString(), null);

		final Holder<List<UDFValue>> udfValues = udfValueService.run();

		final Map<Integer, List<UDFValue>> udfValueMap = CacheManager.getUDFValueMap();
		List<UDFValue> udfValueList;
		Integer foreignObjectId;
		for (UDFValue udfValue : udfValues.value) {
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
	private Map<Integer, ResourceAssignment> readResourceAssignments(final RequestTrackingId trackingId,
			final StringBuilder filter) throws P6ServiceException {
		final ResourceAssignmentServiceCall<List<ResourceAssignment>> resourceAssignmentService = new ReadResourceAssignmentServiceCall(
				trackingId, filter.toString());

		final Holder<List<ResourceAssignment>> resourceAssignments = resourceAssignmentService.run();

		final Map<Integer, ResourceAssignment> resourceAssignmenteMap = new HashMap<>();
		Integer activityObjectId;
		for (ResourceAssignment resourceAssignment : resourceAssignments.value) {
			activityObjectId = resourceAssignment.getActivityObjectId();
			resourceAssignmenteMap.put(activityObjectId, resourceAssignment);
		}
		return resourceAssignmenteMap;
	}

	/**
	 * @param trackingId
	 * @throws P6ServiceException
	 */
	private Boolean getAuthenticated(final RequestTrackingId trackingId) throws P6ServiceException {
		if (CacheManager.getWsHeaders().isEmpty()) {
			AuthenticationService authService = new AuthenticationService(trackingId);
			Holder<Boolean> holder = authService.run();
			logger.debug("Is authentication successfull ??  {} ", holder.value);
			return holder.value;
		}

		return false;
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

		logger.info("Creating activites in P6 ... number of activites # {}", activities.size());
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty(NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_P6);
		logger.debug("Defined P6 UDFValueService webservice call trigger value# {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);
		int crdActivitySize = activities.size();
		logger.debug("Number of calls to P6 UDFValue web service # {}", (crdActivitySize / chunkSize) + 1);

		for (int i = 0; i < (crdActivitySize / chunkSize) + 1; i++) {
			final Map<String, P6ActivityDTO> activityMap = new HashMap<>();

			final List<Activity> crtdActivities = constructActivities(activities, chunkSize, i, activityMap);

			if (isCreateActivities) {
				final ActivityServiceCall<List<Integer>> crActivityService = new CreateActivityServiceCall(trackingId,
						crtdActivities);
				final Holder<List<Integer>> activityIds = crActivityService.run();
				logger.debug("list of activities from P6 # {}", activityIds);
				int j = 0;
				final StringBuilder filter = new StringBuilder();
				filter.append("ObjectId IN (");
				for (Integer activityObjectId : activityIds.value) {
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

				final ActivityServiceCall<List<Activity>> rdActservice = new ReadActivityServiceCall(trackingId,
						filter.toString());
				final Holder<List<Activity>> newlyCrdActivites = rdActservice.run();

				for (Activity newActivity : newlyCrdActivites.value) {
					activityMap.get(newActivity.getId()).setActivityObjectId(newActivity.getObjectId());
				}

				final List<P6ActivityDTO> crdActivityFileds = new ArrayList<>();
				crdActivityFileds.addAll(activityMap.values());
				createActivityFieldsUDF(trackingId, crdActivityFileds);
			} else {
				final ActivityServiceCall<Boolean> crActivityService = new UpdateActivityServiceCall(trackingId,
						crtdActivities);
				final Holder<Boolean> status = crActivityService.run();
				logger.debug("list of activities from P6 # {}", status.value);
				updateActivityFieldsUDF(trackingId, activities, chunkSize);

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
		int endIndex = ((i + 1) * chunkSize - 1) < activities.size() ? ((i + 1) * chunkSize - 1) : activities.size();

		logger.debug("constructing activity start index # {}  - end index # {}", startIndex, endIndex);

		for (P6ActivityDTO p6ActivityDTO : activities.subList(startIndex, endIndex)) {
			activity = new Activity();
			activity.setId(p6ActivityDTO.getActivityId());
			activity.setName(p6ActivityDTO.getActivityName());

			if (null != p6ActivityDTO.getActivityStatus() && !p6ActivityDTO.getActivityStatus().trim().isEmpty())
				activity.setStatus(p6ActivityDTO.getActivityStatus());

			final XMLGregorianCalendar plannedStartDate = dateUtil
					.convertStringToXMLGregorianClalander(p6ActivityDTO.getPlannedStartDate());
			if (null != plannedStartDate)
				activity.setPlannedStartDate(plannedStartDate);
			else
				logger.error("Invalid planned start date# {}", p6ActivityDTO.getPlannedStartDate());

			if (null != p6ActivityDTO.getActualFinishDate()) {
				final XMLGregorianCalendar actualStartDate = dateUtil
						.convertStringToXMLGregorianClalander(p6ActivityDTO.getActualStartDate());
				if (null != actualStartDate) {
					activity.setActualStartDate(objectFactory.createActivityActualStartDate(actualStartDate));
				}
				final XMLGregorianCalendar actualFinishDate = dateUtil
						.convertStringToXMLGregorianClalander(p6ActivityDTO.getActualFinishDate());
				if (null != actualFinishDate) {
					activity.setActualFinishDate(objectFactory.createActivityActualStartDate(actualFinishDate));
				}
			}

			activity.setProjectObjectId(p6ActivityDTO.getProjectObjectId());
			logger.debug("p6ActivityDTO.getActivityObjectId() #{} ", p6ActivityDTO.getActivityObjectId());
			if (p6ActivityDTO.getActivityObjectId() != null)
				activity.setObjectId(p6ActivityDTO.getActivityObjectId());

			logger.debug("PrimaryResorceObjectId # {} ", p6ActivityDTO.getPrimaryResorceObjectId());
			if (p6ActivityDTO.getPrimaryResorceObjectId() != 0)
				activity.setPrimaryResourceObjectId(
						objectFactory.createActivityPrimaryResourceObjectId(p6ActivityDTO.getPrimaryResorceObjectId()));
			activity.setPrimaryResourceId(p6ActivityDTO.getWorkGroup());

			if (p6ActivityDTO.getOriginalDuration() > 0)
				activity.setPlannedDuration(p6ActivityDTO.getOriginalDuration());

			if (p6ActivityDTO.getRemainingDuration() > 0) {
				activity.setRemainingDuration(
						objectFactory.createActivityRemainingDuration(p6ActivityDTO.getRemainingDuration()));
			}
			if (p6ActivityDTO.getEstimatedLabourHours() > 0) {
				activity.setPlannedLaborUnits(p6ActivityDTO.getEstimatedLabourHours());
			}
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
	private void createActivityFieldsUDF(final RequestTrackingId trackingId, List<P6ActivityDTO> activityDTOs)
			throws P6ServiceException {
		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty(NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_P6);
		logger.debug("Defined P6 UDFValueService webservice call trigger value # {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);

		final List<UDFValue> udfValues = new ArrayList<>();
		for (P6ActivityDTO activity : activityDTOs) {
			findUDFValueForUDFType(udfValues, null, activity);
		}

		int udfValueSize = udfValues.size();
		logger.debug("Number of calls to P6 UDFValue web service #{}", (udfValueSize / chunkSize) + 1);

		for (int i = 0; i < (udfValueSize / chunkSize) + 1; i++) {
			int startIndex = i * chunkSize;
			int endIndex = ((i + 1) * chunkSize - 1) < udfValues.size() ? ((i + 1) * chunkSize - 1) : udfValues.size();

			logger.debug("constructing activity start index # {}  - end index # {}", startIndex, endIndex);
			callUDFvalueService(trackingId, udfValues.subList(startIndex, endIndex));

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
	private void findUDFValueForUDFType(final List<UDFValue> createUdfValues, final List<UDFValue> updateUDFValues,
			P6ActivityDTO activity) {

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
				deleteActivityFieldsUDF(new RequestTrackingId(), objectIds);
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
	private void callUDFvalueService(final RequestTrackingId trackingId, final List<UDFValue> udfValues)
			throws P6ServiceException {
		final UDFValueServiceCall<List<au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId>> udfValueService = new CreateUDFValueServiceCall(
				trackingId, udfValues);
		udfValueService.run();
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
	private void updateActivityFieldsUDF(final RequestTrackingId trackingId, List<P6ActivityDTO> activityDTOs,
			final int chunkSize) throws P6ServiceException {

		final List<UDFValue> updateUDFFields = new ArrayList<>();
		final List<UDFValue> createUDFFields = new ArrayList<>();
		for (P6ActivityDTO activity : activityDTOs) {
			findUDFValueForUDFType(createUDFFields, updateUDFFields, activity);
		}

		int createUDFValueSize = createUDFFields.size();
		logger.debug("Number of calls to P6 UDFValue Create web service #{}", (createUDFValueSize / chunkSize) + 1);
		for (int i = 0; i < (createUDFValueSize / chunkSize) + 1; i++) {
			int startIndex = i * chunkSize;
			int endIndex = ((i + 1) * chunkSize - 1) < createUDFValueSize ? ((i + 1) * chunkSize - 1)
					: createUDFValueSize;

			logger.debug("creating udf values start index # {}  - end index # {}", startIndex, endIndex);
			UDFValueServiceCall<List<au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId>> udfValueService = new CreateUDFValueServiceCall(
					trackingId, createUDFFields.subList(startIndex, endIndex));
			udfValueService.run();

		}

		int udfValueSize = updateUDFFields.size();
		logger.debug("Number of calls to P6 UDFValue Update web service #{}", (udfValueSize / chunkSize) + 1);
		for (int i = 0; i < (udfValueSize / chunkSize) + 1; i++) {
			int startIndex = i * chunkSize;
			int endIndex = ((i + 1) * chunkSize - 1) < udfValueSize ? ((i + 1) * chunkSize - 1) : udfValueSize;

			logger.debug("updating udf values start index # {}  - end index # {}", startIndex, endIndex);

			UDFValueServiceCall<Boolean> udfValueService = new UpdateUDFValueServiceCall(trackingId,
					updateUDFFields.subList(startIndex, endIndex));
			udfValueService.run();

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
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty(NO_ACTVTY_TO_BE_PRCSSD_ATATIME_IN_P6);
		logger.debug("Defined P6 UDFValueService webservice call trigger value # {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);
		int deleteActivitySize = activities.size();
		logger.debug("Number of calls to P6 UDFValue web service #{}", (deleteActivitySize / chunkSize) + 1);
		Holder<Boolean> status = null;
		for (int i = 0; i < (deleteActivitySize / chunkSize) + 1; i++) {

			final List<Integer> activityIds = new ArrayList<>();
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds = new ArrayList<>();
			int startIndex = i * chunkSize;
			int endIndex = ((i + 1) * chunkSize - 1) < deleteActivitySize ? ((i + 1) * chunkSize - 1)
					: deleteActivitySize;

			logger.debug("deleting activity start index # {}  - end index # {}", startIndex, endIndex);
			for (P6ActivityDTO p6ActivityDTO : activities.subList(startIndex, endIndex)) {

				prepareDeleteUDFValues(p6ActivityDTO, objectIds);
				activityIds.add(p6ActivityDTO.getActivityObjectId());
			}

			logger.debug("list of activities to be deleted in P6 # {}", activityIds);

			Boolean deleteUDFStatus = deleteActivityFieldsUDF(trackingId, objectIds);
			logger.debug("Related UDF are deleted .....# {}", deleteUDFStatus);
			if (deleteUDFStatus.booleanValue()) {
				ActivityServiceCall<Boolean> activityService = new DeleteActivityServiceCall(trackingId, activityIds);
				status = activityService.run();
			}

		}

		return null != status ? status.value.booleanValue() : Boolean.FALSE;

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
		if (null != activity.getActivityJDCodeUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_JD_CODE_TITLE)));
			objectIds.add(objectId);
		}

		if (null != activity.geteGIUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_EGI_TITLE)));
			objectIds.add(objectId);
		}

		if (null != activity.getUpStreamSwitchUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_UPSTREAM_SWITCH_TITLE)));
			objectIds.add(objectId);
		}

		if (null != activity.getAddressUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_ADDRESS_TITLE)));
			objectIds.add(objectId);
		}
		if (null != activity.getEllipseStandardJobUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_STD_JOB_TITLE)));
			objectIds.add(objectId);

		}
		if (null != activity.getEquipmentCodeUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_EQUIP_CODE_TITLE)));
			objectIds.add(objectId);

		}
		if (null != activity.getEquipmentNoUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_EQUIPMENT_NO_TITLE)));
			objectIds.add(objectId);

		}

		if (null != activity.getFeederUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_FEEEDER_TITLE)));
			objectIds.add(objectId);

		}
		if (null != activity.getLocationInStreetUDF()) {
			objectId = new ObjectId();
			objectIds.add(objectId);

		}
		if (null != activity.getPickIdUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_PICK_ID_TITLE)));
			objectIds.add(objectId);

		}
		if (null != activity.getRequiredByDateUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_REQ_BY_DATE_TITLE)));
			objectIds.add(objectId);

		}
		if (null != activity.getTaskDescriptionUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_TASK_DESC_TITLE)));
			objectIds.add(objectId);
		}
		if (null != activity.getTaskUserStatusUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(getUdfTypeId(getProperty(ELLIPSE_TASK_USER_STATUS_TITLE)));
			objectIds.add(objectId);
		}
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
	private Boolean deleteActivityFieldsUDF(final RequestTrackingId trackingId,
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds)
			throws P6ServiceException {

		UDFValueServiceCall<Boolean> udfValueServiceCall = new DeleteUDFValueServiceCall(trackingId, objectIds);
		Holder<Boolean> status = udfValueServiceCall.run();
		return status.value;

	}

	@Override
	public List<UDFTypeDTO> readUDFTypes() throws P6ServiceException {
		logger.info("Reading UDF type details from P6 ..");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		StringBuilder filter = new StringBuilder();
		filter.append("SubjectArea='Activity'");

		UDFTypeServiceCall udfTypeServiceCall = new UDFTypeServiceCall(trackingId, filter.toString());
		Holder<List<UDFType>> udfTypes = udfTypeServiceCall.run();
		List<UDFTypeDTO> udfTypeDTOs = new ArrayList<>();
		UDFTypeDTO udfTypeDTO;
		for (UDFType udfType : udfTypes.value) {
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
		logger.debug(" size of UDF Type map # {}", udfTypes.size());
		logger.debug("UDF Type tile from create or update # {}", udfTitle);
		UDFTypeDTO udfTypeDTO = udfTypes.get(udfTitle);
		logger.debug("udf type #{}", udfTypeDTO);

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

		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		ResourceService resourceService = new ResourceService(trackingId, null, null);
		Holder<List<Resource>> holders = resourceService.run();

		Map<String, Integer> p6ProjWorkgroupDTOs = new HashMap<>();
		for (Resource resource : holders.value) {
			p6ProjWorkgroupDTOs.put(resource.getId(), resource.getObjectId());
		}
		return p6ProjWorkgroupDTOs;

	}
}
