/**
 * 
 */
package au.com.wp.corp.p6.businessservice;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl;
import au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl;
import au.com.wp.corp.p6.dto.WorkOrder;

/**
 * @author N039603
 *
 */
public class P6SchedulingBusinessServiceTest {
	
	@Mock
	WorkOrderDAOImpl workOrderDAO;
	
	
	@InjectMocks
	P6SchedulingBusinessServiceImpl p6SchedulingBusinessService;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#retrieveWorkOrders(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	/*@Test
	public void testRetrieveWorkOrders() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#retrieveJobs(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	/*@Test
	public void testRetrieveJobs() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#saveWorkOrder(au.com.wp.corp.p6.dto.WorkOrder)}.
	 */
	/*@Test
	public void testSaveWorkOrder() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#listTasks()}.
	 */
	/*@Test
	public void testListTasks() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#fetchToDos()}.
	 */
	/*@Test
	public void testFetchToDos() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#fetchWorkOrdersForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	/*@Test
	public void testFetchWorkOrdersForViewToDoStatus() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#saveToDo(au.com.wp.corp.p6.dto.WorkOrder)}.
	 */
	@Test
	public void testSaveToDo() {
		WorkOrder inputWorkOrder = new WorkOrder();
		WorkOrder outputWorkOrder = new WorkOrder();
		inputWorkOrder = populateWorkOrder(inputWorkOrder);
		outputWorkOrder = populateWorkOrder(outputWorkOrder);
		
		outputWorkOrder = p6SchedulingBusinessService.saveToDo(inputWorkOrder);
		Assert.assertEquals(outputWorkOrder.getCrewNames(), inputWorkOrder.getCrewNames());
	}
	
	private WorkOrder populateWorkOrder(WorkOrder workOrder){
		
		workOrder.setCrewNames("JunitTestCrew");
		workOrder.setLeadCrew("JunitLeadCrew");
		return workOrder;
		
	}
	

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#saveExecutionPackage(au.com.wp.corp.p6.dto.ExecutionPackageDTO)}.
	 */
	/*@Test
	public void testSaveExecutionPackage() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#fetchExecutionPackageList()}.
	 */
	/*@Test
	public void testFetchExecutionPackageList() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#fetchWorkOrdersForAddUpdateToDo(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	/*@Test
	public void testFetchWorkOrdersForAddUpdateToDo() {
		fail("Not yet implemented");
	}*/

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#saveViewToDoStatus(au.com.wp.corp.p6.dto.ViewToDoStatus)}.
	 */
	/*@Test
	public void testSaveViewToDoStatus() {
		fail("Not yet implemented");
	}*/

}
