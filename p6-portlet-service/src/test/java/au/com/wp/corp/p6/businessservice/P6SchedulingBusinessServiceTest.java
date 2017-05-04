/**
 * 
 */
package au.com.wp.corp.p6.businessservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.utils.DateUtils;

/**
 * @author N039603
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6SchedulingBusinessServiceTest {

	@Mock
	WorkOrderDAOImpl workOrderDAO;
	
	@Mock
	DateUtils dateUtils;
	
	@Mock
	ExecutionPackageDao executionPackageDao;
	
	@Mock
	TodoDAO todoDAO;

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
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#retrieveWorkOrders(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	/*
	 * @Test public void testRetrieveWorkOrders() { fail("Not yet implemented");
	 * }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#retrieveJobs(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	/*
	 * @Test public void testRetrieveJobs() { fail("Not yet implemented"); }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#saveWorkOrder(au.com.wp.corp.p6.dto.WorkOrder)}.
	 */
	/*
	 * @Test public void testSaveWorkOrder() { fail("Not yet implemented"); }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#listTasks()}.
	 */
	/*
	 * @Test public void testListTasks() { fail("Not yet implemented"); }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#fetchToDos()}.
	 */
	/*
	 * @Test public void testFetchToDos() { fail("Not yet implemented"); }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#fetchWorkOrdersForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	/*
	 * @Test public void testFetchWorkOrdersForViewToDoStatus() {
	 * fail("Not yet implemented"); }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#saveToDo(au.com.wp.corp.p6.dto.WorkOrder)}.
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

	private WorkOrder populateWorkOrder(WorkOrder workOrder) {

		workOrder.setCrewNames("JunitTestCrew");
		workOrder.setLeadCrew("JunitLeadCrew");
		return workOrder;

	}

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#saveExecutionPackage(au.com.wp.corp.p6.dto.ExecutionPackageDTO)}.
	 */
	/*
	 * @Test public void testSaveExecutionPackage() {
	 * fail("Not yet implemented"); }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#fetchExecutionPackageList()}.
	 */
	/*
	 * @Test public void testFetchExecutionPackageList() {
	 * fail("Not yet implemented"); }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#fetchWorkOrdersForAddUpdateToDo(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	/*
	 * @Test public void testFetchWorkOrdersForAddUpdateToDo() {
	 * fail("Not yet implemented"); }
	 */

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl#saveViewToDoStatus(au.com.wp.corp.p6.dto.ViewToDoStatus)}.
	 */
	/*
	 * @Test public void testSaveViewToDoStatus() { fail("Not yet implemented");
	 * }
	 */

	/**
	 * {"exctnPckgName":"03-05-2017_09160757","workOrders":["Y6UIOP01","Y6UIOP02","Y6UIOP03"],"leadCrew":"MOST1",
	 * "crewNames":"MOST1","scheduleDate":"28/04/2017","toDoItems":[{"toDoName":"ESA","workOrders":["Y6UIOP01"]},
	 * {"toDoName":"DEC Permit","workOrders":["Y6UIOP01"]}],"$$hashKey":"object:214"}
	 **/
	@Test
	@Transactional
	@Rollback(true)
	public void testSaveToDo1() {
		WorkOrder order = new WorkOrder();
		order.setExctnPckgName("03-05-2017_09160757");
		order.setLeadCrew("MOST1");
		
		List<String> workOrders = new ArrayList<>();
		workOrders.add("Y6UIOP01");
		workOrders.add("Y6UIOP02");
		workOrders.add("Y6UIOP03");
		
		order.setWorkOrders(workOrders);
		order.setScheduleDate("28/04/2017");
		order.setCrewNames("MOST1");
		List<ToDoItem> toDoItems =  new ArrayList<>();
		ToDoItem toDoItem = new ToDoItem();
		toDoItem.setToDoName("ESA");
		toDoItem.setWorkOrders(workOrders);
		toDoItems.add(toDoItem);
		order.setToDoItems(toDoItems);
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(order.getScheduleDate())).thenReturn(new Date());
		p6SchedulingBusinessService.saveToDo(order);
	}

}
