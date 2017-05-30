/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.util.CacheManager;
import au.com.wp.corp.p6.util.DateUtil;
import au.com.wp.corp.p6.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.wsclient.constant.P6WSConstants;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * 
 * @author n039126
 * @version 1.0
 */
@Service
@PropertySource("file:/${properties.dir}/p6portal.properties")
public class P6WSClientImpl implements P6WSClient, P6WSConstants {
	private static final Logger logger = LoggerFactory.getLogger(P6WSClientImpl.class);

	@Value("${P6_ACTIVITY_SERVICE_WSDL}")
	private String activityServiceWSDL;

	@Value("${P6_AUTH_SERVICE_WSDL}")
	private String authServiceWSDL;

	@Value("${P6_USER_PRINCIPAL}")
	private String userPrincipal;

	@Value("${P6_USER_CREDENTIAL}")
	private String userCredential;

	@Value("${P6_DB_INSTANCE}")
	private int p6DBInstance;

	@Value("${P6_RESOURCE_SERVICE_WSDL}")
	private String resourceServiceWSDL;

	@Value("${P6_UDF_SERVICE_WSDL}")
	private String udfServiceWSDL;

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

		final ActivityServiceCall<List<Activity>> activityService = new ReadActivityServiceCall(trackingId,
				activityServiceWSDL, null);

		final Holder<List<Activity>> activities = activityService.run();
		logger.debug("list of activities from P6 # {}", activities);

		int i = 0;
		final StringBuilder filter = new StringBuilder();
		filter.append("ForeignObjectId IN (");
		for (Activity activity : activities.value) {
			if (i > 0)
				filter.append(",");
			filter.append(activity.getObjectId());
			i++;
			if (i == 999) {
				filter.append(")");
				filter.append(OR);
				filter.append("ForeignObjectId IN (");
				logger.debug("Filter criteria length for Read Value services # {} ", i);
				i = 0;
			}
		}
		filter.append(")");

		final UDFValueServiceCall<List<UDFValue>> udfValueService = new ReadUDFValueServiceCall(trackingId,
				udfServiceWSDL, filter.toString(), null);

		final Holder<List<UDFValue>> udfValues = udfValueService.run();

		final Map<Integer, List<UDFValue>> udfValueMap = new HashMap<>();
		List<UDFValue> udfValueList = null;
		Integer foreignObjectId = null;
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
			// needs to be confirmed from Anupam
			activityDTO.setOriginalDuration(activity.getPlannedDuration());
			activityDTO.setRemainingDuration(activity.getRemainingDuration().getValue());
			activityDTO.setProjectObjectId(activity.getProjectObjectId());

			// setting all udf fileds
			List<UDFValue> _udfValueList = udfValueMap.get(activity.getObjectId());

			if (null != _udfValueList)
				for (UDFValue udfValue : _udfValueList) {
					if (udfValue.getUDFTypeObjectId() == ELLIPSE_EGI_TYP_ID) {
						activityDTO.seteGIUDF(udfValue.getText());
					} else if (udfValue.getUDFTypeObjectId() == ELLIPSE_EQUIP_CODE_TYP_ID) {
						activityDTO.setEquipmentCodeUDF(udfValue.getText());
					} else if (udfValue.getUDFTypeObjectId() == ELLIPSE_EQUIPMENT_NO_TYP_ID) {
						activityDTO.setEquipmentNoUDF(udfValue.getText());
					} else if (udfValue.getUDFTypeObjectId() == ELLIPSE_FEEEDER_TYP_ID) {
						activityDTO.setFeederUDF(udfValue.getText());
					} else if (udfValue.getUDFTypeObjectId() == ELLIPSE_PICK_ID_TYP_ID) {
						activityDTO.setPickIdUDF(udfValue.getText());
					} else if (udfValue.getUDFTypeObjectId() == ELLIPSE_REQ_BY_DATE_TYP_ID) {
						activityDTO.setRequiredByDateUDF(udfValue.getText());
					} else if (udfValue.getUDFTypeObjectId() == ELLIPSE_STD_JOB_TYP_ID) {
						activityDTO.setEllipseStandardJobUDF(udfValue.getText());
					} else if (udfValue.getUDFTypeObjectId() == ELLIPSE_TASK_USER_STATUS_TYP_ID) {
						activityDTO.setTaskUserStatusUDF(udfValue.getText());
					} else if (udfValue.getUDFTypeObjectId() == ELLIPSE_UPSTREAM_SWITCH_TYP_ID) {
						activityDTO.setUpStreamSwitchUDF(udfValue.getText());
					}
				}

			activityDTOs.add(activityDTO);
		}

		return activityDTOs;
	}

	/**
	 * @param trackingId
	 * @throws P6ServiceException
	 */
	private Boolean getAuthenticated(final RequestTrackingId trackingId) throws P6ServiceException {
		if (CacheManager.getWsHeaders().isEmpty()) {
			AuthenticationService authService = new AuthenticationService(trackingId, authServiceWSDL, userPrincipal,
					userCredential, p6DBInstance);
			Holder<Boolean> holder = authService.run();
			logger.debug("Is authentication successfull ??  {} ", holder.value);
			return holder.value;
		}

		return false;
	}

	@Override
	public void createActivities(final List<P6ActivityDTO> activities) throws P6ServiceException {
		if (null == activities || activities.isEmpty()) {
			throw new P6ServiceException("List of activties can't be null or empty");
		}
		logger.info("Creating activites in P6 ... number of activites # {}", activities.size());
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty("ACTIVITY_LIST_SIZE_TO_PROCESS_IN_P6");
		logger.debug("Defined P6 UDFValueService webservice call trigger value # {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);
		int crdActivitySize = activities.size();
		logger.debug("Number of calls to P6 UDFValue web service #{}", (crdActivitySize / chunkSize) + 1);

		for (int i = 0; i < (crdActivitySize / chunkSize) + 1; i++) {
			final Map<String, P6ActivityDTO> activityMap = new HashMap<>();

			final List<Activity> _activities = new ArrayList<>();
			Activity activity = null;
			for (P6ActivityDTO p6ActivityDTO : activities.subList(i,
					i + chunkSize <= activities.size() ? i + chunkSize : activities.size())) {
				activity = new Activity();
				activity.setId(p6ActivityDTO.getActivityId());
				activity.setName(p6ActivityDTO.getActivityName());
				final XMLGregorianCalendar plannedStartDate = dateUtil
						.convertStringToXMLGregorianClalander(p6ActivityDTO.getPlannedStartDate());
				if (null != plannedStartDate)
					activity.setPlannedStartDate(plannedStartDate);
				else
					logger.error("Invalid planned start date# {}", p6ActivityDTO.getPlannedStartDate());
				activity.setProjectObjectId(p6ActivityDTO.getProjectObjectId());
				_activities.add(activity);

				activityMap.put(p6ActivityDTO.getActivityId(), p6ActivityDTO);
			}

			final ActivityServiceCall<List<Integer>> crActivityService = new CreateActivityServiceCall(trackingId,
					activityServiceWSDL, _activities);
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
					activityServiceWSDL, filter.toString());
			final Holder<List<Activity>> newlyCrdActivites = rdActservice.run();

			for (Activity newActivity : newlyCrdActivites.value) {
				activityMap.get(newActivity.getId()).setActivityObjectId(newActivity.getObjectId());
			}

			final List<P6ActivityDTO> crdActivityFileds = new ArrayList<>();
			crdActivityFileds.addAll(activityMap.values());
			createActivityFieldsUDF(trackingId, crdActivityFileds);
		}

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
		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty("ACTIVITY_LIST_SIZE_TO_PROCESS_IN_P6");
		logger.debug("Defined P6 UDFValueService webservice call trigger value # {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);

		final List<UDFValue> udfValues = new ArrayList<>();
		for (P6ActivityDTO activity : activityDTOs) {

			if (null != activity.getActivityJDCodeUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 5938, "Activity", "Ellipse JD Code",
						activity.getActivityJDCodeUDF(), null);
				udfValues.add(udfValue);
			}

			if (null != activity.geteGIUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 5911, "Activity", "Ellipse EGI",
						activity.geteGIUDF(), null);
				udfValues.add(udfValue);
			}

			if (null != activity.getUpStreamSwitchUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}

			if (null != activity.getAddressUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}
			if (null != activity.getEllipseStandardJobUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 5932, "Activity", "Ellipse STD Job",
						activity.getEllipseStandardJobUDF(), null);
				udfValues.add(udfValue);
			}
			if (null != activity.getEquipmentCodeUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}
			if (null != activity.getEquipmentNoUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}

			if (null != activity.getEstimatedLabourHoursUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}
			if (null != activity.getFeederUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}
			if (null != activity.getLocationInStreetUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}
			if (null != activity.getPickIdUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}
			if (null != activity.getRequiredByDateUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}
			if (null != activity.getTaskDescriptionUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}
			if (null != activity.getTaskUserStatusUDF()) {
				UDFValue udfValue = createUDFValue(activity.getActivityObjectId(), 1234, "Activity", "", "", 0.0);
				udfValues.add(udfValue);
			}

		}

		int udfValueSize = udfValues.size();
		logger.debug("Number of calls to P6 UDFValue web service #{}", (udfValueSize / chunkSize) + 1);
		for (int i = 0; i < (udfValueSize / chunkSize) + 1; i++) {
			callUDFvalueService(trackingId,
					udfValues.subList(i, i + chunkSize <= udfValueSize ? i + chunkSize : udfValueSize));

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
				trackingId, udfServiceWSDL, udfValues);
		final Holder<List<au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId>> udfs = udfValueService
				.run();
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
		if (textValue != null)
			udfValue.setText(textValue);

		if (doubleValue != null)
			udfValue.setDouble(null);

		return udfValue;
	}

	@Override
	public void updateActivities(List<P6ActivityDTO> activities) throws P6ServiceException {
		
		

	}

	private void updateActivityFieldsUDF() {

	}

	@Override
	public void deleteActivities(List<P6ActivityDTO> activities) throws P6ServiceException {
		if (null == activities || activities.isEmpty()) {
			throw new P6ServiceException("List of activties can't be null or empty");
		}
		logger.info("Deleting activites in P6 ... number of activites # {}", activities.size());
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		final String chunkSizeStr = P6ReloadablePropertiesReader.getProperty("ACTIVITY_LIST_SIZE_TO_PROCESS_IN_P6");
		logger.debug("Defined P6 UDFValueService webservice call trigger value # {}", chunkSizeStr);
		final int chunkSize = Integer.parseInt(chunkSizeStr);
		int crdActivitySize = activities.size();
		logger.debug("Number of calls to P6 UDFValue web service #{}", (crdActivitySize / chunkSize) + 1);

		for (int i = 0; i < (crdActivitySize / chunkSize) + 1; i++) {
			final Map<String, P6ActivityDTO> activityMap = new HashMap<>();

			final List<Integer> activityIds = new ArrayList<>();
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds = new ArrayList<>();
			for (P6ActivityDTO p6ActivityDTO : activities.subList(i,
					i + chunkSize <= activities.size() ? i + chunkSize : activities.size())) {

				prepareDeleteUDFValues(p6ActivityDTO, objectIds);
				activityIds.add(p6ActivityDTO.getActivityObjectId());
			}

			logger.debug("list of activities to be deleted in P6 # {}", activityIds);

			Boolean deleteUDFStatus = deleteActivityFieldsUDF(trackingId, objectIds);
			logger.debug("Related UDF are deleted .....# {}", deleteUDFStatus.booleanValue() );
			if (deleteUDFStatus.booleanValue()) {
				ActivityServiceCall activityService = new DeleteActivityServiceCall(trackingId, activityServiceWSDL,
						activityIds);
				Holder<Boolean> status = activityService.run();
			}
		}

	}

	private void prepareDeleteUDFValues(final P6ActivityDTO activity,
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds) {

		au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId objectId = new ObjectId();
		if (null != activity.getActivityJDCodeUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			// objectId.setUDFTypeObjectId(activity);
			objectIds.add(objectId);
		}

		if (null != activity.geteGIUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(5911);
		}

		if (null != activity.getUpStreamSwitchUDF()) {

		}

		if (null != activity.getAddressUDF()) {
		}
		if (null != activity.getEllipseStandardJobUDF()) {
			objectId = new ObjectId();
			objectId.setForeignObjectId(activity.getActivityObjectId());
			objectId.setUDFTypeObjectId(5911);

		}
		if (null != activity.getEquipmentCodeUDF()) {

		}
		if (null != activity.getEquipmentNoUDF()) {

		}

		if (null != activity.getEstimatedLabourHoursUDF()) {

		}
		if (null != activity.getFeederUDF()) {

		}
		if (null != activity.getLocationInStreetUDF()) {

		}
		if (null != activity.getPickIdUDF()) {

		}
		if (null != activity.getRequiredByDateUDF()) {

		}
		if (null != activity.getTaskDescriptionUDF()) {

		}
		if (null != activity.getTaskUserStatusUDF()) {

		}

	}

	private Boolean deleteActivityFieldsUDF(final RequestTrackingId trackingId,
			final List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds)
			throws P6ServiceException {

		UDFValueServiceCall<Boolean> udfValueServiceCall = new DeleteUDFValueServiceCall(trackingId, udfServiceWSDL,
				objectIds);
		Holder<Boolean> status = udfValueServiceCall.run();
		return status.value;

	}

}
