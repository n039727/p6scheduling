/**
 * 
 */
package au.com.wp.corp.p6.model;

import java.math.BigDecimal;

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

	
	
	public void testTask () {
		Task task = new Task();
	}
}
