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

import au.com.wp.corp.p6.businessservice.impl.DepotTodoServiceImpl;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6DataAccessException;
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
public class DepotTodoServiceTest {
	
	@InjectMocks
	DepotTodoServiceImpl depotTodoService;

	
	@Mock
	WorkOrderDAOImpl workOrderDao;

	@Mock
	DateUtils dateUtils;
	
	@Mock
	TodoDAO todoDAO;
	
	@Mock
	ExecutionPackageDao executionPackageDao;
	
	
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
	 * Test method for {@link au.com.wp.corp.p6.businessservice.DepotTodoService#fetchDepotTaskForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchRequest)}.
	 * @throws P6BusinessException 
	 */
	@Test
	public void testFetchDepotTaskForViewToDoStatus() throws P6BusinessException {

		WorkOrderSearchRequest  request =  new WorkOrderSearchRequest();
		request.setExecPckgName("15-05-2017_05590757");
		
		ExecutionPackage execPckg = new ExecutionPackage();
		execPckg.setExctnPckgNam(request.getExecPckgName());
		
		
		Set<Task> tasks = new HashSet<>();
		Task task = new Task();
		task.setTaskId("05162583002");
		task.setCrewId("MOMT4");
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
		task.setTaskId("05195042002");
		task.setCrewId("MOST7");
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
		
		ViewToDoStatus viewToDoStatus = depotTodoService.fetchDepotTaskForViewToDoStatus(request);
		Assert.assertNotNull(viewToDoStatus);
	}

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.DepotTodoService#UpdateDepotToDo(au.com.wp.corp.p6.dto.ViewToDoStatus)}.
	 */
	@Test
	@Rollback(true)
	public void testUpdateDepotToDo() throws P6BusinessException{

		ViewToDoStatus viewToDoStatus = new ViewToDoStatus();
		
		ExecutionPackage excPckg = new ExecutionPackage();
		viewToDoStatus.setExctnPckgName("15-05-2017_05590757");
		
		WorkOrder workOrder = new WorkOrder();
		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("05162583002");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("05162583002");
		workOrder.setCrewNames("MOMT4");
		workOrder.setExctnPckgName("15-05-2017_05590757");
		
		Set<Task> tasks = new HashSet<>();
		Task task = new Task();
		task.setTaskId("05162583002");
		task.setCrewId("MOMT4");
		task.setSchdDt(new Date());
		task.setExecutionPackage(excPckg);
		tasks.add(task);
		excPckg.setTasks(tasks);
		
		Set<TodoAssignment> todoAssignments = new HashSet<>();
		TodoAssignment todo = new TodoAssignment();
		
		todoAssignments.add(todo);
		task.setTodoAssignments(todoAssignments);
		
		au.com.wp.corp.p6.dto.ToDoAssignment assignmentDto = new au.com.wp.corp.p6.dto.ToDoAssignment();
		
		List<au.com.wp.corp.p6.dto.ToDoAssignment> assignments = new ArrayList<au.com.wp.corp.p6.dto.ToDoAssignment>();
		assignments.add(assignmentDto);
		viewToDoStatus.setTodoAssignments(assignments);
		
		Mockito.when(executionPackageDao.fetch(viewToDoStatus.getExctnPckgName())).thenReturn(excPckg);
		Mockito.when(workOrderDao.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(todoDAO.getToDoId("ESA")).thenReturn(new BigDecimal(1));
		ViewToDoStatus outPutToDoStatus = depotTodoService.UpdateDepotToDo(viewToDoStatus);

		Assert.assertNotNull(outPutToDoStatus);
		
		
	}
	
	/**
	 * Add todos Saving newly created work order with todos
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

		Mockito.when(workOrderDao.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(todoDAO.getToDoId("ESA")).thenReturn(new BigDecimal(1));
		WorkOrder outputWorkOrder = depotTodoService.saveDepotToDo(workOrder);

		Assert.assertNotNull(outputWorkOrder);
		Assert.assertEquals(workOrder.getCrewNames(), outputWorkOrder.getCrewNames());
		Assert.assertEquals(workOrder.getWorkOrderId(), outputWorkOrder.getWorkOrderId());

		for (ToDoItem todo : outputWorkOrder.getToDoItems()) {
			Assert.assertEquals("ESA", todo.getToDoName());
		}

	}
	
	/**
	 * Add todos Saving newly created work order with new todo
	 * 
	 * @throws P6BusinessException
	 */
	@Test
	//@Rollback(true)
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
		toDoItem.setToDoName("Test");
		toDoItem.setWorkOrders(workOrderIds);
		toDoItems.add(toDoItem);
		workOrder.setToDoItems(toDoItems);

		Task task = new Task();
		task.setTaskId("WO11");
		task.setActioned("Y");
		task.setCrewId("CRW1");

		Mockito.when(workOrderDao.fetch(workOrder.getWorkOrderId())).thenReturn(task);
		Mockito.when(todoDAO.getToDoId("ESA")).thenReturn(new BigDecimal(1));
		WorkOrder outputWorkOrder = depotTodoService.saveDepotToDo(workOrder);

		Assert.assertNotNull(outputWorkOrder);
		Assert.assertEquals(workOrder.getCrewNames(), outputWorkOrder.getCrewNames());
		Assert.assertEquals(workOrder.getWorkOrderId(), outputWorkOrder.getWorkOrderId());

		for (ToDoItem todo : outputWorkOrder.getToDoItems()) {
			Assert.assertEquals("Test", todo.getToDoName());
		}

	}
	
	@Test
	public void testFetchDepotTaskForAddUpdateToDo() throws P6BusinessException {

		WorkOrderSearchRequest  request =  new WorkOrderSearchRequest();
		request.setExecPckgName("15-05-2017_05590757");
		
		ExecutionPackage execPckg = new ExecutionPackage();
		execPckg.setExctnPckgNam(request.getExecPckgName());
		
		
		Set<Task> tasks = new HashSet<>();
		Task task = new Task();
		task.setTaskId("05162583002");
		task.setCrewId("MOMT4");
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
		task.setTaskId("05195042002");
		task.setCrewId("MOST7");
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
		
		List<WorkOrder> workOrders = depotTodoService.fetchDepotTaskForAddUpdateToDo(request);
		Assert.assertNotNull(workOrders);
	}

}
