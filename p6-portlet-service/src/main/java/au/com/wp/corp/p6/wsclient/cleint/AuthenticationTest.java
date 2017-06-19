/**
 * 
 */
package au.com.wp.corp.p6.wsclient.cleint;

import java.util.ArrayList;
import java.util.List;

import au.com.wp.corp.p6.dto.ActivitySearchRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.wsclient.cleint.impl.P6WSClientImpl;

/**
 * @author n039126
 *
 */
public class AuthenticationTest {

	/**
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args) throws Exception {
		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		
		List<String> crews = new ArrayList<>();
		
		searchRequest.setCrewList(crews);
		searchRequest.setPlannedStartDate("2017-05-25");
		
		P6WSClient p6wsClient = new P6WSClientImpl();
		
		List <WorkOrder> workOrders = p6wsClient.searchWorkOrder(searchRequest);
		
		for (WorkOrder workOrder : workOrders ){
			StringBuilder sb = new StringBuilder();
			sb.append("*******************************");
			sb.append("\n planned start date = "+ workOrder.getScheduleDate()  );
			sb.append("\n Work Order Id = "+workOrder.getWorkOrderId());
			sb.append("\n Assigned Crew = "+ workOrder.getCrewNames());
			sb.append("\n*******************************");
			
		}
		
		
		/**
		
		ActivityService activityService = new ActivityService(trackingId, "", null);
		Holder<List<Activity>> activityHolder =  activityService.run();
		
		List<Activity> activities = activityHolder.value;
		
		for ( Activity activity : activities){
			StringBuilder sb = new StringBuilder();
			sb.append("*******************************");
			sb.append("\n planned start date = "+ activity.getPlannedStartDate()  );
			sb.append("\n ID = "+activity.getId());
			sb.append("\n Object Id = "+ activity.getObjectId());
			sb.append("\n Name = "+ activity.getName());
			sb.append("\n Primary Resource Object Id = "+ activity.getPrimaryResourceObjectId());
			sb.append("\n Primary Resource name = "+ activity.getPrimaryResourceName());
			sb.append("\n Project Object Id = "+ activity.getProjectObjectId());
			sb.append("\n Primary Resource Id = "+ activity.getPrimaryResourceId());			
			sb.append("\n*******************************");
			
		}
		**/

		
	}

}
