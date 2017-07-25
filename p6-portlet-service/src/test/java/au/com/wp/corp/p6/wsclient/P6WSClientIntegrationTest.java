/**
 * 
 */
package au.com.wp.corp.p6.wsclient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.dataservice.ResourceDetailDAO;
import au.com.wp.corp.p6.dto.ActivitySearchRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.utils.P6Constant;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.wsclient.cleint.impl.P6WSClientImpl;
import au.com.wp.corp.p6.wsclient.logging.RequestTrackingId;

/**
 * @author N039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6WSClientIntegrationTest {
	
	@Autowired
	P6WSClient p6WSClient;
	@Autowired
	ResourceDetailDAO resourceDetailDAO;
	List<WorkOrder> baseWorders = null;
	
	Map<String, List<String>> depotCrewMap = new HashMap<String,List<String>>();
	
	private Logger logger = LoggerFactory.getLogger(P6WSClientIntegrationTest.class);;
	
	@Before
	public void setup() {
		ActivitySearchRequest activitySearchRequest = new ActivitySearchRequest();
		activitySearchRequest.setPlannedStartDate("2017-03-02");
		try {
			baseWorders = p6WSClient.searchWorkOrder(activitySearchRequest);
		} catch (P6ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	@Transactional
	@Rollback
	public void testSearchWorkOrder () throws P6ServiceException {
		ActivitySearchRequest request = new ActivitySearchRequest();
		depotCrewMap = resourceDetailDAO.fetchAllResourceDetail();
		List<String> crewListAll = new ArrayList<String>();
		crewListAll = depotCrewMap.values().stream().flatMap(List::stream)
				.collect(Collectors.toList());
		request.setPlannedStartDate("2017-06-14");
		request.setCrewList(crewListAll);
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
		request.setPlannedStartDate("2017-06-14");
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
	public void testCreateExecutionPackage() throws P6ServiceException {
		List<ExecutionPackageCreateRequest> request = new ArrayList<>();

		ExecutionPackageCreateRequest executionPackageCreateRequest = new ExecutionPackageCreateRequest();
		Entry<String, Integer> entryInMap = p6WSClient.getWorkOrderIdMap().entrySet().iterator().next();
		Integer foreignObjectId = entryInMap.getValue();
		String workOrderId = entryInMap.getKey();
		executionPackageCreateRequest.setForeignObjectId(foreignObjectId);
		executionPackageCreateRequest.setText("18-05-2017_023711511");
		executionPackageCreateRequest.setUdfTypeDataType(P6Constant.TEXT);
		executionPackageCreateRequest.setUdfTypeObjectId(5920);
		executionPackageCreateRequest.setUdfTypeSubjectArea(P6Constant.ACTIVITY);
		executionPackageCreateRequest.setUdfTypeTitle(P6Constant.EXECUTION_GROUPING);
		request.add(executionPackageCreateRequest);
		ExecutionPackageDTO dto = p6WSClient.createExecutionPackage(request);
		if (dto != null) {
				Assert.assertEquals("18-05-2017_023711511",dto.getExctnPckgName());
				Assert.assertEquals(workOrderId, dto.getWorkOrders().get(0).getWorkOrderId());
			
		}

	}

	@Test
	public void test_7_LogoutFromP6 () throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		RequestTrackingId trackingId = new RequestTrackingId();
		Set<Method> privateMethods = ReflectionUtils.getAllMethods(P6WSClientImpl.class, ReflectionUtils.withModifier(Modifier.PRIVATE));
		for(Method m: privateMethods){
			if(m.getName().equalsIgnoreCase("getAuthenticated")){
				m.setAccessible(true);
				m.invoke(p6WSClient, trackingId);
			}
		}
		boolean status = p6WSClient.logoutFromP6(trackingId, true);
		Assert.assertTrue(status);
	}
	
	@Test
	public void testRemoveExecutionPackage() throws P6ServiceException {
		List<Integer> foreignIds = new ArrayList<Integer>();
		Entry<String, Integer> entryInMap = p6WSClient.getWorkOrderIdMap().entrySet().iterator().next();
		Integer foreignObjectId = entryInMap.getValue();
		foreignIds.add(foreignObjectId);
		Boolean  success = p6WSClient.removeExecutionPackage(foreignIds, true);
		logger.info("success {}",success);
		Assert.assertNotNull(success);

	}
	@Test
	public void testRemoveExecutionPackage_withNullForeignIds() throws P6ServiceException {
		List<Integer> foreignIds = new ArrayList<Integer>();
		foreignIds.add(null);
		p6WSClient.getWorkOrderIdMap().put("05236356002", 0);
		Boolean  success = p6WSClient.removeExecutionPackage(foreignIds, true);
		logger.info("success {}",success);
		Assert.assertNotNull(success);

	}
}
