/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoAssignmentPK;
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
public class WorkOrderDAOIntegrationTest {

	@Autowired
	WorkOrderDAO workOrderDAO;

	@Autowired
	ExecutionPackageDao executionPackageDao;

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
	 * {@link au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl#fetchWorkOrdersForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchRequest)}.
	 */
	@Transactional
	@Rollback(true)
	@Test
	public void testFetchWorkOrdersForViewToDoStatus() {
		List<Task> tasks = null;
		WorkOrderSearchRequest input = new WorkOrderSearchRequest();
		input.setWorkOrderId("ABCD");
		tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(input);
		assertTrue(tasks.isEmpty());
	}

	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl#saveTask(au.com.wp.corp.p6.model.Task)}.
	 * 
	 * @throws P6DataAccessException
	 */
	@Transactional
	@Rollback(true)
	@Test
	public void testSaveTask() throws P6DataAccessException {
		Task dbTask = new Task();
		dbTask = prepareTaskBean(dbTask);
		Task createdTask = null;
		createdTask = workOrderDAO.saveTask(dbTask);
		Assert.assertNotNull(createdTask);
		Assert.assertEquals(dbTask.getTaskId(), createdTask.getTaskId());

	}

	private Task prepareTaskBean(Task dbTask) {

		dbTask.setTaskId("JunitTest");
		dbTask.setCmts("Test cmt");
		dbTask.setCrewId("TestCrew");
		dbTask.setLeadCrewId("TestLeadCrew");
		java.util.Date scheduleDate = new java.util.Date();
		dbTask.setSchdDt(scheduleDate);
		dbTask.setDepotId("TestDeport");
		dbTask.setMatrlReqRef("TestMatrReqRef");
		long currentTime = System.currentTimeMillis();
		// dbTask.setCrtdTs(new Timestamp(currentTime));
		dbTask.setCrtdUsr("Test");
		dbTask.setLstUpdtdTs(new Timestamp(currentTime));
		dbTask.setLstUpdtdUsr("Test");
		dbTask.setExecutionPackage(executionPackageDao.fetch("08-05-2017_092351551"));
		dbTask.setActioned("Y");
		TodoAssignment todoAssignment = new TodoAssignment();
		TodoAssignmentPK todoAssignmentPK = new TodoAssignmentPK();
		todoAssignmentPK.setTask(dbTask);
		todoAssignmentPK.setTodoId(new BigDecimal("1"));
		todoAssignment.setTodoAssignMentPK(todoAssignmentPK);
		Set<TodoAssignment> todoAssignments = new HashSet<>();
		todoAssignments.add(todoAssignment);
		dbTask.setTodoAssignments(todoAssignments);

		return dbTask;
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testFetch() throws P6DataAccessException {
		Task task = workOrderDAO.fetch("WO11");
		Assert.assertNull(task);
	}

}
