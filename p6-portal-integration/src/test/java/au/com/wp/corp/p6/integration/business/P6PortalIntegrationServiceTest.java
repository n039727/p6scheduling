/**
 * 
 */
package au.com.wp.corp.p6.integration.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.integration.business.impl.P6PortalIntegrationServiceImpl;
import au.com.wp.corp.p6.integration.dao.P6PortalDAOImpl;
import au.com.wp.corp.p6.integration.dto.WorkOrder;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.util.DateUtils;
import au.com.wp.corp.p6.integration.wsclient.cleint.impl.P6WSClientImpl;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoAssignmentPK;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6PortalIntegrationServiceTest {
	@Mock
	P6PortalDAOImpl p6PortalDAO;

	@InjectMocks
	P6PortalIntegrationServiceImpl p6PortalIntegrationService;

	@Mock
	P6WSClientImpl p6WSClient;

	
	@Mock
	DateUtils dateUtils;
	
	@Mock
	DateUtil dateUtil;
	
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	List<String> workgroupList = null;
	Map<String, Integer> projWorkgroupDTOs = new HashMap<>();
	
	@Before
	public void setup() throws P6BusinessException {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Sync WorkOrder and Task between P6 and Portal
	 * @throws P6BusinessException 
	 * 
	 * 
	 */
	@Test
	public void testStart() throws P6BusinessException{
		List<Task> tasklist = prepareTaskList("18/05/2017","MOST1","1234567890");
		List<WorkOrder> woList = prepareWoList("18/05/2017", "MOST1", "1234567890");
		Mockito.when(p6PortalDAO.getALlTasks()).thenReturn(tasklist);
		Mockito.when(p6WSClient.readActivities(Mockito.anyList())).thenReturn(woList);
		Mockito.when(dateUtil.convertStringToDate((Mockito.anyString()))).thenReturn(new Date());
		Mockito.when(p6WSClient.logoutFromP6()).thenReturn(true);
		p6PortalIntegrationService.startPortalToP6Integration();
		
		
	}
	public List<Task> prepareTaskList(String scheduleDate, String crewNames, String exctnPckgName){
		Set<Task> tasks = new HashSet<Task>();
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setActioned("Y");
		executionPackage.setExctnPckgId(1234);
		executionPackage.setExctnPckgNam(exctnPckgName);
		executionPackage.setLeadCrewId("CRW1");
		executionPackage.setTasks(tasks);
		List<Task> taskList = new ArrayList<Task>();
		Task task = new Task();
		task.setTaskId("11");
		task.setActioned("Y");
		task.setCrewId("CRW1");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(scheduleDate));
		task.setExecutionPackage(executionPackage);
		Set<TodoAssignment> todoAssignments = new HashSet<TodoAssignment>();
		TodoAssignmentPK todoAssignMentPK = new TodoAssignmentPK();
		todoAssignMentPK.setTask(task);
		todoAssignMentPK.setTodoId(new BigDecimal("123456789"));
		TodoAssignment todo = new TodoAssignment();
		todo.setTodoAssignMentPK(todoAssignMentPK);
		todo.setStat("Completed");
		task.setTodoAssignments(todoAssignments);
		Task task1 = new Task();
		task1.setTaskId("12");
		task1.setActioned("Y");
		task1.setCrewId("CRW2");
		task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY("18/05/2017"));
		task1.setExecutionPackage(executionPackage);
		Task task2 = new Task();
		task2.setTaskId("13");
		task2.setActioned("Y");
		task2.setCrewId("CRW1");
		task2.setSchdDt(dateUtils.toDateFromDD_MM_YYYY("18/05/2017"));
		task2.setExecutionPackage(executionPackage);
		Task task3 = new Task();
		task3.setTaskId("14");
		task3.setActioned("Y");
		task3.setCrewId(crewNames);
		task3.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(scheduleDate));
		task3.setExecutionPackage(executionPackage);
		tasks.add(task);
		taskList.add(task);
		tasks.add(task1);
		taskList.add(task1);
		tasks.add(task2);
		taskList.add(task2);
		tasks.add(task3);
		taskList.add(task3);

		return taskList;
	}
	
	public List<WorkOrder> prepareWoList(String scheduleDate, String crewNames, String exctnPckgName){
		List<WorkOrder> woList = new ArrayList<>();

		WorkOrder workOrder = new WorkOrder();
		Map<String, List<String>> depotCrewMap = new HashMap<String, List<String>>();
		List<String> crewList = new ArrayList<>();
		crewList.add("MOST1");
		depotCrewMap.put("DEPOT1", crewList);
		List<String> workOrderIds = new ArrayList<>();
		workOrderIds.add("11");
		workOrder.setWorkOrders(workOrderIds);
		workOrder.setWorkOrderId("11");
		workOrder.setCrewNames(crewNames);
		workOrder.setDepotId("DEPOT1");
		workOrder.setScheduleDate("19/05/2017");
		workOrder.setExctnPckgName(exctnPckgName);
		workOrder.setLeadCrew("MOST1");
		WorkOrder workOrder2 = new WorkOrder();

		List<String> workOrderIds2 = new ArrayList<>();
		workOrderIds2.add("14");
		workOrder2.setWorkOrders(workOrderIds2);
		workOrder2.setWorkOrderId("14");
		workOrder2.setCrewNames(crewNames);
		workOrder2.setDepotId("DEPOT1");
		workOrder2.setScheduleDate(scheduleDate);
		workOrder2.setExctnPckgName(exctnPckgName);
		woList.add(workOrder2);
		woList.add(workOrder);
		return woList;
	}
	
	@After
	public void testClearApplicationMemory() {
		p6PortalIntegrationService.clearApplicationMemory();
	}
	@Test
	public void testSync_crewChange() throws P6BusinessException{
		List<Task> tasklist = prepareTaskList("18/05/2017","MOST1","1234567890");
		List<WorkOrder> woList = prepareWoList("18/05/2017", "CRW1", "1234567890");
		Mockito.when(p6PortalDAO.getALlTasks()).thenReturn(tasklist);
		Mockito.when(p6WSClient.readActivities(Mockito.anyList())).thenReturn(woList);
		Mockito.when(dateUtil.convertStringToDate((Mockito.anyString()))).thenReturn(new Date());
		Mockito.when(p6WSClient.logoutFromP6()).thenReturn(true);
		p6PortalIntegrationService.startPortalToP6Integration();
	}
	@Test
	public void testSync_DateChange() throws P6BusinessException{
		List<Task> tasklist = prepareTaskList("18/05/2017","MOST1","1234567890");
		List<WorkOrder> woList = prepareWoList("19/05/2017", "MOST1", "1234567890");
		Mockito.when(p6PortalDAO.getALlTasks()).thenReturn(tasklist);
		Mockito.when(p6WSClient.readActivities(Mockito.anyList())).thenReturn(woList);
		Mockito.when(dateUtil.convertStringToDate((Mockito.anyString()))).thenReturn(new Date());
		Mockito.when(p6WSClient.logoutFromP6()).thenReturn(true);
		p6PortalIntegrationService.startPortalToP6Integration();
	}
	@Test
	public void testSync_ExeckPackageChange() throws P6BusinessException{
		List<Task> tasklist = prepareTaskList("18/05/2017","MOST1","123456789");
		List<WorkOrder> woList = prepareWoList("18/05/2017", "MOST1", "1234567890");
		Mockito.when(p6PortalDAO.getALlTasks()).thenReturn(tasklist);
		Mockito.when(p6WSClient.readActivities(Mockito.anyList())).thenReturn(woList);
		Mockito.when(dateUtil.convertStringToDate((Mockito.anyString()))).thenReturn(new Date());
		Mockito.when(p6WSClient.logoutFromP6()).thenReturn(true);
		p6PortalIntegrationService.startPortalToP6Integration();
	}
}
