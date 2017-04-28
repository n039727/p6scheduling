/**
 * 
 */
package au.com.wp.corp.p6.bussiness;

import java.util.ArrayList;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.impl.ExecutionPackageServiceImpl;
import au.com.wp.corp.p6.dataservice.impl.ExecutionPackageDaoImpl;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author n039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ExecutionPackageServiceTest {

	@Autowired
	@InjectMocks
	ExecutionPackageServiceImpl execPckgService;
	
	@Mock
	ExecutionPackageDaoImpl executionPckgDao;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * pre-requisite to perform unit test cases for ExecutionPackageService. It initializes
	 * all dependency objects
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * destroy all the objects after test case execution
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Transactional
	@Rollback(true)
	@Test
	public void testCreateOrUpdateExecutionPackage () throws P6BusinessException {
		ExecutionPackageDTO execPckg = new ExecutionPackageDTO();
		execPckg.setLeadCrew("MOST1");
		List<WorkOrder> workOrders = new ArrayList<>();
		Set<Task> tasks = new HashSet<>();
		WorkOrder workOrder =  null;
		for ( int i= 0; i < 10 ; i++){
			workOrder = new WorkOrder();
			workOrder.setWorkOrderId("WO123"+i);
			workOrders.add(workOrder);
			
			Task task = new Task();
			task.setTaskId("WO123"+i);
			tasks.add(task);
			Mockito.when(executionPckgDao.getTaskbyId("WO123"+i)).thenReturn(task);
		}
		execPckg.setWorkOrders(workOrders);
		ExecutionPackage execPackage =  new ExecutionPackage();
		execPackage.setTasks(tasks);
		
		Mockito.when(executionPckgDao.createOrUpdateExecPackage(execPackage)).thenReturn(true);
		execPckg = execPckgService.createOrUpdateExecutionPackage(execPckg, "Test User");
		
		Assert.assertNotNull(execPckg);
		
	}

}
