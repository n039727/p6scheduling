/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
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

import au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.test.config.AppConfig;
/**
 * @author N039603
 *
 */
@ContextConfiguration(loader=AnnotationConfigContextLoader.class, classes={AppConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class WorkOrderDAOTest {

	@InjectMocks
	WorkOrderDAOImpl workOrderDAO;
	
		
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
	 * Test method for {@link au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl#fetchWorkOrdersForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	@Test
	public void testFetchWorkOrdersForViewToDoStatus() {
		List<Task> tasks = new ArrayList<Task>();
		Set<TodoAssignment> assignments = new HashSet<TodoAssignment>();
		Task task = new Task();
		task.setTaskId("Y6UIOP67");
		tasks.add(task);
		TodoAssignment assignment = new TodoAssignment();
		assignment.setCrtdUsr("N039603");
		assignments.add(assignment);
		task.setTodoAssignments(assignments);
		WorkOrderSearchInput input = new WorkOrderSearchInput();
		input.setWorkOrderId("Y6UIOP67");
		
		Mockito.when(workOrderDAO.fetchWorkOrdersForViewToDoStatus(input)).thenReturn(tasks);
		assertEquals("Y6UIOP67", tasks.get(0).getTaskId());
	}

}
