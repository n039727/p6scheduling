/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.ws.Holder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.integration.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.integration.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.integration.dto.WorkOrder;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtils;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.integration.wsclient.constant.P6EllipseWSConstants;
import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.udftype.UDFType;
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
	DateUtils dateUtil;

	@Value("${P6_UDF_SERVICE_WSDL}")
	private String udfServiceWSDL;
	
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
	public List<WorkOrder> readActivities(final List<String> taskIds) throws P6ServiceException {
		CacheManager.getWsHeaders().remove("WS_COOKIE");
		logger.info("Calling activity service in P6 Webservice ...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		final StringBuilder readActivitiesFilter = createFilters(taskIds, P6EllipseWSConstants.ID);

		final ActivityServiceCall<List<Activity>> activityService = new ReadActivityServiceCall(trackingId,
				readActivitiesFilter.toString().isEmpty() ? null : readActivitiesFilter.toString());

		final Holder<List<Activity>> activities = activityService.run();
		logger.debug("list of activities from P6#{}", activities);

		final List<WorkOrder> workOrders = new ArrayList<>();
		if (null != activities) {
			logger.debug("size of activity list from P6 # {}", activities.value.size());
			for (Activity activity : activities.value) {
				WorkOrder workOrder = new WorkOrder();
				workOrder.setWorkOrderId(activity.getId());
				workOrder.setCrewNames(activity.getPrimaryResourceId());
				if (!StringUtils.isEmpty(activity.getPrimaryResourceId())) {
					workOrder.getCrewAssigned().add(activity.getPrimaryResourceId());
				}
				workOrder.setScheduleDate(dateUtil.convertDateDDMMYYYY(activity.getPlannedStartDate().toString()));
				List<String> wos = new ArrayList<>();
				wos.add(activity.getId());
				workOrderIdMap.put(activity.getId(), activity.getObjectId());
				workOrder.setWorkOrders(wos);
				workOrders.add(workOrder);
			}

			List<ExecutionPackageDTO> dtoList = readExecutionPackage(trackingId);

			if (dtoList != null) {
				for (ExecutionPackageDTO executionPackageDTO : dtoList) {
					if (executionPackageDTO != null && executionPackageDTO.getWorkOrders().size() > 0) {
						for (Iterator<WorkOrder> iterator = executionPackageDTO.getWorkOrders().iterator(); iterator
								.hasNext();) {
							WorkOrder woInExecPkg = iterator.next();
							Stream<WorkOrder> woStreams = workOrders.stream()
									.filter(wo -> woInExecPkg.getWorkOrderId().equals(wo.getWorkOrderId()));
							woStreams.forEach(wo -> {
								wo.setExctnPckgName(executionPackageDTO.getExctnPckgName());
							});
						}
					}
				}

			}
		}

		return workOrders;
	}

	// chops a list into non-view sublists of length L
	private static <T> List<List<T>> chopped(List<T> list, final int L) {
		List<List<T>> parts = new ArrayList<List<T>>();
		final int N = list.size();
		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));
		}
		return parts;
	}

	/**
	 * Read execution package name from p6 for multiple work orders
	 * based on their foreign object Id as passed in parameter.
	 * Invokes the UDF service for all the list of work order foreign
	 * object id to get their execution package name.
	 * @param workOrderIds
	 * @param trackingId
	 * @return
	 * @throws P6ServiceException
	 */
	private List<ExecutionPackageDTO> readExecutionPackage( RequestTrackingId trackingId)
			throws P6ServiceException {
		final StringBuilder filter = new StringBuilder();
		filter.append("UDFTypeSubjectArea= ");
		filter.append("'" + P6EllipseWSConstants.SUBJECT_AREA + "'");
		List<ExecutionPackageDTO> dtoList = new ArrayList<ExecutionPackageDTO>();

		filter.append(AND);
		if (workOrderIdMap != null && workOrderIdMap.size() > 0) {
			if (workOrderIdMap.size() == 1) {
				filter.append(P6EllipseWSConstants.FOREIGN_OBJECT_ID +" = ");
				filter.append(workOrderIdMap.get(0));
			} else {
				List<List<Integer>> workOrderParts = chopped(new ArrayList<Integer>(workOrderIdMap.values()), 10);
				if (workOrderParts != null) {
					if (workOrderParts.size() > 1) {
						filter.append("(");
					}
					int i = 0;
					for (List<Integer> workOrderIdsList : workOrderParts) {
						if (i > 0) {
							filter.append(OR);
						}
						filter.append(P6EllipseWSConstants.FOREIGN_OBJECT_ID +" IN (");
						String inClause = StringUtils
								.join(workOrderIdsList.toArray(new Integer[workOrderIdsList.size()]), ",");
						filter.append(inClause);
						filter.append(")");
						i++;
					}
					if (workOrderParts.size() > 1) {
						filter.append(")");
					}

				}

			}
			filter.append(AND);
			filter.append("UDFTypeTitle = ");
			filter.append("'" + P6EllipseWSConstants.EXECUTION_GROUPING + "'");

		}
		logger.debug("filter criteria for search # {} ", filter.toString());
		logger.debug("calling read UDF Values");
		Map<String, List<Integer>> udfValueMap = readUDFvalues(trackingId, filter);


		if (udfValueMap != null) {
			logger.debug("returned from search {}", udfValueMap.values());
			Set<Entry<String,List<Integer>>> udfValueEntries = udfValueMap.entrySet();
			udfValueEntries.forEach(entry -> {
				logger.debug("Key : " + entry.getKey() + " Value : " + entry.getValue());
				ExecutionPackageDTO dto = new ExecutionPackageDTO();
				String executionPackageName = entry.getKey();
				List<Integer> foreignObjIds= entry.getValue();
				logger.debug("Returned Package name from P6 {}", executionPackageName);
				List<WorkOrder> workOrders = new ArrayList<WorkOrder>();

				dto.setExctnPckgName(executionPackageName);
				foreignObjIds.forEach(foreignId ->{
					WorkOrder workOrder = new WorkOrder();
					String workOrderId = workOrderIdMap.entrySet().stream().filter(
							map->foreignId.equals(map.getValue())).
							map(map->map.getKey())
							.collect(Collectors.joining());
					workOrder.setWorkOrderId(workOrderId);
					workOrders.add(workOrder);
				});
				dto.setWorkOrders(workOrders);
				dtoList.add(dto);
			});

		}

		return dtoList;
	}

	/**
	 * @param activities
	 * @return
	 */
	private StringBuilder createFilters(final List<String> ids, String filterParam) {
		int i = 0;
		final StringBuilder filter = new StringBuilder();
		filter.append(filterParam + " IN ('");
		for (String id : ids) {
			if (i > 0)
				filter.append("','");
			filter.append(id);
			i++;
			if (i == 999) {
				filter.append("')");
				filter.append(OR);
				filter.append(filterParam + " IN ('");
				logger.debug("Filter criteria length for Read Value services # {} ", i);
				i = 0;
			}
		}
		filter.append("')");
		return filter;
	}

	/**
	 * @param trackingId
	 * @param filter
	 * @return
	 * @throws P6ServiceException
	 */
	private Map<String, List<Integer>> readUDFvalues(final RequestTrackingId trackingId, final StringBuilder filter)
			throws P6ServiceException {
		final UDFValueServiceCall<List<UDFValue>> udfValueService = new ReadUDFValueServiceCall(trackingId,
				filter.toString(), null);

		final Holder<List<UDFValue>> udfValues = udfValueService.run();

		final Map<String, List<Integer>> udfValueMap = CacheManager.getUDFValueMap();
		List<Integer> udfValueList;
		Integer foreignObjectId;
		for (UDFValue udfValue : udfValues.value) {
			foreignObjectId = udfValue.getForeignObjectId();
			String text = udfValue.getText();
			if (null == udfValueMap.get(text)) {
				udfValueList = new ArrayList<>();
				udfValueMap.put(text, udfValueList);
			} else {
				udfValueList = udfValueMap.get(text);
			}
			udfValueList.add(foreignObjectId);
		}
		return udfValueMap;
	}


	/**
	 * @param trackingId
	 * @throws P6ServiceException
	 */
	private Boolean getAuthenticated(final RequestTrackingId trackingId) throws P6ServiceException {
		if (CacheManager.getWsHeaders().isEmpty() || null == CacheManager.getWsHeaders().get(WS_COOKIE)) {
			AuthenticationService authService = new AuthenticationService(trackingId);
			Holder<Boolean> holder = authService.run();
			logger.debug("Is authentication successfull ??  {} ", holder.value);
			return holder.value;
		}

		return false;
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
	 * @param key
	 * @return
	 */
	private String getProperty(String key) {
		return P6ReloadablePropertiesReader.getProperty(key);
	}


	@Override
	public boolean logoutFromP6() {
		final RequestTrackingId trackingId = new RequestTrackingId();
		boolean status = false;
		if (null != CacheManager.getWsHeaders().get(WS_COOKIE)) {
			LogoutServiceCall authService = new LogoutServiceCall(trackingId);
			try {
				status = authService.run().value.isReturn();
			} catch (P6ServiceException e) {
				logger.error("Error occurs during logout - ", e);
			}
			logger.info("Is logout successfull ??  {} ", status);
		}

		return status;

	}
	private int readUDFTypeForExecutionPackage() throws P6ServiceException {
		logger.info("Reading UDF type details from P6 ..");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		StringBuilder filter = new StringBuilder();
		filter.append("SubjectArea='"+ getProperty(SUBJECT_AREA) +"' and Title = '"+ getProperty(EXECUTION_GROUPING) +"'");

		UDFTypeServiceCall udfTypeServiceCall = new UDFTypeServiceCall(trackingId, filter.toString());
		Holder<List<UDFType>> udfTypes = udfTypeServiceCall.run();
		return udfTypes.value.get(0).getObjectId();
	}


	@Override
	public Boolean removeExecutionPackage(List<Integer> foreignObjIds, boolean doLogout)
			throws P6ServiceException {
		logger.info("Calling udfvalue service in P6 Webservice to de link execution package...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		if (null == foreignObjIds) {
			throw new P6ServiceException("NO_SEARCH_CRITERIA_FOUND");
		}
		int udfObjectId = readUDFTypeForExecutionPackage();
		List<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId> objectIds = new ArrayList<au.com.wp.corp.p6.wsclient.udfvalue.DeleteUDFValues.ObjectId>();
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
		final UDFValueServiceCall<Boolean> deleteUdfservice = new DeleteUDFValueServiceCall(trackingId, objectIds);
		final Holder<Boolean> isDeleted = deleteUdfservice.run();
		logoutFromP6();
		return isDeleted.value;
	}
	@Override
	public Boolean updateExecutionPackage(List<ExecutionPackageCreateRequest> request) throws P6ServiceException{
		logger.info("Calling udfvalue  service in P6 Webservice to Update executionpackage...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);
		if (null == request) {
			throw new P6ServiceException("NO_SEARCH_CRITERIA_FOUND");
		}
		final UDFValueServiceCall<Boolean> updateUdfservice = new UpdateUDFValueServiceCall(trackingId,
				udfServiceWSDL, request);
		logger.debug("creating for request package name {}", request.get(0).getText());
		final Holder<Boolean> result = updateUdfservice.run();
		return result.value;
	}
}

