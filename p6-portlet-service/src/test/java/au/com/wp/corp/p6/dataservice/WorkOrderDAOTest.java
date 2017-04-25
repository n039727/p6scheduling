/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl;
import au.com.wp.corp.p6.dto.WorkOrderSearchInput;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * Performs unit test cases for WorkOrderDAO
 * 
 * @author N039126
 * @version 1.0
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class WorkOrderDAOTest {

	@Autowired
	WorkOrderDAOImpl workOrderDAO;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * pre-requisite to perform unit test cases for WorkOrderDAO. It initializes
	 * all dependency objects
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * destory all the objects after test case execution
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl#fetchWorkOrdersForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	@Rollback(true)
	@Test
	public void testFetchWorkOrdersForViewToDoStatus() {
		List<Task> tasks = null;
		WorkOrderSearchInput input = new WorkOrderSearchInput();
		input.setWorkOrderId("Y6UIOP67");

		tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(input);
		for (Task task : tasks) {
			assertEquals("Y6UIOP67", task.getTaskId());
			assertNotNull(task.getCmts());
		}
	}

}
