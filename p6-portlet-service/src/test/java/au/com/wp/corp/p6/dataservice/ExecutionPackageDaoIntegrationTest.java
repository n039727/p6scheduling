package au.com.wp.corp.p6.dataservice;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
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

import au.com.wp.corp.p6.dataservice.impl.ExecutionPackageDaoImpl;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoAssignmentPK;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * Test class to test ExecutionPackageDAO
 * 
 * @author n039126
 * @version 1.0
 * 
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ExecutionPackageDaoIntegrationTest {
	
	@Autowired
	ExecutionPackageDaoImpl executionPackageDao;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * pre-requisite to perform unit test cases for ExecutionPackageDao. It initializes
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
	@Rollback(true)
	@After
	public void tearDown() throws Exception {
	}

		
	private  String getCurrentDateTimeMS() {
		java.util.Date dNow = new java.util.Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy-hhmmssMs");
        String datetime = ft.format(dNow);
        return datetime;
    }
	
	/**
	 * test case to insert the execution package details into database
	 * 
	 * @throws P6DataAccessException
	 */
	@Test
	@Transactional
	@Rollback(true)
	public void testCreateOrUpdateExecPackage() throws P6DataAccessException {
		ExecutionPackage excPkg = new ExecutionPackage();

		excPkg.setExctnPckgNam("122344555");
		excPkg.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		excPkg.setCrtdUsr("test User");
		excPkg.setLeadCrewId("LEAD12");
		excPkg.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		excPkg.setLstUpdtdUsr("Test user 1");
		boolean status = executionPackageDao.createOrUpdateExecPackage(excPkg);
		Assert.assertTrue(status);

	}

	/**
	 * test case to fetch Task by task id
	 * 
	 * @throws P6DataAccessException
	 */
	@Transactional
	@Test
	public void testGetTaskbyId() throws P6DataAccessException {
		Task task = executionPackageDao.getTaskbyId("Y6UIOP97");
		if (task != null) {
			Assert.assertNotNull(task.getTaskId());
		}
	}


	@Test
	@Transactional
	@Rollback(true)
	public void testFetch() throws P6DataAccessException {
		ExecutionPackage executionPackage = executionPackageDao.fetch("08062017032033633");
		Assert.assertNotNull(executionPackage);
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testCreateOrUpdateTasks() throws P6DataAccessException {
		Task dbTask = new Task();
		dbTask = prepareTaskBean(dbTask);
		Set<Task> tasks = new HashSet<>();
		tasks.add(dbTask);
		boolean status = executionPackageDao.createOrUpdateTasks(tasks);
		Assert.assertEquals(status, Boolean.TRUE);
	}
	
	private Task prepareTaskBean(Task dbTask) {

		dbTask.setTaskId("JunitTest101");
		dbTask.setCmts("Test cmt");
		dbTask.setCrewId("TestCrew");
		dbTask.setLeadCrewId("TestLeadCrew");
		java.util.Date scheduleDate = new java.util.Date();
		dbTask.setSchdDt(scheduleDate);
		dbTask.setDepotId("TestDeport");
		dbTask.setMatrlReqRef("TestMatrReqRef");
		long currentTime = System.currentTimeMillis();
		dbTask.setCrtdUsr("Test");
		dbTask.setCrtdTs(new Timestamp(currentTime));
		dbTask.setLstUpdtdTs(new Timestamp(currentTime));
		dbTask.setLstUpdtdUsr("Test");
		dbTask.setExecutionPackage(executionPackageDao.fetch("08-05-2017_092351551"));
		dbTask.setActioned("Y");
		
		return dbTask;
	}
	
	@Test
	@Transactional
	@Rollback(true)
	public void testDeleteExecPackage() throws P6DataAccessException {
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setExctnPckgNam("08062017032033633");
		boolean status = executionPackageDao.deleteExecPackage(executionPackage);
		Assert.assertEquals(status, Boolean.TRUE);
	}

}
