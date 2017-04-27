package au.com.wp.corp.p6.dataservice;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;

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

import au.com.wp.corp.p6.dataservice.impl.ExecutionPackageDaoImpl;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.test.config.AppConfig;

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

	/*@Rollback(true)
	@Test
	public void testFetch() {
		ExecutionPackageDTO executionPackageDTO = new ExecutionPackageDTO();
		String name = "No Name";
		//executionPackageDTO.setExctnPckgNam(name);
		//executionPackageDTO = executionPackageDao.saveExecutionPackage(executionPackageDTO);
		//ExecutionPackage executionPackage = executionPackageDao.fetch(name);
		assertNull(executionPackageDao.fetch(name));
		
	}*/

	@Rollback(true)
	@Test
	public void testSaveExecutionPackage() {
		ExecutionPackageDTO executionPackageDTO = new ExecutionPackageDTO();
		String name = getCurrentDateTimeMS();
		executionPackageDTO.setExctnPckgNam(name);
		executionPackageDTO = executionPackageDao.saveExecutionPackage(executionPackageDTO);
		assertEquals(executionPackageDTO.getExctnPckgNam(), name);
	}
	
	private  String getCurrentDateTimeMS() {
		java.util.Date dNow = new java.util.Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy-hhmmssMs");
        String datetime = ft.format(dNow);
        return datetime;
    }

}
