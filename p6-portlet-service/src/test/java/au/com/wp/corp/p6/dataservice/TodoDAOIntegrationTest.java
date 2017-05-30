/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.dataservice.impl.TodoDAOImpl;
import au.com.wp.corp.p6.model.TodoTemplate;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * 
 * @author n039126
 * @version 1.0
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class TodoDAOIntegrationTest {

	@Autowired
	TodoDAOImpl todoDAOImpl;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * test case to verify whether application able to fetch all todo template
	 * value
	 */
	@Test
	@Transactional
	@Rollback(true)
	public void tetsFetchAllToDos() {
		List<TodoTemplate> todoTemps = todoDAOImpl.fetchAllToDos();
		Assert.assertNotNull(todoTemps);
		for (TodoTemplate todoTemplate : todoTemps) {
			Assert.assertNotNull(todoTemplate);
			Assert.assertNotNull(todoTemplate.getCrtdUsr());
			Assert.assertNotNull(todoTemplate.getLstUpdtdUsr());
			Assert.assertNotNull(todoTemplate.getTmpltDesc());
			Assert.assertNotNull(todoTemplate.getTodoNam());
			Assert.assertNotNull(todoTemplate.getCrtdTs());
			Assert.assertNotNull(todoTemplate.getLstUpdtdTs());
			Assert.assertNotNull(todoTemplate.getId().getTmpltId());
			Assert.assertNotNull(todoTemplate.getId().getTodoId());

		}

	}
	
	
	/**
	 * test case to verify whether application able to fetch the todo name 
	 * corresponding to a todo id
	 * 
	 */
	@Test
	@Transactional
	@Rollback(true)
	public void testGetToDoName() {
		String todoname = todoDAOImpl.getToDoName(new Long("4"));
		Assert.assertEquals("Gas Permit", todoname);

	}
	
	/**
	 * test case to verify whether application able to fetch the todo name 
	 * corresponding to a todo id
	 * 
	 */
	@Test
	@Transactional
	@Rollback(true)
	public void testGetToDoId() {
		BigDecimal todoId = todoDAOImpl.getToDoId("Gas Permit");
		Assert.assertEquals(new BigDecimal("4"), todoId);

	}
	
	/**
	 * test case to verify whether application able to fetch the latest record 
	 * to increment the value of the ID fields
	 * value
	 */
	/*@Test
	@Transactional
	@Rollback(true)
	public void testCreateToDo() {
		
		TodoTemplate todoTemplate = new TodoTemplate();
		TodoTemplatePK todoTemplatePK = new TodoTemplatePK();
		todoTemplatePK.setTmpltId(2);
		todoTemplatePK.setTodoId(99999);
		todoTemplate.setId(todoTemplatePK);
		todoTemplate.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		todoTemplate.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		todoTemplate.setCrtdUsr("Test User");
		todoTemplate.setLstUpdtdUsr("Test User");
		todoTemplate.setTmpltDesc("test depot desc");
		todoTemplate.setTodoNam("TestDepotToDo");
		todoTemplate.setTypId(New BigDecimal("2"));
		List<TodoTemplate> todoTemps = todoDAOImpl.fetchToDoForGratestToDoId();
		Assert.assertNotNull(todoTemps);
		for (TodoTemplate todoTemplate : todoTemps) {
			Assert.assertNotNull(todoTemplate);
			Assert.assertNotNull(todoTemplate.getCrtdUsr());
			Assert.assertNotNull(todoTemplate.getLstUpdtdUsr());
			Assert.assertNotNull(todoTemplate.getTmpltDesc());
			Assert.assertNotNull(todoTemplate.getTodoNam());
			Assert.assertNotNull(todoTemplate.getCrtdTs());
			Assert.assertNotNull(todoTemplate.getLstUpdtdTs());
			Assert.assertNotNull(todoTemplate.getId().getTodoId());
			Assert.assertNotNull(todoTemplate.getId().getTmpltId());

		}

	}*/
	
}
