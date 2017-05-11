/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.model.ActivitySearchRequest;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.wsclient.activity.Activity;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;

/**
 * @author n039126
 *
 */
@Service
public class P6WSClientImpl implements P6WSClient {
	private static final Logger logger= LoggerFactory.getLogger(P6WSClientImpl.class);
	private static final String AND = " AND ";

	private static final String OR = " OR ";
	
	private static final String ACTIVITY_SERVICE_WSDL = "http://sdc-tssgt01:8206/p6ws/services/ActivityService?wsdl";
	
	private static final String AUTH_SERVICE_WSDL = "http://sdc-tssgt01:8206/p6ws/services/AuthenticationService?wsdl";

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * au.com.wp.corp.p6.wsclient.cleint.P6WSClient#searchWorkOrder(au.com.wp.
	 * corp.p6.model.ActivitySearchRequest)
	 */
	@Override
	public List<WorkOrder> searchWorkOrder(ActivitySearchRequest searchRequest) throws P6ServiceException {
		final RequestTrackingId trackingId = new RequestTrackingId();
		if ( CacheManager.getWsHeaders().isEmpty()) {
			AuthenticationService authService = new AuthenticationService(trackingId, AUTH_SERVICE_WSDL);
			Holder<Boolean> holder = authService.run();
			logger.debug("Is authentication successfull ??  {} ", holder.value);
		}

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
				filter.append("'"+crew+"'");
				i++;
			}
			if (searchRequest.getCrewList().size() > 1)
				filter.append(")");
		}
		
		if ( null != searchRequest.getPlannedStartDate()) {
			if (filter.length() > 0)
			filter.append(AND);
			filter.append("PlannedStartDate BETWEEN TO_DATE('");
			filter.append(searchRequest.getPlannedStartDate()+" 00:00:00', 'yyyy-mm-dd hh24:mi:ss') AND TO_DATE('");
			filter.append(searchRequest.getPlannedStartDate()+" 23:59:59', 'yyyy-mm-dd hh24:mi:ss')"); 
		}
		
		logger.debug("filter criteria for search # {} ", filter.toString());

		final ActivityService activityService = new ActivityService(trackingId, ACTIVITY_SERVICE_WSDL,
				filter.length() > 0 ? filter.toString() : null);

		final Holder<List<Activity>> activities = activityService.run();
		final List<WorkOrder> workOrders = new ArrayList<>();
		logger.debug("list of activities from P6 # {}",activities );
		if (null != activities) {
			logger.debug("size of activity list from P6 # {}",activities.value.size() );
			for ( Activity activity : activities.value){
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

}
