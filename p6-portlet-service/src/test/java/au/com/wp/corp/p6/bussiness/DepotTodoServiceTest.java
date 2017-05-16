/**
 * 
 */
package au.com.wp.corp.p6.bussiness;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.businessservice.impl.DepotTodoServiceImpl;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.impl.ExecutionPackageDaoImpl;
import au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
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
	ExecutionPackageDaoImpl executionPckgDao;
	
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
	 */
	@Test
	public void testFetchDepotTaskForViewToDoStatus() {

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
		
		depotTodoService.fetchDepotTaskForViewToDoStatus(request);
	}

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.DepotTodoService#UpdateDepotToDo(au.com.wp.corp.p6.dto.ViewToDoStatus)}.
	 */
	/*@Test
	public void testUpdateDepotToDo() {
		fail("Not yet implemented");
	}*/

}
