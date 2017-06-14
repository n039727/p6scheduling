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
import au.com.wp.corp.p6.dto.Crew;
import au.com.wp.corp.p6.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.ResourceSearchRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.utils.DateUtils;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.resource.Resource;
import au.com.wp.corp.p6.wsclient.udftype.UDFType;
import au.com.wp.corp.p6.wsclient.udfvalue.CreateUDFValuesResponse.ObjectId;

/**
 * 
 * @author n039126
 * @version 1.0
 */
@Service
@PropertySource("file:/${properties.dir}/p6portal.properties")
public class P6WSClientImpl implements P6WSClient {
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
		return workOrders;
	}
	
	
	private int readUDFTypeForExecutionPackage() throws P6ServiceException {
		logger.info("Reading UDF type details from P6 ..");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		StringBuilder filter = new StringBuilder();
		filter.append("SubjectArea='Activity' and Title = 'Execution Grouping'");

		UDFTypeServiceCall udfTypeServiceCall = new UDFTypeServiceCall(trackingId, filter.toString());
		Holder<List<UDFType>> udfTypes = udfTypeServiceCall.run();
		return udfTypes.value.get(0).getObjectId();
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.wsclient.cleint.P6WSClient#searchCrew(au.com.wp.corp.p6
	 * .dto.ResourceSearchRequest)
	 */
	@Override
	public List<Crew> searchCrew(ResourceSearchRequest searchRequest) throws P6ServiceException {
		logger.info("Calling resource service in P6 Webservice ...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		if (null == searchRequest) {
			throw new P6ServiceException("NO_SEARCH_CRITERIA_FOUND");
		}

		final StringBuilder filter = new StringBuilder();
		if (null != searchRequest.getResourceType()) {

			filter.append("ResourceType = ");
			filter.append("'" + searchRequest.getResourceType() + "'");

		}

		logger.debug("filter criteria for crew search # {} ", filter.toString());

		final ResourceService resourceService = new ResourceService(trackingId, resourceServiceWSDL,
				filter.length() > 0 ? filter.toString() : null, null);

		final Holder<List<Resource>> resources = resourceService.run();
		final List<Crew> crews = new ArrayList<Crew>();
		logger.debug("list of crew from P6 # {}", crews);
		if (null != crews) {
			logger.debug("size of cres list from P6 # {}", crews.size());
			for (Resource resource : resources.value) {
				Crew crew = new Crew();
				crew.setCrewId(resource.getId());
				crew.setCrewName(resource.getName());
				crews.add(crew);
			}
		}

		return crews;
	}

	/**
	 * @param trackingId
	 * @throws P6ServiceException
	 */
	private Boolean getAuthenticated(final RequestTrackingId trackingId) throws P6ServiceException {
		long wsAuthCallTimestamp = CacheManager.getWSLoginTimestamp().get(WS_AUTH_SERVICE_CALL_TIME) != null
				? (Long) CacheManager.getWSLoginTimestamp().get(WS_AUTH_SERVICE_CALL_TIME) : 0;
		if (CacheManager.getWsHeaders().isEmpty()
				|| System.currentTimeMillis() - wsAuthCallTimestamp > 2 * 60 * 60 * 1000) {
			AuthenticationService authService = new AuthenticationService(trackingId, authServiceWSDL, userPrincipal,
					userCredential, p6DBInstance);
			Holder<Boolean> holder = authService.run();
			logger.debug("Is authentication successfull ??  {} ", holder.value);
			if (holder.value)
				CacheManager.getWSLoginTimestamp().put(WS_AUTH_SERVICE_CALL_TIME, System.currentTimeMillis());
			return holder.value;
		}

		return false;
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
			for (String string : workOrderIdsNotFound) {

				if (filter.length() > 0) {
					filter.append(",'" + string + "'");
				} else {
					filter.append("'" + string + "'");
				}
			}
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
		
		return isDeleted.value;
	}
}
