/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

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
			Assert.assertNotNull(todoTemplate.getTmpltId());
			Assert.assertNotNull(todoTemplate.getTodoId());

		}

	}
	
}
