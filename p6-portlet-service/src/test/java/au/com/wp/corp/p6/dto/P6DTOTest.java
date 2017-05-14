/**
 * 
 */
package au.com.wp.corp.p6.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * Test cases to verify the DTO objects.
 * 
 * @author n039126
 * @version 1.0
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6DTOTest {

	/**
	 * Test case to verify the DTO object {@link Crew}
	 */
	@Test
	public void testCrewDTO() {
		Crew crew = new Crew();
		crew.setCrewId("CRW1");
		crew.setCrewName("Test CRW");

		Assert.assertEquals("CRW1", crew.getCrewId());
		Assert.assertEquals("Test CRW", crew.getCrewName());
	}

	/**
	 * Test case to verify the DTO object {@link Depot}
	 */
	@Test
	public void testDepotDTO() {
		Crew crew = new Crew();
		crew.setCrewId("CRW1");
		crew.setCrewName("Test CRW");
		List<Crew> crews = new ArrayList<>();

		Depot depot = new Depot();
		depot.setCrews(crews);
		depot.setDepotId("DEP1");
		depot.setDepotName("Test Depot");

		Assert.assertEquals("DEP1", depot.getDepotId());
		Assert.assertEquals("Test Depot", depot.getDepotName());
		Assert.assertArrayEquals(crews.toArray(), depot.getCrews().toArray());
	}

	/**
	 * Test case to verify the DTO object {@link ErrorResponse}
	 */
	@Test
	public void testErrorResponseDTO() {
		ErrorResponse error = new ErrorResponse();
		error.setErrorCode("2001");
		error.setErrorMessage("Test ERROR");

		Assert.assertEquals("2001", error.getErrorCode());
		Assert.assertEquals("Test ERROR", error.getErrorMessage());
	}

	/**
	 * Test case to verify the DTO object {@link TaskDTO}
	 */
	@Test
	public void testTaskDTO() {
		final Timestamp today = new Timestamp(System.currentTimeMillis());
		final Date date = new Date();
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setActioned("Y");
		taskDTO.setCmts("Test Comments");
		taskDTO.setCrewId("CRW1");
		taskDTO.setCrtdTs(today);
		taskDTO.setCrtdUsr("Test user");
		taskDTO.setDepotId("DEP1");
		taskDTO.setExecutionPackageId(12345L);
		taskDTO.setLeadCrewId("CRW1");
		taskDTO.setLstUpdtdTs(today);
		taskDTO.setLstUpdtdUsr("test User");
		taskDTO.setMatrlReqRef("MAT1");
		taskDTO.setSchdDt(date);
		taskDTO.setTaskId("WO11");

		Assert.assertEquals("Y", taskDTO.getActioned());
		Assert.assertEquals("Test Comments", taskDTO.getCmts());
		Assert.assertEquals("CRW1", taskDTO.getCrewId());
		Assert.assertEquals(today, taskDTO.getCrtdTs());
		Assert.assertEquals("Test user", taskDTO.getCrtdUsr());
		Assert.assertEquals("DEP1", taskDTO.getDepotId());
		Assert.assertEquals(12345L, taskDTO.getExecutionPackageId());
		Assert.assertEquals("CRW1", taskDTO.getLeadCrewId());
		Assert.assertEquals(today, taskDTO.getLstUpdtdTs());
		Assert.assertEquals("test User", taskDTO.getLstUpdtdUsr());
		Assert.assertEquals("MAT1", taskDTO.getMatrlReqRef());
		Assert.assertEquals(date, taskDTO.getSchdDt());
		Assert.assertEquals("WO11", taskDTO.getTaskId());
	}

	/**
	 * Test case to verify the DTO object {@link ToDoAssignment}
	 */
	@Test
	public void testTodoAssignment() {
		ToDoAssignment tda = new ToDoAssignment();

		tda.setComment("test comments");
		tda.setReqByDate("22/04/2017");
		tda.setStatus("completed");
		tda.setSupportingDoc("test docs");
		tda.setToDoAssignmentId(12345L);
		tda.setWorkOrderId("WO11");
		tda.setToDoName("ESA");

		Assert.assertEquals("test comments", tda.getComment());
		Assert.assertEquals("22/04/2017", tda.getReqByDate());
		Assert.assertEquals("completed", tda.getStatus());
		Assert.assertEquals("test docs", tda.getSupportingDoc());
		Assert.assertEquals(new Long(12345), tda.getToDoAssignmentId());
		Assert.assertEquals("ESA", tda.getToDoName());
		Assert.assertEquals("WO11", tda.getWorkOrderId());
		Assert.assertNotNull(tda.toString());
	}
	
	/**
	 * Test case to verify the DTO object {@link ViewToDoStatus}
	 */
	@Test
	public void testViewToDoStatusDTO () {
		ViewToDoStatus todoStatus = new ViewToDoStatus();
		List<String> crewAssigned = new ArrayList<>();
		todoStatus.setCrewAssigned(crewAssigned);
		todoStatus.setDeportComment("Depot comments");
		todoStatus.setExctnPckgName("28-04-2017_12222294");
		todoStatus.setLeadCrew("CREW1");
		todoStatus.setScheduleDate("28/04/2017");
		todoStatus.setSchedulingComment("Scheduler comments");
		List<ToDoAssignment> tdas = new ArrayList<>();
		todoStatus.setTodoAssignments(tdas);
		List<String> workOrders = new ArrayList<>();
		todoStatus.setWorkOrders(workOrders);
		
		Assert.assertEquals(crewAssigned, todoStatus.getCrewAssigned());
		Assert.assertEquals("Depot comments", todoStatus.getDeportComment());
		Assert.assertEquals("28-04-2017_12222294", todoStatus.getExctnPckgName());
		Assert.assertEquals("CREW1", todoStatus.getLeadCrew());
		Assert.assertEquals("28/04/2017", todoStatus.getScheduleDate());
		Assert.assertEquals("Scheduler comments", todoStatus.getSchedulingComment());
		Assert.assertEquals(tdas, todoStatus.getTodoAssignments());
		Assert.assertEquals(workOrders, todoStatus.getWorkOrders());
		Assert.assertNotNull(todoStatus.toString());
	}
	
	/**
	 * Test case to verify the DTO object {@link ToDoItem}
	 */
	@Test
	public void testTodoItem () {
		ToDoItem todoItem = new ToDoItem();
		todoItem.setComments("Test comments");
		todoItem.setCrtdTs("28/04/2017");
		todoItem.setCrtdUsr("Test user");
		todoItem.setLstUpdtdTs("29/04/2017");
		todoItem.setLstUpdtdUsr("test user1");
		Date reqdByDate = new Date();
		todoItem.setReqdByDate(reqdByDate);
		todoItem.setStatus("comleted");
		todoItem.setSupportingDocLink("doc links");
		todoItem.setTmpltDesc("template desc");
		todoItem.setTmpltId("1");
		todoItem.setTodoId("1");
		todoItem.setToDoName("ESA");
		List<String> workOrders = new ArrayList<>();
		todoItem.setWorkOrders(workOrders);
		
		Assert.assertEquals("Test comments", todoItem.getComments());
		Assert.assertEquals("28/04/2017", todoItem.getCrtdTs());
		Assert.assertEquals("Test user", todoItem.getCrtdUsr());
		Assert.assertEquals("29/04/2017", todoItem.getLstUpdtdTs());
		Assert.assertEquals("test user1", todoItem.getLstUpdtdUsr());
		Assert.assertEquals(reqdByDate, todoItem.getReqdByDate());
		Assert.assertEquals("comleted", todoItem.getStatus());
		Assert.assertEquals("doc links", todoItem.getSupportingDocLink());
		Assert.assertEquals("template desc", todoItem.getTmpltDesc());
		Assert.assertEquals("1", todoItem.getTmpltId());
		Assert.assertEquals("1", todoItem.getTodoId());
		Assert.assertEquals("ESA", todoItem.getToDoName());
		Assert.assertEquals(workOrders, todoItem.getWorkOrders());
	}
	/**
	 * Test case to verify the DTO object {@link WorkOrderSearchRequest}
	 */
	@Test
	public void testWorkOrderSearchRequestDTO() {
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		List<String> crewList = new ArrayList<>();
		request.setCrewList(crewList);
		List<String> depotList = new ArrayList<>();
		request.setDepotList(depotList);
		request.setExecPckgName("28-04-2017_1264932");
		request.setFromDate("28/04/2017");
		request.setToDate("28/04/2017");
		request.setWorkOrderId("WO11");
		
		Assert.assertEquals(crewList, request.getCrewList());
		Assert.assertEquals(depotList, request.getDepotList());
		Assert.assertEquals("28-04-2017_1264932", request.getExecPckgName());
		Assert.assertEquals("28/04/2017", request.getFromDate());
		Assert.assertEquals("28/04/2017", request.getToDate());
		Assert.assertEquals("WO11", request.getWorkOrderId());
		
	}
	/**
	 * Test case to verify the DTO object {@link WorkOrder}
	 */
	@Test
	public void testWorkOrderDTO () {
		WorkOrder workOrder = new WorkOrder();
		workOrder.setMeterialReqRef("MAT1");
		workOrder.setDepotId("DEP1");
		workOrder.setSchedulingToDoComment("Scheduler comments");
		workOrder.setDepotToDoComment("Depot comments");
		workOrder.setActioned("Y");
		
		Assert.assertEquals("Depot comments", workOrder.getDepotToDoComment());
		Assert.assertEquals("Y", workOrder.getActioned());
	}

	/**
	 * Test case to verify the DTO object {@link ExecutionPackageDTO}
	 */
	@Test
	public void testExecutionPackageDTO () {
		ExecutionPackageDTO execPckg = new ExecutionPackageDTO();
		execPckg.setActioned("Y");
		execPckg.setCrewNames("CRW1,CRW2");
		Assert.assertEquals("Y", execPckg.getActioned());
		Assert.assertEquals("CRW1,CRW2", execPckg.getCrewNames());
	}
	
}
