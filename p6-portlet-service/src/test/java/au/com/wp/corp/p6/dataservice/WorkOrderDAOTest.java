/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.After;
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
	 * {@link au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl#fetchWorkOrdersForViewToDoStatus(au.com.wp.corp.p6.dto.WorkOrderSearchInput)}.
	 */
	@Rollback(true)
	@Test
	public void testFetchWorkOrdersForViewToDoStatus() {
		List<Task> tasks = null;
		WorkOrderSearchInput input = new WorkOrderSearchInput();
		input.setWorkOrderId("ABCD");
		tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(input);
		assertTrue(tasks.isEmpty());
	}
	
	/**
	 * Test method for
	 * {@link au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl#saveTask(au.com.wp.corp.p6.model.Task)}.
	 */
	@Rollback(true)
	@Test
	public void testSaveTask() {
		Task dbTask = new Task();
		dbTask = prepareTaskBean(dbTask);
		Task createdTask = workOrderDAO.saveTask(dbTask);	
		assertEquals(dbTask.getTaskId(), createdTask.getTaskId());
		
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
		dbTask.setCrtdTs(new Timestamp(currentTime));
		dbTask.setCrtdUsr("Test"); //TODO update the user name here
		dbTask.setLstUpdtdTs(new Timestamp(currentTime));
		dbTask.setLstUpdtdUsr("Test");
		//TODO will remove after DB constrain change
		dbTask.setExecutionPackage(executionPackageDao.fetch("PKG1" ));
		
		return dbTask;
	}
	
	private  String getCurrentDateTimeMS() {
		java.util.Date dNow = new java.util.Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy-hhmmssMs");
        String datetime = ft.format(dNow);
        return datetime;
    }

}
