/**
 * 
 */
package au.com.wp.corp.p6.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author n039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6ModelTest {

	@Test
	public void testTodoAssignmentPK () {
		
		TodoAssignmentPK pk = new TodoAssignmentPK();
		Task task = new Task();
		pk.setTask(task);
		pk.setTodoId(new BigDecimal("1"));
		
		Assert.assertEquals(task, pk.getTask());
		Assert.assertEquals(1, pk.getTodoId().intValue());
		Assert.assertTrue(pk.equals(pk));
		Assert.assertNotNull(pk.hashCode());
	}

	
	@Test
	public void testTask () {
		final long time = System.currentTimeMillis();
		final Date date = new Date();
		final Set<Task> tasks = new HashSet<>();
		Task task = new Task();
		task.setActioned("Y");
		task.setCmts("test comments");
		task.setCrewId("MOST1");
		task.setCrtdTs(new Timestamp(time));
		task.setCrtdUsr("Test User");
		task.setDepotId("DEP1");
		
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setActioned("Y");
		executionPackage.setCrtdTs(new Timestamp(time));
		executionPackage.setExctnPckgId(12345L);
		executionPackage.setExctnPckgNam("14-05-2017_123456");
		executionPackage.setCrtdUsr("Test User");
		executionPackage.setLeadCrewId("MOST1");
		executionPackage.setLstUpdtdTs(new Timestamp(time));
		executionPackage.setLstUpdtdUsr("Test User1");
		executionPackage.setScheduledStartDate(date);
		executionPackage.setTasks(tasks);
		task.setExecutionPackage(executionPackage);
		task.setLeadCrewId("MOST1");
		task.setLstUpdtdTs(new Timestamp(time));
		task.setLstUpdtdUsr("Test User1");
		task.setMatrlReqRef("M1234");
		task.setSchdDt(date);
		task.setTaskId("WO11");
		Set<TodoAssignment> todoAssignments = new HashSet<>();
		TodoAssignment todoAssignment = new TodoAssignment();
		todoAssignment.setCmts("Test comments");
		todoAssignment.setCrtdTs(new Timestamp(time));
		todoAssignment.setCrtdUsr("Test User");
		todoAssignment.setLstUpdtdTs(new Timestamp(time));
		todoAssignment.setLstUpdtdUsr("Test User1");
		todoAssignment.setReqdByDt(date);
		todoAssignment.setStat("Completed");
		TodoAssignmentPK todoAssignMentPK = new TodoAssignmentPK();
		todoAssignment.setTodoAssignMentPK(todoAssignMentPK);
		todoAssignment.setSuprtngDocLnk("Test docs");
		todoAssignments.add(todoAssignment);
		task.setTodoAssignments(todoAssignments);
		task.addTodoAssignment(todoAssignment);
		Assert.assertEquals("Y", task.getActioned());
		Assert.assertEquals("test comments", task.getCmts());
		Assert.assertEquals("MOST1", task.getCrewId());
		Assert.assertEquals( time, task.getCrtdTs().getTime());
		Assert.assertEquals("Test User", task.getCrtdUsr());
		Assert.assertEquals(executionPackage, task.getExecutionPackage());
		Assert.assertEquals("MOST1", task.getLeadCrewId());
		Assert.assertEquals(time, task.getLstUpdtdTs().getTime());
		Assert.assertEquals("Test User1", task.getLstUpdtdUsr());
		Assert.assertEquals("M1234", task.getMatrlReqRef());
		Assert.assertEquals(date, task.getSchdDt());
		Assert.assertEquals("WO11", task.getTaskId());
		Assert.assertEquals(todoAssignments, task.getTodoAssignments());
		Assert.assertEquals("DEP1", task.getDepotId());
		task.removeTodoAssignment(todoAssignment);
		
		Assert.assertEquals("Test comments", todoAssignment.getCmts());
		Assert.assertEquals(time, todoAssignment.getCrtdTs().getTime());
		Assert.assertEquals("Test User", todoAssignment.getCrtdUsr());
		Assert.assertEquals(time, todoAssignment.getLstUpdtdTs().getTime());
		Assert.assertEquals(date, todoAssignment.getReqdByDt());
		Assert.assertEquals("Completed", todoAssignment.getStat());
		Assert.assertEquals("Test docs", todoAssignment.getSuprtngDocLnk());
		Assert.assertEquals(todoAssignMentPK, todoAssignment.getTodoAssignMentPK());
		Assert.assertEquals("Test User1", todoAssignment.getLstUpdtdUsr());
	}
	
	@Test
	public void testExecutionPackage () {
		final long time = System.currentTimeMillis();
		final Date date = new Date();
		final Set<Task> tasks = new HashSet<>();
		Task task = new Task();
		task.setActioned("Y");
		task.setCmts("test comments");
		task.setCrewId("MOST1");
		task.setCrtdTs(new Timestamp(time));
		task.setCrtdUsr("Test User");
		task.setDepotId("DEP1");
		
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setActioned("Y");
		executionPackage.setCrtdTs(new Timestamp(time));
		executionPackage.setExctnPckgId(12345L);
		executionPackage.setExctnPckgNam("14-05-2017_123456");
		executionPackage.setCrtdUsr("Test User");
		executionPackage.setLeadCrewId("MOST1");
		executionPackage.setLstUpdtdTs(new Timestamp(time));
		executionPackage.setLstUpdtdUsr("Test User1");
		executionPackage.setScheduledStartDate(date);
		executionPackage.setTasks(tasks);
		executionPackage.addTask(task);
		task.setExecutionPackage(executionPackage);
		task.setLeadCrewId("MOST1");
		task.setLstUpdtdTs(new Timestamp(time));
		task.setLstUpdtdUsr("Test User1");
		task.setMatrlReqRef("M1234");
		task.setSchdDt(date);
		task.setTaskId("WO11");
		
		Assert.assertEquals(12345L, executionPackage.getExctnPckgId());
		Assert.assertEquals("Y", executionPackage.getActioned());
		Assert.assertEquals(time, executionPackage.getCrtdTs().getTime());
		Assert.assertEquals("Test User", executionPackage.getCrtdUsr());
		Assert.assertEquals("14-05-2017_123456", executionPackage.getExctnPckgNam());
		Assert.assertEquals("MOST1", executionPackage.getLeadCrewId());
		Assert.assertEquals(time, executionPackage.getLstUpdtdTs().getTime());
		Assert.assertEquals("Test User1", executionPackage.getLstUpdtdUsr());
		Assert.assertEquals(date, executionPackage.getScheduledStartDate());
		Assert.assertEquals(tasks, executionPackage.getTasks());
		executionPackage.removeTask(task);
		
	}
	
	public void testTodoTemplate () {
		TodoTemplate todoTemplate = new TodoTemplate();
		
		
	}
}
