/**
 * 
 */
package au.com.wp.corp.p6.bussiness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.scripting.xmltags.WhereSqlNode;
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
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.impl.ExecutionPackageServiceImpl;
import au.com.wp.corp.p6.dataservice.impl.ExecutionPackageDaoImpl;
import au.com.wp.corp.p6.dataservice.impl.WorkOrderDAOImpl;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.mock.CreateP6MockData;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.utils.DateUtils;

/**
 * @author n039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ExecutionPackageServiceTest {

	@InjectMocks
	ExecutionPackageServiceImpl execPckgService;

	@Mock
	ExecutionPackageDaoImpl executionPckgDao;
	
	@Mock
	WorkOrderDAOImpl workOrderDao;

	@Mock
	DateUtils dateUtils;
	
	@Mock
	CreateP6MockData mockData;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	/**
	 * pre-requisite to perform unit test cases for ExecutionPackageService. It
	 * initializes all dependency objects
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

	/**
	 * 
	 * tests create or update execution package.
	 * 
	 * @throws P6BusinessException
	 */
	@Transactional
	@Rollback(true)
	@Test
	public void testCreateOrUpdateExecutionPackage() throws P6BusinessException {
		ExecutionPackageDTO execPckg = new ExecutionPackageDTO();
		execPckg.setLeadCrew("MOST1");
		List<WorkOrder> workOrders = new ArrayList<>();
		Set<Task> tasks = new HashSet<>();
		WorkOrder workOrder = null;
		workOrder = new WorkOrder();
		workOrder.setWorkOrderId("WO1231");
		workOrder.setCrewNames("CREW1");
		workOrders.add(workOrder);

		ExecutionPackage excPckg = new ExecutionPackage();
		execPckg.setExctnPckgName("06-05-2017_1643493");
		excPckg.setTasks(tasks);
		
		
		Task task = new Task();
		task.setTaskId("WO1231");
		task.setCrewId("CREW1");
		task.setExecutionPackage(excPckg);
		tasks.add(task);
		Mockito.when(workOrderDao.fetch("WO1231")).thenReturn(task);
		execPckg.setWorkOrders(workOrders);
		ExecutionPackage execPackage = new ExecutionPackage();
		execPackage.setTasks(tasks);
		Mockito.when(dateUtils.getCurrentDateWithTimeStamp()).thenReturn("12345678");
		Mockito.when(executionPckgDao.createOrUpdateExecPackage(execPackage)).thenReturn(true);
		execPckg = execPckgService.createOrUpdateExecutionPackage(execPckg, "Test User");

		Assert.assertNotNull(execPckg);

	}
	
	/**
	 * 
	 * tests create or update execution package.
	 * 
	 * @throws P6BusinessException
	 */
	@Transactional
	@Rollback(true)
	@Test
	public void testCreateOrUpdateExecutionPackage1() throws P6BusinessException {
		ExecutionPackageDTO execPckg = new ExecutionPackageDTO();
		execPckg.setLeadCrew("MOST1");
		List<WorkOrder> workOrders = new ArrayList<>();
		Set<Task> tasks = new HashSet<>();
		WorkOrder workOrder = null;
		workOrder = new WorkOrder();
		workOrder.setWorkOrderId("WO1231");
		workOrder.setCrewNames("CREW1");
		workOrders.add(workOrder);

		ExecutionPackage excPckg = new ExecutionPackage();
		execPckg.setExctnPckgName("06-05-2017_1643493");
		excPckg.setTasks(tasks);
		
		
		Task task = new Task();
		task.setTaskId("WO1231");
		task.setCrewId("CREW1");
		task.setExecutionPackage(excPckg);
		tasks.add(task);
		Mockito.when(workOrderDao.fetch("WO1231")).thenReturn(null);
		execPckg.setWorkOrders(workOrders);
		ExecutionPackage execPackage = new ExecutionPackage();
		execPackage.setTasks(tasks);
		Mockito.when(dateUtils.getCurrentDateWithTimeStamp()).thenReturn("12345678");
		Mockito.when(executionPckgDao.createOrUpdateExecPackage(execPackage)).thenReturn(true);
		execPckg = execPckgService.createOrUpdateExecutionPackage(execPckg, "Test User");

		Assert.assertNotNull(execPckg);

	}

	/**
	 * 
	 * tests create or update execution package - error scenario -1 # getting
	 * exception while calling getTask By id .
	 * 
	 * @throws P6BusinessException
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	@Rollback(true)
	@Test
	public void testCreateOrUpdateExecutionPackage_Error1() throws P6BusinessException {
		thrown.expect(P6DataAccessException.class);
		ExecutionPackageDTO execPckg = new ExecutionPackageDTO();
		execPckg.setLeadCrew("MOST1");
		List<WorkOrder> workOrders = new ArrayList<>();
		Set<Task> tasks = new HashSet<>();
		WorkOrder workOrder = null;
		for (int i = 0; i < 10; i++) {
			workOrder = new WorkOrder();
			workOrder.setWorkOrderId("WO123" + i);
			workOrder.setCrewNames("CREW"+i);
			workOrders.add(workOrder);

			Task task = new Task();
			task.setTaskId("WO123" + i);
			task.setCrewId("CREW"+i);
			tasks.add(task);
			Mockito.when(workOrderDao.fetch("WO123" + i)).thenThrow(P6DataAccessException.class);
		}
		execPckg.setWorkOrders(workOrders);
		ExecutionPackage execPackage = new ExecutionPackage();
		execPackage.setTasks(tasks);

		Mockito.when(executionPckgDao.createOrUpdateExecPackage(execPackage)).thenReturn(true);
		execPckg = execPckgService.createOrUpdateExecutionPackage(execPckg, "Test User");

		Assert.assertNotNull(execPckg);

	}

	@Transactional
	@Rollback(true)
	@Test
	public void testsearchByExecutionPackage  ( ) throws P6DataAccessException{
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		List<String> crewList = new ArrayList<>();
		crewList.add("MOST1");
		request.setCrewList(crewList);
		request.setFromDate("");
		WorkOrder workOrder = new WorkOrder();
		List<String> wos = new ArrayList<>();
		wos.add("WO11");
		workOrder.setWorkOrders(wos);
		List<WorkOrder> _workOrders = new ArrayList<>();
		_workOrders.add(workOrder);
		Mockito.when(mockData.search(request)).thenReturn(_workOrders);
		Task task = new Task();
		ExecutionPackage excPckg = new ExecutionPackage();
		excPckg.setExctnPckgId(12734L);
		excPckg.setExctnPckgNam("06-05-2017_128383131");
		task.setExecutionPackage(excPckg);
		Mockito.when(workOrderDao.fetch("WO11")).thenReturn(task);
		List<WorkOrder> workOrders = execPckgService.searchByExecutionPackage(request);
	}
}
