package au.com.wp.corp.p6.dataservice;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
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
public class ExecutionPackageDaoTest {
	
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

	@Transactional
	@Rollback(true)
	@Test
	public void testSaveExecutionPackage() {
		ExecutionPackageDTO executionPackageDTO = new ExecutionPackageDTO();
		String name = getCurrentDateTimeMS();
		executionPackageDTO.setExctnPckgName(name);
		executionPackageDTO = executionPackageDao.saveExecutionPackage(executionPackageDTO);
		assertEquals(executionPackageDTO.getExctnPckgName(), name);
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




}
