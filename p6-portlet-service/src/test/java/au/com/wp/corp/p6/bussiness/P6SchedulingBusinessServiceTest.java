/**
 * 
 */
package au.com.wp.corp.p6.bussiness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import au.com.wp.corp.p6.dto.UserTokenRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.mock.CreateP6MockData;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
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
	
	@Mock
	UserTokenRequest userTokenRequest;

	@InjectMocks
	P6SchedulingBusinessServiceImpl p6SchedulingBusinessService;

	@Mock
	CreateP6MockData mockData;

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
	 * Saving newly created work order with out any todos
	 * 
	 * @throws P6BusinessException
	 */
	@Test
	@Rollback(true)
	public void testSaveToDo() throws P6BusinessException {
		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("WO11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("WO11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");

		Task task = new Task();
		task.setTaskId("W11");
		task.setActioned("Y");
		task.setCrewId("CRW1");

		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		WorkOrder outputWorkOrder = p6SchedulingBusinessService.saveToDo(workOrder);

		Assert.assertNotNull(outputWorkOrder);
		Assert.assertEquals(workOrder.getCrewNames(), outputWorkOrder.getCrewNames());
		Assert.assertEquals(workOrder.getWorkOrderId(), outputWorkOrder.getWorkOrderId());
		// Assert.assertEquals(task.getActioned(),
		// outputWorkOrder.getActioned());
	}

	/**
	 * Add todos
	 * Saving newly created work order with todos  
	 * 
	 * @throws P6BusinessException
	 */
	@Test
	@Rollback(true)
	public void testSaveToDo_1() throws P6BusinessException {
		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("WO11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("WO11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");

		List<ToDoItem> toDoItems = new ArrayList<>();
		ToDoItem toDoItem = new ToDoItem();
		toDoItem.setToDoName("ESA");
		toDoItem.setWorkOrders(workOrderIds);
		toDoItems.add(toDoItem);
		workOrder.setToDoItems(toDoItems);

		Task task = new Task();
		task.setTaskId("WO11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		
		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(todoDAO.getToDoId("ESA")).thenReturn(new BigDecimal(1));
		WorkOrder outputWorkOrder = p6SchedulingBusinessService.saveToDo(workOrder);

		Assert.assertNotNull(outputWorkOrder);
		Assert.assertEquals(workOrder.getCrewNames(), outputWorkOrder.getCrewNames());
		Assert.assertEquals(workOrder.getWorkOrderId(), outputWorkOrder.getWorkOrderId());
		// Assert.assertEquals(task.getActioned(),
		// outputWorkOrder.getActioned());

		for (ToDoItem todo : outputWorkOrder.getToDoItems()) {
			Assert.assertEquals("ESA", todo.getToDoName());
		}

	}

	/**
	 * Saving newly created work order with todos and execution package
	 * 
	 * @throws P6BusinessException
	 */
	@Test
	@Rollback(true)
	public void testSaveToDo_2() throws P6BusinessException {
		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("WO11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("WO11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");
		workOrder.setExctnPckgName("28-04-2017_122345");

		ExecutionPackage excPckg = new ExecutionPackage();
		excPckg.setExctnPckgId(123456L);
		excPckg.setExctnPckgNam(workOrder.getExctnPckgName());

		List<ToDoItem> toDoItems = new ArrayList<>();
		ToDoItem toDoItem = new ToDoItem();
		toDoItem.setToDoName("ESA");
		toDoItem.setWorkOrders(workOrderIds);
		toDoItems.add(toDoItem);
		workOrder.setToDoItems(toDoItems);

		Task task = new Task();
		task.setTaskId("WO11");
		task.setActioned("Y");
		task.setCrewId("CRW1");

		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(executionPackageDao.fetch(workOrder.getExctnPckgName())).thenReturn(excPckg);
		Mockito.when(todoDAO.getToDoId("ESA")).thenReturn(new BigDecimal(1));
		WorkOrder outputWorkOrder = p6SchedulingBusinessService.saveToDo(workOrder);

		Assert.assertNotNull(outputWorkOrder);
		Assert.assertEquals(workOrder.getCrewNames(), outputWorkOrder.getCrewNames());
		Assert.assertEquals(workOrder.getWorkOrderId(), outputWorkOrder.getWorkOrderId());
		// Assert.assertEquals(task.getActioned(),
		// outputWorkOrder.getActioned());

		for (ToDoItem todo : outputWorkOrder.getToDoItems()) {
			Assert.assertEquals("ESA", todo.getToDoName());
		}

	}
	
	/**
	 * Update todos
	 * Saving newly created work order with todos  
	 * 
	 * @throws P6BusinessException
	 */
	@Test
	@Rollback(true)
	public void testSaveToDo_3() throws P6BusinessException {
		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("WO11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("WO11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");

		List<ToDoItem> toDoItems = new ArrayList<>();
		ToDoItem toDoItem = new ToDoItem();
		toDoItem.setToDoName("ESA");
		toDoItem.setWorkOrders(workOrderIds);
		toDoItems.add(toDoItem);
		workOrder.setToDoItems(toDoItems);

		Task task = new Task();
		task.setTaskId("WO11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		TodoAssignment todoAssignment = new TodoAssignment();
		todoAssignment.getTodoAssignMentPK().setTask(task);
		todoAssignment.getTodoAssignMentPK().setTodoId(new BigDecimal(2));
		Set<TodoAssignment> todos = new HashSet<>();
		task.setTodoAssignments(todos);
		
		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(todoDAO.getToDoId("ESA")).thenReturn(new BigDecimal(1));
		WorkOrder outputWorkOrder = p6SchedulingBusinessService.saveToDo(workOrder);

		Assert.assertNotNull(outputWorkOrder);
		Assert.assertEquals(workOrder.getCrewNames(), outputWorkOrder.getCrewNames());
		Assert.assertEquals(workOrder.getWorkOrderId(), outputWorkOrder.getWorkOrderId());
		// Assert.assertEquals(task.getActioned(),
		// outputWorkOrder.getActioned());

		for (ToDoItem todo : outputWorkOrder.getToDoItems()) {
			Assert.assertEquals("ESA", todo.getToDoName());
		}

	}

	/**
	 * {"exctnPckgName":"03-05-2017_09160757","workOrders":["Y6UIOP01","Y6UIOP02","Y6UIOP03"],"leadCrew":"MOST1",
	 * "crewNames":"MOST1","scheduleDate":"28/04/2017","toDoItems":[{"toDoName":"ESA","workOrders":["Y6UIOP01"]},
	 * {"toDoName":"DEC
	 * Permit","workOrders":["Y6UIOP01"]}],"$$hashKey":"object:214"}
	 * 
	 * @throws P6BusinessException
	 **/
	@Test
	@Transactional
	@Rollback(true)
	public void testSaveToDo1() throws P6BusinessException {
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
		List<ToDoItem> toDoItems = new ArrayList<>();
		ToDoItem toDoItem = new ToDoItem();
		toDoItem.setToDoName("ESA");
		toDoItem.setWorkOrders(workOrders);
		toDoItems.add(toDoItem);
		order.setToDoItems(toDoItems);
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(order.getScheduleDate())).thenReturn(new Date());
		p6SchedulingBusinessService.saveToDo(order);
	}

	/**
	 * If the work order newly created in p6 and it is not available in portal
	 * database
	 * 
	 * @throws P6BusinessException
	 */

	@Test
	public void testSearch() throws P6BusinessException {
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-04-28T00:00:00.000Z");

		List<WorkOrder> searchResult = new ArrayList<>();
		WorkOrder workOrder = new WorkOrder();

		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");
		Mockito.when(mockData.search(request)).thenReturn(searchResult);
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		Assert.assertNotNull(workOrders);

		for (WorkOrder _workOrder : workOrders) {
			Assert.assertEquals("W11", _workOrder.getWorkOrderId());
			Assert.assertEquals("CRW1", workOrder.getCrewNames());
			Assert.assertEquals("28/04/2017", workOrder.getScheduleDate());
		}

	}

	/**
	 * If the work order exist in p6 and it is available in portal database and
	 * work order is associated with execution package
	 * 
	 * @throws P6BusinessException
	 */

	@Test
	public void testSearch_1() throws P6BusinessException {
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-04-28T00:00:00.000Z");

		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("W11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");
		searchResult.add(workOrder);
		Mockito.when(mockData.search(request)).thenReturn(searchResult);
		Task task = new Task();
		task.setTaskId("W11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate())).thenReturn(new Date());
		task.setSchdDt(new Date());
		ExecutionPackage excPckg = new ExecutionPackage();
		excPckg.setExctnPckgId(123456L);
		excPckg.setExctnPckgNam("28-04-2017_12345678");
		task.setExecutionPackage(excPckg);

		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		Assert.assertNotNull(workOrders);
		Assert.assertEquals(1, workOrders.size());
		for (WorkOrder _workOrder : workOrders) {
			Assert.assertEquals("W11", _workOrder.getWorkOrders().get(0));
			Assert.assertEquals("CRW1", _workOrder.getCrewNames());
			Assert.assertEquals("28-04-2017_12345678", _workOrder.getExctnPckgName());
		}

	}

	/**
	 * If the work order exist in p6 and it is available in portal database but
	 * work order is not associated with execution package
	 * 
	 * @throws P6BusinessException
	 */

	@Test
	public void testSearch_2() throws P6BusinessException {
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-04-28T00:00:00.000Z");

		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("W11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");
		searchResult.add(workOrder);
		Mockito.when(mockData.search(request)).thenReturn(searchResult);
		Task task = new Task();
		task.setTaskId("W11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate())).thenReturn(new Date());
		task.setSchdDt(new Date());
		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		Assert.assertNotNull(workOrders);
		Assert.assertEquals(1, workOrders.size());
		for (WorkOrder _workOrder : workOrders) {
			Assert.assertEquals("W11", _workOrder.getWorkOrders().get(0));
			Assert.assertEquals("CRW1", _workOrder.getCrewNames());
			Assert.assertEquals(null, _workOrder.getExctnPckgName());
		}

	}

}
