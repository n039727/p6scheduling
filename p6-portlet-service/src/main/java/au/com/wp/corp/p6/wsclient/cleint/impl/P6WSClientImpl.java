/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.ws.Holder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.dto.ActivitySearchRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.utils.DateUtils;
import au.com.wp.corp.p6.utils.P6Constant;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.udftype.UDFType;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId;

/**
 * 
 * @author n039126
 * @version 1.0
 */
@Service
@PropertySource("file:/${properties.dir}/p6portal.properties")
public class P6WSClientImpl implements P6WSClient, P6Constant {
	private static final Logger logger = LoggerFactory.getLogger(P6WSClientImpl.class);

	@Value("${P6_ACTIVITY_SERVICE_WSDL}")
	private String activityServiceWSDL;

	@Value("${P6_AUTH_SERVICE_WSDL}")
	private String authServiceWSDL;

	@Value("${P6_REAUTH_INTERVAL}")
	private String intervalReAuthenticateP6;

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
	
	@Value("${P6_EXECUTION_PCKG_TITLE}")
	private String executionPckgeTitle;
	
	@Value("${P6_SUBJECT_AREA}")
	private String subjectArea;
	
	@Autowired
	DateUtils dateUtils;
	
	private Map<String, Integer> workOrderIdMap = new HashMap<String, Integer>();

	@Override
	public Map<String, Integer> getWorkOrderIdMap() {
		return workOrderIdMap;
	}





	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.wsclient.cleint.P6WSClient#searchWorkOrder(au.com.wp.
	 * corp.p6.model.ActivitySearchRequest)
	 */
	@Override
	public List<WorkOrder> searchWorkOrder(ActivitySearchRequest searchRequest) throws P6ServiceException {
		long starttime = System.currentTimeMillis();
		logger.info("Calling activity service in P6 Webservice ...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		if (null == searchRequest) {
			throw new P6ServiceException("NO_SEARCH_CRITERIA_FOUND");
		}

		final StringBuilder filter = new StringBuilder();
		if (null != searchRequest.getWorkOrder() && !searchRequest.getWorkOrder().trim().isEmpty()) {
			filter.append("(Id = ");
			filter.append("'" + searchRequest.getWorkOrder() + "')");
		} else {
			if (null != searchRequest.getCrewList()) {
				int i = 0;
				if(searchRequest.getCrewList().size() > 0){
					filter.append("PrimaryResourceId IN ");
					filter.append("(");
					for (String crew : searchRequest.getCrewList()) {
						if(i== 0){
							filter.append("'" + crew + "'");
						}else{
							filter.append(",'" + crew + "'");
						}
						i++;
					}
					filter.append(")");
				}
			}

			if (null != searchRequest.getPlannedStartDate()) {
				if (filter.length() > 0)
					filter.append(AND);
				filter.append("PlannedStartDate BETWEEN TO_DATE('");
				filter.append(
						searchRequest.getPlannedStartDate() + " 00:00:00', 'yyyy-mm-dd hh24:mi:ss') AND TO_DATE('");
				filter.append((searchRequest.getPlannedEndDate() != null ? searchRequest.getPlannedEndDate() :  searchRequest.getPlannedStartDate()));  
				filter.append(" 23:59:59', 'yyyy-mm-dd hh24:mi:ss')");
				
			}
			filter.append(" order by PlannedStartDate,Id");
		}
		logger.debug("filter criteria for search # {} ", filter.toString());

		final ActivityServiceCall activityService = new ActivityServiceCall(trackingId, activityServiceWSDL,
				filter.length() > 0 ? filter.toString() : null);

		final Holder<List<Activity>> activities = activityService.run();
		final List<WorkOrder> workOrders = new ArrayList<>();
		logger.debug("list of activities from P6 # {}", activities);
		if (null != activities) {
			logger.debug("size of activity list from P6 # {}", activities.value.size());
			for (Activity activity : activities.value) {
				WorkOrder workOrder = new WorkOrder();
				workOrder.setWorkOrderId(activity.getId());
				workOrder.setCrewNames(activity.getPrimaryResourceId());
				if (!StringUtils.isEmpty(activity.getPrimaryResourceId())) {
					workOrder.getCrewAssigned().add(activity.getPrimaryResourceId());
				}
				workOrder.setScheduleDate(dateUtils.convertDateDDMMYYYY(activity.getPlannedStartDate().toString()));
				List<String> wos = new ArrayList<>();
				wos.add(activity.getId());
				workOrderIdMap.put(activity.getId(),activity.getObjectId());
				workOrder.setWorkOrders(wos);
				workOrders.add(workOrder);
			}
				
			}
		logger.debug("Total time taken to execute method search work order via ActivityService {}",System.currentTimeMillis() - starttime);
		logoutFromP6(trackingId);
		return workOrders;
	}
	
	
	private int readUDFTypeForExecutionPackage() throws P6ServiceException {
		logger.info("Reading UDF type details from P6 ..");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		StringBuilder filter = new StringBuilder();
		filter.append("SubjectArea='"+ subjectArea +"' and Title = '"+ executionPckgeTitle +"'");

		UDFTypeServiceCall udfTypeServiceCall = new UDFTypeServiceCall(trackingId, filter.toString());
		Holder<List<UDFType>> udfTypes = udfTypeServiceCall.run();
		logoutFromP6(trackingId);
		return udfTypes.value.get(0).getObjectId();
	}
	
	
	/**
	 * @param trackingId
	 * @throws P6ServiceException
	 */
	private Boolean getAuthenticated(final RequestTrackingId trackingId) throws P6ServiceException {
			AuthenticationService authService = new AuthenticationService(trackingId, authServiceWSDL, userPrincipal,
					userCredential, p6DBInstance);
			Holder<Boolean> holder = authService.run();
			logger.debug("Is authentication successfull ??  {} ", holder.value);
			if (holder.value)
				CacheManager.getWSLoginTimestamp().put(WS_AUTH_SERVICE_CALL_TIME, System.currentTimeMillis());
			return holder.value;
	}

	@Override
	public ExecutionPackageDTO createExecutionPackage(List<ExecutionPackageCreateRequest> request)
			throws P6ServiceException {
		logger.info("Calling udfvalue  service in P6 Webservice to create executionpackage...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		if (null == request) {
			throw new P6ServiceException("NO_SEARCH_CRITERIA_FOUND");
		}
		ExecutionPackageDTO dto = null;
		List<Integer> foreignIds = new ArrayList<>();
		for (ExecutionPackageCreateRequest executionPackageCreateRequest : request) {
			Integer foregnObjId = executionPackageCreateRequest.getForeignObjectId();
			foreignIds.add(foregnObjId);
		}
		
		 removeExecutionPackage(foreignIds);
		logger.debug("deleted for request package name {} ", request.get(0).getText());
		final UDFValueServiceCall<List<ObjectId>> createUdfservice = new CreateUDFValueServiceCall(trackingId,
				udfServiceWSDL, request);
		logger.debug("creating for request package name {}", request.get(0).getText());
		final Holder<List<ObjectId>> objectIds = createUdfservice.run();
		List<ObjectId> objectIdList = objectIds.value;
		List<WorkOrder> workOrders = new ArrayList<WorkOrder>();;
		dto = new ExecutionPackageDTO();
		dto.setExctnPckgName(request.get(0).getText());
		if (objectIdList != null) {
			for (ObjectId objectId : objectIdList) {
				WorkOrder workOrder = new WorkOrder();
				if (workOrderIdMap.containsValue(objectId.getForeignObjectId())) {
					Set<Entry<String, Integer>> entrySet = workOrderIdMap.entrySet();
					for (Entry<String, Integer> entry : entrySet) {
						if (entry.getValue() == objectId.getForeignObjectId()) {
							workOrder.setWorkOrderId(entry.getKey());
							break;
						}
					}
				}

				workOrders.add(workOrder);
			}
			dto.setWorkOrders(workOrders);
		}
		logoutFromP6(trackingId);
		return dto;
	}



	@Override
	public Boolean removeExecutionPackage(List<Integer> foreignObjIds)
			throws P6ServiceException {
		logger.info("Calling udfvalue service in P6 Webservice to de link execution package...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		if (null == foreignObjIds) {
			throw new P6ServiceException("NO_SEARCH_CRITERIA_FOUND");
		}
		int udfObjectId = readUDFTypeForExecutionPackage();
		List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds = new ArrayList<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId>();
		List<String>  workOrderIdsNotFound = workOrderIdMap.entrySet().stream()
                .filter(map -> 0 == map.getValue())
                .map(map -> map.getKey())
                .collect(Collectors.toList());
		logger.info("workOrderIds not found {}",workOrderIdsNotFound);
		if (workOrderIdsNotFound != null && workOrderIdsNotFound.size() > 0) {
			StringBuilder filter = new StringBuilder();
			filter.append("Id in(");
			StringBuilder filterWithIn = new StringBuilder();
			for (String string : workOrderIdsNotFound) {

				if (filterWithIn.length() > 0) {
					filterWithIn.append(",'" + string + "'");
				} else {
					filterWithIn.append("'" + string + "'");
				}
			}
			filter.append(filterWithIn);
			filter.append(")");
			logger.info("filter {}", filter);
			final ActivityServiceCall activityService = new ActivityServiceCall(trackingId, activityServiceWSDL,
					filter.length() > 0 ? filter.toString() : null);

			final Holder<List<Activity>> activities = activityService.run();
			logger.debug("list of activities from P6 # {}", activities);
			if (null != activities) {
				logger.debug("size of activity list from P6 # {}", activities.value.size());
				for (Activity activity : activities.value) {
					workOrderIdMap.put(activity.getId(), activity.getObjectId());
					foreignObjIds.add(activity.getObjectId());
				}

			}

		}
		if (foreignObjIds.contains(null)) {
			for (Iterator<Integer> iterator = foreignObjIds.iterator(); iterator.hasNext();) {
				Integer objId = (Integer) iterator.next();
				if (objId == null) {
					iterator.remove();
				}

			}
		}
		for (Integer workOrderId : foreignObjIds) {
			au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId deletedObjId = new au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId();
			deletedObjId.setForeignObjectId(workOrderId);
			deletedObjId.setUDFTypeObjectId(udfObjectId);
			objectIds.add(deletedObjId);
		}
		logger.debug("request {}",objectIds);
		final UDFValueServiceCall<Boolean> deleteUdfservice = new DeleteUDFValueServiceCall(trackingId, udfServiceWSDL, objectIds);
		final Holder<Boolean> isDeleted = deleteUdfservice.run();
		logoutFromP6(trackingId);
		return isDeleted.value;
	}
	@Override
	public boolean logoutFromP6(RequestTrackingId trackingId) {
		boolean status = false;
		if (null != CacheManager.getWsHeaders().get(WS_COOKIE)) {
			LogoutServiceCall authService = new LogoutServiceCall(trackingId);
			try {
				status = authService.run().value.isReturn();
			} catch (P6ServiceException e) {
				logger.error("Error occurs during logout - ", e);
			}
			logger.debug("Is logout successfull ??  {} ", status);
		}

		return status;

	}
}
