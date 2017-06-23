/**
 * 
 */
package au.com.wp.corp.p6.bussiness;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.businessservice.IExecutionPackageService;
import au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.ResourceDetailDAO;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl;
import au.com.wp.corp.p6.dto.ActivitySearchRequest;
import au.com.wp.corp.p6.dto.MetadataDTO;
import au.com.wp.corp.p6.dto.ResourceDTO;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.UserTokenRequest;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoAssignmentPK;
import au.com.wp.corp.p6.model.TodoTemplate;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.utils.DateUtils;
import au.com.wp.corp.p6.wsclient.cleint.impl.P6WSClientImpl;

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
	ResourceDetailDAO resourceDetailDAO;

	@Mock
	UserTokenRequest userTokenRequest;

	@Mock
	IExecutionPackageService executionPackageService;
	
	@InjectMocks
	P6SchedulingBusinessServiceImpl p6SchedulingBusinessService;

	@Mock
	P6WSClientImpl p6wsClient;
	
	
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

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
	 * Add todos Saving newly created work order with todos
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
	
	@Test
	@Rollback(true)
	public void testRetrieveWorkOrderWoId() throws P6BusinessException{
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setWorkOrderId("11");
		request.setFromDate("");
		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("11");
		workOrder.setCrewNames("MOST1");
		workOrder.setScheduleDate("19/05/2017");
		workOrder.setExctnPckgName("1234567890");
		workOrder.setLeadCrew("CRW1");
		WorkOrder workOrder2 = new WorkOrder();

		searchResult.add(workOrder);
		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-05-19");
		Mockito.when(dateUtils.convertDate(request.getFromDate())).thenReturn("2017-05-19");
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);
		Set<Task> tasks = new HashSet<Task>();
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setActioned("Y");
		executionPackage.setExctnPckgId(1234);
		executionPackage.setExctnPckgNam("1234567890");
		executionPackage.setLeadCrewId("CRW1");
		executionPackage.setTasks(tasks);
		List<Task> taskList = new ArrayList<Task>();
		Task task = new Task();
		task.setTaskId("11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
		task.setExecutionPackage(executionPackage);
		Set<TodoAssignment> todoAssignments = new HashSet<TodoAssignment>();
		TodoAssignmentPK todoAssignMentPK = new TodoAssignmentPK();
		todoAssignMentPK.setTask(task);
		todoAssignMentPK.setTodoId(new BigDecimal("123456789"));
		TodoAssignment todo = new TodoAssignment();
		todo.setTodoAssignMentPK(todoAssignMentPK);
		todo.setStat("Completed");
		task.setTodoAssignments(todoAssignments);
		Task task1 = new Task();
		task1.setTaskId("12");
		task1.setActioned("Y");
		task1.setCrewId("CRW2");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
		task1.setExecutionPackage(executionPackage);
		Task task2 = new Task();
		task2.setTaskId("13");
		task2.setActioned("Y");
		task2.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate())).thenReturn(new Date());
		task2.setSchdDt(new Date());
		task2.setExecutionPackage(executionPackage);
		Task task3 = new Task();
		task3.setTaskId("14");
		task3.setActioned("Y");
		task3.setCrewId("CRW1");
		task3.setSchdDt(dateUtils.toDateFromDD_MM_YYYY("18/05/2017"));
		tasks.add(task);
		taskList.add(task);
		tasks.add(task1);
		taskList.add(task1);
		tasks.add(task2);
		taskList.add(task2);
		tasks.add(task3);
		taskList.add(task3);
		List<Date> dateRange = new ArrayList<Date>();
		if (request != null) {
			dateRange.add(request.getFromDate() != null ? dateUtils.toDateFromYYYY_MM_DD(request.getFromDate()): null);
			dateRange.add(request.getToDate() != null ? dateUtils.toDateFromYYYY_MM_DD(request.getToDate()) : null);

		}
		List<String> worOrders = new ArrayList<String>();
		if (searchResult != null) {
			searchResult.forEach(wo->{
				worOrders.add(wo.getWorkOrderId());
			});

		}
		Mockito.when(workOrderDAO.fetchTasksByDateAndWo(dateRange,worOrders)).thenReturn(taskList);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		Mockito.when(p6wsClient.removeExecutionPackage(Mockito.anyList())).thenReturn(true);
		Mockito.when(workOrderDAO.saveTask(Mockito.any())).thenReturn(task);
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		p6SchedulingBusinessService.updateTasksAndExecutionPackageInP6AndDB();
		Assert.assertNotNull(workOrders);
	}

	@Test
	@Rollback(true)
	public void testPrepareWorkOrder() throws P6BusinessException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		List<String> depotList = new ArrayList<String>();
		depotList.add("DEPOT1");
		request.setDepotList(depotList);
		request.setFromDate("");
		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();
		Map<String, List<String>> depotCrewMap = new HashMap<String, List<String>>();
		List<String> crewList = new ArrayList<>();
		crewList.add("MOST1");
		depotCrewMap.put("DEPOT1", crewList);
		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("11");
		workOrder.setCrewNames("MOST1");
		workOrder.setDepotId("DEPOT1");
		workOrder.setScheduleDate("19/05/2017");
		workOrder.setExctnPckgName("1234567890");
		workOrder.setLeadCrew("MOST1");
		WorkOrder workOrder2 = new WorkOrder();

		List<String> workOrderIds2 = new ArrayList<>();
		workOrderIds2.add("14");
		workOrder2.setWorkOrders(workOrderIds2);
		workOrder2.setWorkOrderId("14");
		workOrder2.setCrewNames("MOST1");
		workOrder2.setDepotId("DEPOT1");
		workOrder2.setExctnPckgName("1234567890");
		workOrder2.setScheduleDate("19/05/2017");
		searchResult.add(workOrder2);
		searchResult.add(workOrder);
		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-05-19");
		Mockito.when(dateUtils.convertDate(request.getFromDate())).thenReturn("2017-05-19");
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);
		Field f = p6SchedulingBusinessService.getClass().getDeclaredField("depotCrewMap"); //NoSuchFieldException
		f.setAccessible(true);
		f.set(p6SchedulingBusinessService, depotCrewMap);
		Set<Task> tasks = new HashSet<Task>();
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setActioned("Y");
		executionPackage.setExctnPckgId(1234);
		executionPackage.setExctnPckgNam("1234567890");
		executionPackage.setLeadCrewId("CRW1");
		executionPackage.setTasks(tasks);
		List<Task> taskList = new ArrayList<Task>();
		Task task = new Task();
		task.setTaskId("11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
		task.setExecutionPackage(executionPackage);
		Set<TodoAssignment> todoAssignments = new HashSet<TodoAssignment>();
		TodoAssignmentPK todoAssignMentPK = new TodoAssignmentPK();
		todoAssignMentPK.setTask(task);
		todoAssignMentPK.setTodoId(new BigDecimal("123456789"));
		TodoAssignment todo = new TodoAssignment();
		todo.setTodoAssignMentPK(todoAssignMentPK);
		todo.setStat("Completed");
		task.setTodoAssignments(todoAssignments);
		Task task1 = new Task();
		task1.setTaskId("12");
		task1.setActioned("Y");
		task1.setCrewId("CRW2");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
		task1.setExecutionPackage(executionPackage);
		Task task2 = new Task();
		task2.setTaskId("13");
		task2.setActioned("Y");
		task2.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate())).thenReturn(new Date());
		task2.setSchdDt(new Date());
		task2.setExecutionPackage(executionPackage);
		Task task3 = new Task();
		task3.setTaskId("14");
		task3.setActioned("Y");
		task3.setCrewId("CRW1");
		Date date = new Date("19/05/2017");
		task3.setSchdDt(date);
		task3.setExecutionPackage(executionPackage);
		tasks.add(task);
		taskList.add(task);
		tasks.add(task1);
		taskList.add(task1);
		tasks.add(task2);
		taskList.add(task2);
		tasks.add(task3);
		taskList.add(task3);
		List<Date> dateRange = new ArrayList<Date>();
		if (request != null) {
			dateRange.add(request.getFromDate() != null ? dateUtils.toDateFromYYYY_MM_DD(request.getFromDate()): null);
			dateRange.add(request.getToDate() != null ? dateUtils.toDateFromYYYY_MM_DD(request.getToDate()) : null);

		}
		List<String> worOrders = new ArrayList<String>();
		if (searchResult != null) {
			searchResult.forEach(wo->{
				worOrders.add(wo.getWorkOrderId());
			});

		}
		Mockito.when(workOrderDAO.fetchTasksByDateAndWo(dateRange,worOrders)).thenReturn(taskList);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		Mockito.when(p6wsClient.removeExecutionPackage(Mockito.anyList())).thenReturn(true);
		Mockito.when(workOrderDAO.saveTask(Mockito.any())).thenReturn(task);
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		p6SchedulingBusinessService.updateTasksAndExecutionPackageInP6AndDB();
		Assert.assertNotNull(workOrders);
	}
	@Test
	@Rollback(true)
	public void testRetrieveWorkOrderDepot() throws P6BusinessException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		List<String> depotList = new ArrayList<String>();
		depotList.add("DEPOT1");
		request.setDepotList(depotList);
		request.setFromDate("");
		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();
		Map<String, List<String>> depotCrewMap = new HashMap<String, List<String>>();
		List<String> crewList = new ArrayList<>();
		crewList.add("MOST1");
		depotCrewMap.put("DEPOT1", crewList);
		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("11");
		workOrder.setCrewNames("MOST1");
		workOrder.setDepotId("DEPOT1");
		workOrder.setScheduleDate("19/05/2017");
		workOrder.setExctnPckgName("1234567890");
		workOrder.setLeadCrew("MOST1");
		WorkOrder workOrder2 = new WorkOrder();

		List<String> workOrderIds2 = new ArrayList<>();
		workOrderIds2.add("14");
		workOrder2.setWorkOrders(workOrderIds2);
		workOrder2.setWorkOrderId("14");
		workOrder2.setCrewNames("MOST1");
		workOrder2.setDepotId("DEPOT1");
		workOrder2.setScheduleDate("19/05/2017");
		workOrder2.setExctnPckgName("1234567890");
		searchResult.add(workOrder2);
		searchResult.add(workOrder);
		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-05-19");
		Mockito.when(dateUtils.convertDate(request.getFromDate())).thenReturn("2017-05-19");
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);
		Field f = p6SchedulingBusinessService.getClass().getDeclaredField("depotCrewMap"); //NoSuchFieldException
		f.setAccessible(true);
		f.set(p6SchedulingBusinessService, depotCrewMap);
		Set<Task> tasks = new HashSet<Task>();
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setActioned("Y");
		executionPackage.setExctnPckgId(1234);
		executionPackage.setExctnPckgNam("1234567890");
		executionPackage.setLeadCrewId("CRW1");
		executionPackage.setTasks(tasks);
		List<Task> taskList = new ArrayList<Task>();
		Task task = new Task();
		task.setTaskId("11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
		task.setExecutionPackage(executionPackage);
		Set<TodoAssignment> todoAssignments = new HashSet<TodoAssignment>();
		TodoAssignmentPK todoAssignMentPK = new TodoAssignmentPK();
		todoAssignMentPK.setTask(task);
		todoAssignMentPK.setTodoId(new BigDecimal("123456789"));
		TodoAssignment todo = new TodoAssignment();
		todo.setTodoAssignMentPK(todoAssignMentPK);
		todo.setStat("Completed");
		task.setTodoAssignments(todoAssignments);
		Task task1 = new Task();
		task1.setTaskId("12");
		task1.setActioned("Y");
		task1.setCrewId("CRW2");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
		task1.setExecutionPackage(executionPackage);
		Task task2 = new Task();
		task2.setTaskId("13");
		task2.setActioned("Y");
		task2.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate())).thenReturn(new Date());
		task2.setSchdDt(new Date());
		task2.setExecutionPackage(executionPackage);
		Task task3 = new Task();
		task3.setTaskId("14");
		task3.setActioned("Y");
		task3.setCrewId("MOST1");
		task3.setSchdDt(dateUtils.toDateFromDD_MM_YYYY("18/05/2017"));
		task3.setExecutionPackage(executionPackage);
		tasks.add(task);
		taskList.add(task);
		tasks.add(task1);
		taskList.add(task1);
		tasks.add(task2);
		taskList.add(task2);
		tasks.add(task3);
		taskList.add(task3);
		List<Date> dateRange = new ArrayList<Date>();
		if (request != null) {
			dateRange.add(request.getFromDate() != null ? dateUtils.toDateFromYYYY_MM_DD(request.getFromDate()): null);
			dateRange.add(request.getToDate() != null ? dateUtils.toDateFromYYYY_MM_DD(request.getToDate()) : null);

		}
		List<String> worOrders = new ArrayList<String>();
		if (searchResult != null) {
			searchResult.forEach(wo->{
				worOrders.add(wo.getWorkOrderId());
			});

		}
		Mockito.when(workOrderDAO.fetchTasksByDateAndWo(dateRange,worOrders)).thenReturn(taskList);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		Mockito.when(p6wsClient.removeExecutionPackage(Mockito.anyList())).thenReturn(true);
		Mockito.when(workOrderDAO.saveTask(Mockito.any())).thenReturn(task);
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		p6SchedulingBusinessService.updateTasksAndExecutionPackageInP6AndDB();
		Assert.assertNotNull(workOrders);
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
	 * Update todos Saving newly created work order with todos
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
	public void testSaveToDo1() throws P6BusinessException {
		thrown.expect(IllegalArgumentException.class);
		p6SchedulingBusinessService.saveToDo(null);
	}

	/**
	 * If the work order newly created in p6 and it is not available in portal
	 * database
	 * 
	 * @throws P6BusinessException
	 */

	@Test
	@Rollback(true)
	public void testSearch() throws P6BusinessException {
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-04-28T00:00:00.000Z");

		List<WorkOrder> searchResult = new ArrayList<>();
		WorkOrder workOrder = new WorkOrder();

		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");
		searchResult.add(workOrder);

		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-04-28");
		Mockito.when(dateUtils.convertDate(request.getFromDate())).thenReturn("2017-04-28");
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);

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
	@Rollback(true)
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
		workOrder.setScheduleDate("09/05/2017");
		searchResult.add(workOrder);

		workOrder = new WorkOrder();

		workOrderIds = new ArrayList<>();
		workOrderIds.add("W11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("09/05/2017");
		searchResult.add(workOrder);

		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-04-28");
		Mockito.when(dateUtils.convertDate(Mockito.any())).thenReturn("2017-05-09");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		try {
			Mockito.when(dateUtils.toDateFromDD_MM_YYYY(Mockito.any())).thenReturn(dateFormat.parse("2017-05-09"));
			Mockito.when(dateUtils.toDateFromYYYY_MM_DD(Mockito.any())).thenReturn(dateFormat.parse("2017-05-09"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);

		Task task = new Task();
		task.setTaskId("W11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(Mockito.any())).thenReturn(new Date());
		ExecutionPackage excPckg = new ExecutionPackage();
		excPckg.setExctnPckgId(123456L);
		excPckg.setExctnPckgNam("09-05-2017_064556556");
		task.setExecutionPackage(excPckg);
		Set<Task> tasks = new HashSet<Task>();
		tasks.add(task);
		excPckg.setTasks(tasks);
		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		Assert.assertNotNull(workOrders);
		for (WorkOrder _workOrder : workOrders) {
			Assert.assertEquals("W11", _workOrder.getWorkOrders().get(0));
			Assert.assertEquals("CRW1", _workOrder.getCrewNames());
			Assert.assertEquals("09-05-2017_064556556", _workOrder.getExctnPckgName());
		}

	}
	@Test
	@Rollback(true)
	public void testSearch_WithoutCrew() throws P6BusinessException {
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-06-14T00:00:00.000Z");
		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("W11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("09/05/2017");
		searchResult.add(workOrder);

		workOrder = new WorkOrder();

		workOrderIds = new ArrayList<>();
		workOrderIds.add("W11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("09/05/2017");
		searchResult.add(workOrder);

		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-06-14");
		Mockito.when(dateUtils.convertDate(Mockito.any())).thenReturn("2017-05-09");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
		try {
			Mockito.when(dateUtils.toDateFromDD_MM_YYYY(Mockito.any())).thenReturn(dateFormat.parse("2017-05-09"));
			Mockito.when(dateUtils.toDateFromYYYY_MM_DD(Mockito.any())).thenReturn(dateFormat.parse("2017-05-09"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);

		Task task = new Task();
		task.setTaskId("W11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(Mockito.any())).thenReturn(new Date());
		ExecutionPackage excPckg = new ExecutionPackage();
		excPckg.setExctnPckgId(123456L);
		excPckg.setExctnPckgNam("09-05-2017_064556556");
		task.setExecutionPackage(excPckg);
		Set<Task> tasks = new HashSet<Task>();
		tasks.add(task);
		excPckg.setTasks(tasks);
		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		Assert.assertNotNull(workOrders);
		for (WorkOrder _workOrder : workOrders) {
			Assert.assertEquals("W11", _workOrder.getWorkOrders().get(0));
			Assert.assertEquals("CRW1", _workOrder.getCrewNames());
			Assert.assertEquals("09-05-2017_064556556", _workOrder.getExctnPckgName());
		}

	}

	/**
	 * If the work order exist in p6 and it is available in portal database but
	 * work order is not associated with execution package
	 * 
	 * @throws P6BusinessException
	 */

	@Test
	@Rollback(true)
	public void testSearch_2() throws P6BusinessException {
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-05-19'T'00:00:00.000Z");

		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("W11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("19/05/2017");
		searchResult.add(workOrder);

		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-05-19");
		Mockito.when(dateUtils.convertDate(request.getFromDate())).thenReturn("2017-05-19");
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);

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
		for (WorkOrder _workOrder : workOrders) {
			Assert.assertEquals("W11", _workOrder.getWorkOrders().get(0));
			Assert.assertEquals("CRW1", _workOrder.getCrewNames());
			Assert.assertEquals("", _workOrder.getExctnPckgName());
		}

	}
	@Test
	@Rollback(true)
	public void testSearchAndSyncP6AndPortal() throws P6BusinessException{
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		List<String> crewList = new ArrayList<String>();
		crewList.add("CRW1");
		request.setFromDate("2017-05-19'T'00:00:00.000Z");
		request.setCrewList(crewList);
		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("19/05/2017");
		workOrder.setExctnPckgName("1234567890");
		workOrder.setLeadCrew("CRW1");
		WorkOrder workOrder2 = new WorkOrder();

		List<String> workOrderIds2 = new ArrayList<>();
		workOrderIds2.add("14");
		workOrder2.setWorkOrders(workOrderIds2);
		workOrder2.setWorkOrderId("14");
		workOrder2.setCrewNames("CRW1");
		workOrder2.setScheduleDate("19/05/2017");
		searchResult.add(workOrder2);
		searchResult.add(workOrder);
		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-05-19");
		Mockito.when(dateUtils.convertDate(request.getFromDate())).thenReturn("2017-05-19");
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);
		
		Set<Task> tasks = new HashSet<Task>();
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setActioned("Y");
		executionPackage.setExctnPckgId(1234);
		executionPackage.setExctnPckgNam("1234567890");
		executionPackage.setLeadCrewId("CRW1");
		executionPackage.setTasks(tasks);
		List<Task> taskList = new ArrayList<Task>();
		Task task = new Task();
		task.setTaskId("11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
		task.setExecutionPackage(executionPackage);
		Set<TodoAssignment> todoAssignments = new HashSet<TodoAssignment>();
		TodoAssignmentPK todoAssignMentPK = new TodoAssignmentPK();
		todoAssignMentPK.setTask(task);
		todoAssignMentPK.setTodoId(new BigDecimal("123456789"));
		TodoAssignment todo = new TodoAssignment();
		todo.setTodoAssignMentPK(todoAssignMentPK);
		todo.setStat("Completed");
		task.setTodoAssignments(todoAssignments);
		Task task1 = new Task();
		task1.setTaskId("12");
		task1.setActioned("Y");
		task1.setCrewId("CRW2");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
		task1.setExecutionPackage(executionPackage);
		Task task2 = new Task();
		task2.setTaskId("13");
		task2.setActioned("Y");
		task2.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate())).thenReturn(new Date());
		task2.setSchdDt(new Date());
		task2.setExecutionPackage(executionPackage);
		Task task3 = new Task();
		task3.setTaskId("14");
		task3.setActioned("Y");
		task3.setCrewId("CRW1");
		task3.setSchdDt(dateUtils.toDateFromDD_MM_YYYY("18/05/2017"));
		tasks.add(task);
		taskList.add(task);
		tasks.add(task1);
		taskList.add(task1);
		tasks.add(task2);
		taskList.add(task2);
		tasks.add(task3);
		taskList.add(task3);
		List<Date> dateRange = new ArrayList<Date>();
		if (request != null) {
			dateRange.add(request.getFromDate() != null ? dateUtils.toDateFromYYYY_MM_DD(request.getFromDate()): null);
			dateRange.add(request.getToDate() != null ? dateUtils.toDateFromYYYY_MM_DD(request.getToDate()) : null);

		}
		List<String> worOrders = new ArrayList<String>();
		if (searchResult != null) {
			searchResult.forEach(wo->{
				worOrders.add(wo.getWorkOrderId());
			});

		}
		Mockito.when(workOrderDAO.fetchTasksByDateAndWo(dateRange,worOrders)).thenReturn(taskList);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		Mockito.when(p6wsClient.removeExecutionPackage(Mockito.anyList())).thenReturn(true);
		Mockito.when(workOrderDAO.saveTask(Mockito.any())).thenReturn(task);
		List<WorkOrder> workOrders = p6SchedulingBusinessService.search(request);
		p6SchedulingBusinessService.updateTasksAndExecutionPackageInP6AndDB();
		Assert.assertNotNull(workOrders);
	}
	@Test
	public void testFetchMetadata () throws P6BusinessException{
		List<TodoTemplate> toDoTemplateList = new ArrayList<>();
		TodoTemplate toDo = new TodoTemplate();
		toDo.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		toDo.setCrtdUsr("Test user");
		toDo.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		toDo.setLstUpdtdUsr("Test user1");
		toDo.setTmpltDesc("test template desc");
		toDo.setTmpltId(1);
		toDo.setTypId(new BigDecimal(0.1));
		toDo.setTodoId(1);
		toDoTemplateList.add(toDo);
		
		Mockito.when(todoDAO.fetchAllToDos()).thenReturn(toDoTemplateList);
		
		Map<String, List<String>> depotCrewMap = new HashMap<String, List<String>>();
		List<String> crews = new ArrayList<String>();
		crews.add("BUNT04");
		crews.add("BUNT05");
		depotCrewMap.put("Picton", crews);
		
		Mockito.when(resourceDetailDAO.fetchAllResourceDetail()).thenReturn(depotCrewMap);

		MetadataDTO metadataDTO=  p6SchedulingBusinessService.fetchMetadata();
		List<ToDoItem> toDos = metadataDTO.getToDoItems();
		ResourceDTO resourceDTO = metadataDTO.getResourceDTO();
		
		Assert.assertNotNull(toDos);
		Assert.assertNotNull(resourceDTO);
		
		for ( ToDoItem item : toDos) {
			Assert.assertEquals(toDo.getCrtdUsr(),item.getCrtdUsr());
			Assert.assertEquals(toDo.getLstUpdtdUsr(), item.getLstUpdtdUsr());
			Assert.assertEquals(toDo.getTmpltDesc(), item.getTmpltDesc());
			//Assert.assertEquals(String.valueOf(toDo.getTmpltId()), item.getTmpltId());
			
			Assert.assertEquals(toDo.getTodoNam(), item.getToDoName());
			Assert.assertEquals(toDo.getCrtdTs().toString(), item.getCrtdTs());
			
		}

		Set<String> keys = depotCrewMap.keySet();
		for(String depot : keys){
			Assert.assertNotNull(depotCrewMap.get(depot));
		}
	}
	
	@Test
	public void testFetchWorkOrdersForAddUpdateToDo () throws P6BusinessException {
		WorkOrderSearchRequest  request =  new WorkOrderSearchRequest();
		request.setExecPckgName("13-05-2017_123456");
		
		ExecutionPackage execPckg = new ExecutionPackage();
		execPckg.setExctnPckgNam(request.getExecPckgName());
		
		
		Set<Task> tasks = new HashSet<>();
		Task task = new Task();
		task.setTaskId("WO11");
		task.setCrewId("MOST1");
		task.setSchdDt(new Date());
		task.setExecutionPackage(execPckg);
		Set<TodoAssignment> todoAssignments = new HashSet<>();
		TodoAssignment todo = new TodoAssignment();
		todo.getTodoAssignMentPK().setTask(task);
		todo.getTodoAssignMentPK().setTodoId(new BigDecimal("1"));
		todoAssignments.add(todo);
		task.setTodoAssignments(todoAssignments);
		tasks.add(task);
		task = new Task();
		task.setTaskId("WO12");
		task.setCrewId("MOST2");
		task.setSchdDt(new Date());
		task.setExecutionPackage(execPckg);
		todoAssignments = new HashSet<>();
		todo = new TodoAssignment();
		todo.getTodoAssignMentPK().setTask(task);
		todo.getTodoAssignMentPK().setTodoId(new BigDecimal("1"));
		todoAssignments.add(todo);
		task.setTodoAssignments(todoAssignments);
		tasks.add(task);
		
		execPckg.setTasks(tasks);
		
		Mockito.when(todoDAO.getToDoName(1L)).thenReturn("ESA");
		Mockito.when(executionPackageDao.fetch(request.getExecPckgName())).thenReturn(execPckg);
		
		List<WorkOrder> workOrders = p6SchedulingBusinessService.fetchWorkOrdersForAddUpdateToDo(request);
		
		Assert.assertNotNull(workOrders);
		
		for ( WorkOrder workOrder : workOrders) {
			Assert.assertTrue(workOrder.getCrewNames().contains("MOST1"));
			Assert.assertTrue(workOrder.getCrewNames().contains("MOST2"));
			Assert.assertEquals("13-05-2017_123456", workOrder.getExctnPckgName());
			for (String wo : workOrder.getWorkOrders())
				Assert.assertNotNull(wo);
		}
	}
	
	@Test
	public void testFetchWorkOrdersForViewToDoStatus () throws P6BusinessException {
		WorkOrderSearchRequest  request =  new WorkOrderSearchRequest();
		request.setExecPckgName("13-05-2017_123456");
		
		ExecutionPackage execPckg = new ExecutionPackage();
		execPckg.setExctnPckgNam(request.getExecPckgName());
		
		
		Set<Task> tasks = new HashSet<>();
		Task task = new Task();
		task.setTaskId("WO11");
		task.setCrewId("MOST1");
		task.setSchdDt(new Date());
		task.setExecutionPackage(execPckg);
		Set<TodoAssignment> todoAssignments = new HashSet<>();
		TodoAssignment todo = new TodoAssignment();
		todo.getTodoAssignMentPK().setTask(task);
		todo.getTodoAssignMentPK().setTodoId(new BigDecimal("1"));
		todoAssignments.add(todo);
		task.setTodoAssignments(todoAssignments);
		tasks.add(task);
		task = new Task();
		task.setTaskId("WO12");
		task.setCrewId("MOST2");
		task.setSchdDt(new Date());
		task.setExecutionPackage(execPckg);
		todoAssignments = new HashSet<>();
		todo = new TodoAssignment();
		todo.getTodoAssignMentPK().setTask(task);
		todo.getTodoAssignMentPK().setTodoId(new BigDecimal("1"));
		todoAssignments.add(todo);
		task.setTodoAssignments(todoAssignments);
		tasks.add(task);
		
		execPckg.setTasks(tasks);
		
		Mockito.when(todoDAO.getToDoName(1L)).thenReturn("ESA");
		Mockito.when(executionPackageDao.fetch(request.getExecPckgName())).thenReturn(execPckg);
		
		p6SchedulingBusinessService.fetchWorkOrdersForViewToDoStatus(request);
		
	}
	
	/**
	 * retrieve work orders from P6
	 * 
	 * @throws P6BusinessException
	 */

	@Test
	@Rollback(true)
	public void testRetrieveWorkOrders() throws P6BusinessException {
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-05-19'T'00:00:00.000Z");

		List<WorkOrder> searchResult = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("W11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("W11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("19/05/2017");
		searchResult.add(workOrder);

		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		searchRequest.setPlannedStartDate("2017-05-19");
		Mockito.when(dateUtils.convertDate(request.getFromDate())).thenReturn("2017-05-19");
		Mockito.when(p6wsClient.searchWorkOrder(searchRequest)).thenReturn(searchResult);

		Task task = new Task();
		task.setTaskId("W11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		Mockito.when(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate())).thenReturn(new Date());
		task.setSchdDt(new Date());
		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(userTokenRequest.getUserPrincipal()).thenReturn("test user");
		List<WorkOrder> workOrders = p6SchedulingBusinessService.retrieveWorkOrders(request);
		Assert.assertNotNull(workOrders);
		for (WorkOrder _workOrder : workOrders) {
			Assert.assertEquals("W11", _workOrder.getWorkOrders().get(0));
			Assert.assertEquals("CRW1", _workOrder.getCrewNames());
			Assert.assertEquals(null, _workOrder.getExctnPckgName());
		}

	}
	
	@Test
	@Rollback(true)
	public void testSaveViewToDoStatus() throws P6BusinessException {
		
		ExecutionPackage excPckg = new ExecutionPackage();
		excPckg.setExctnPckgId(123456L);
		excPckg.setExctnPckgNam("28-04-2017_122345");
		WorkOrder workOrder = new WorkOrder();

		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("WO11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("WO11");
		workOrder.setCrewNames("CRW1");
		workOrder.setScheduleDate("28/04/2017");
		workOrder.setExctnPckgName("28-04-2017_122345");

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
		task.setExecutionPackage(excPckg);
		Set<Task> tasks = new HashSet();
		tasks.add(task);
		excPckg.setTasks(tasks);
		TodoAssignment todoAssignment = new TodoAssignment();
		todoAssignment.getTodoAssignMentPK().setTask(task);
		todoAssignment.getTodoAssignMentPK().setTodoId(new BigDecimal(2));
		Set<TodoAssignment> todos = new HashSet<>();
		task.setTodoAssignments(todos);

		Mockito.when(workOrderDAO.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(todoDAO.getToDoId("ESA")).thenReturn(new BigDecimal(1));
		Mockito.when(executionPackageDao.fetch(workOrder.getExctnPckgName())).thenReturn(excPckg);
		
		ViewToDoStatus viewToDoStatus = new ViewToDoStatus();
		viewToDoStatus.setExctnPckgName("28-04-2017_122345");
		viewToDoStatus.setWorkOrders(workOrderIds);
		ViewToDoStatus output = p6SchedulingBusinessService.saveViewToDoStatus(viewToDoStatus);

		Assert.assertNotNull(output);
		

	}

}
