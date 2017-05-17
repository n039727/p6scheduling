/**
 * 
 */
package au.com.wp.corp.p6.wsclient;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.dto.Crew;
import au.com.wp.corp.p6.dto.ResourceSearchRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.model.ActivitySearchRequest;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;

/**
 * @author N039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6WSClientIntegrationTest {
	
	@Autowired
	P6WSClient p6WSClient;
	
	@Before
	public void setup() {
	}
	
	@Test
	public void testSearchWorkOrder () throws P6ServiceException {
		ActivitySearchRequest request = new ActivitySearchRequest();
		request.setPlannedStartDate("2017-07-26");
		List<WorkOrder> workOrders =  p6WSClient.searchWorkOrder(request);
		Assert.assertNotNull(workOrders);
		for ( WorkOrder workOrder : workOrders)
		{
			Assert.assertNotNull(workOrder.getWorkOrderId());
			Assert.assertNotNull(workOrder.getCrewNames());
			Assert.assertNotNull(workOrder.getWorkOrders());
			Assert.assertNotNull(workOrder.getScheduleDate());
		}
		
	}
	
	@Test
	public void testsearchCrew () throws P6ServiceException {
		ResourceSearchRequest request = new ResourceSearchRequest();
		request.setResourceType("Labor");
		List<Crew> crews =  p6WSClient.searchCrew(request);
		Assert.assertNotNull(crews);
		for ( Crew crew : crews)
		{
			Assert.assertNotNull(crew.getCrewId());
			Assert.assertNotNull(crew.getCrewName());
			
		}
		
	}

}
