/**
 * 
 */
package au.com.wp.corp.p6.wsclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.dto.ActivitySearchRequest;
import au.com.wp.corp.p6.dto.Crew;
import au.com.wp.corp.p6.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.ResourceSearchRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.utils.P6Constant;
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
	private Logger logger = LoggerFactory.getLogger(P6WSClientIntegrationTest.class);;
	
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
	public void testSearchWorkOrder_withCrew () throws P6ServiceException {
		ActivitySearchRequest request = new ActivitySearchRequest();
		request.setPlannedStartDate("2017-07-26");
		request.setCrewList(Arrays.asList("MONT1,MONT2"));
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
	public void testSearchWorkOrder_withNullRequest () throws P6ServiceException {
		List<WorkOrder> workOrders = null;
		try {
			 workOrders =  p6WSClient.searchWorkOrder(null);
			
		} catch (P6ServiceException e) {
			Assert.assertEquals(e.getMessage(), "NO_SEARCH_CRITERIA_FOUND");
		}
		
		Assert.assertNull(workOrders);
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
	
	@Test
	public void testCreateExecutionPackage() throws P6ServiceException {
		List<ExecutionPackageCreateRequest> request = new ArrayList<>();

		ExecutionPackageCreateRequest executionPackageCreateRequest = new ExecutionPackageCreateRequest();
		executionPackageCreateRequest.setForeignObjectId(5401390);
		executionPackageCreateRequest.setText("18-05-2017_023711511");
		executionPackageCreateRequest.setUdfTypeDataType(P6Constant.TEXT);
		executionPackageCreateRequest.setUdfTypeObjectId(5920);
		executionPackageCreateRequest.setUdfTypeSubjectArea(P6Constant.ACTIVITY);
		executionPackageCreateRequest.setUdfTypeTitle(P6Constant.EXECUTION_GROUPING);
		request.add(executionPackageCreateRequest);
		p6WSClient.getWorkOrderIdMap().put("05214374002", 5401390);
		ExecutionPackageDTO dto = p6WSClient.createExecutionPackage(request);
		if (dto != null) {
				Assert.assertEquals("18-05-2017_023711511",dto.getExctnPckgName());
				Assert.assertEquals("05214374002", dto.getWorkOrders().get(0).getWorkOrderId());
			
		}

	}

	@Test
	public void testRemoveExecutionPackage() throws P6ServiceException {
		List<Integer> foreignIds = new ArrayList<Integer>();
		foreignIds.add(5401390);
		Boolean  success = p6WSClient.removeExecutionPackage(foreignIds);
		logger.info("success {}",success);
		Assert.assertNotNull(success);

	}
}
