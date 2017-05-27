/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.util.CacheManager;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.wsclient.constant.P6WSConstants;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.udfvalue.UDFValue;

/**
 * 
 * @author n039126
 * @version 1.0
 */
@Service
@PropertySource("file:/${properties.dir}/p6portal-wsdl.properties")
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
			activityDTO.setOriginalDuration(activity.getActualDuration().getValue());
			activityDTO.setRemainingDuration(activity.getRemainingDuration().getValue());
			activityDTO.setProjectObjectId(activity.getProjectObjectId());

			// setting all udf fileds
			List<UDFValue> _udfValueList = udfValueMap.get(activity.getObjectId());

			if ( null != _udfValueList)
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
	public void createActivities(List<P6ActivityDTO> activities) throws P6ServiceException {

		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		List<Activity> _activities = new ArrayList<>();
		Activity activity = null;
		for (P6ActivityDTO p6ActivityDTO : activities) {
			activity = new Activity();
			activity.setId(p6ActivityDTO.getActivityId());
			// activity.set
			_activities.add(activity);
		}

		final ActivityServiceCall activityService = new CreateActivityServiceCall(trackingId, activityServiceWSDL,
				_activities);
		final Holder<List<Integer>> activityIds = activityService.run();
		logger.debug("list of activities from P6 # {}", activityIds);

	}

	@Override
	public void updateActivities(List<P6ActivityDTO> activities) throws P6ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteActivities(List<P6ActivityDTO> activities) throws P6ServiceException {
		// TODO Auto-generated method stub

	}

}
