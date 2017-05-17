/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.dto.Crew;
import au.com.wp.corp.p6.dto.ResourceSearchRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.model.ActivitySearchRequest;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;
import au.com.wp.corp.p6.wsclient.resource.Resource;

/**
 * 
 * @author n039126
 * @version 1.0
 */
@Service
@PropertySource("file:/${properties.dir}/p6portal-wsdl.properties")
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
	
	@Value ("${P6_USER_CREDENTIAL}")
	private String userCredential;
	
	@Value ("${P6_DB_INSTANCE}")
	private int p6DBInstance;
	
	@Value("${P6_RESOURCE_SERVICE_WSDL}")
	private String resourceServiceWSDL;
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.wsclient.cleint.P6WSClient#searchWorkOrder(au.com.wp.
	 * corp.p6.model.ActivitySearchRequest)
	 */
	@Override
	public List<WorkOrder> searchWorkOrder(ActivitySearchRequest searchRequest) throws P6ServiceException {
		logger.info("Calling activity service in P6 Webservice ...");
		final RequestTrackingId trackingId = new RequestTrackingId();
		getAuthenticated(trackingId);

		if (null == searchRequest) {
			throw new P6ServiceException("NO_SEARCH_CRITERIA_FOUND");
		}

		final StringBuilder filter = new StringBuilder();
		if (null != searchRequest.getCrewList()) {
			int i = 0;
			if (searchRequest.getCrewList().size() > 1)
				filter.append("(");
			for (String crew : searchRequest.getCrewList()) {
				if (i > 0)
					filter.append(OR);
				filter.append("PrimaryResourceId = ");
				filter.append("'" + crew + "'");
				i++;
			}
			if (searchRequest.getCrewList().size() > 1)
				filter.append(")");
		}

		if (null != searchRequest.getPlannedStartDate()) {
			if (filter.length() > 0)
				filter.append(AND);
			filter.append("PlannedStartDate BETWEEN TO_DATE('");
			filter.append(searchRequest.getPlannedStartDate() + " 00:00:00', 'yyyy-mm-dd hh24:mi:ss') AND TO_DATE('");
			filter.append(searchRequest.getPlannedStartDate() + " 23:59:59', 'yyyy-mm-dd hh24:mi:ss')");
		}

		if ( null != searchRequest.getWorkOrder() ) {
			if (filter.length() > 0)
				filter.append(OR);
			filter.append("(Id = ");
			filter.append("'"+searchRequest.getWorkOrder()+"')");
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
				workOrder.setScheduleDate(activity.getPlannedStartDate().toString());
				List<String> wos = new ArrayList<>();
				wos.add(activity.getId());
				workOrder.setWorkOrders(wos);
				workOrders.add(workOrder);
			}
		}

		return workOrders;
	}
	
	

	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.wsclient.cleint.P6WSClient#searchCrew(au.com.wp.corp.p6.dto.ResourceSearchRequest)
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
				crew.setCrewId(resource.getId());;
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
			AuthenticationService authService = new AuthenticationService(trackingId, authServiceWSDL, userPrincipal,userCredential,p6DBInstance);
			Holder<Boolean> holder = authService.run();
			logger.debug("Is authentication successfull ??  {} ", holder.value);
			if (holder.value)
				CacheManager.getWSLoginTimestamp().put(WS_AUTH_SERVICE_CALL_TIME, System.currentTimeMillis());
			return holder.value;
		}

		return false;
	}

}
