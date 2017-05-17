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

import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author n039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class TaskDAOIntegrationTest {

	@Autowired
	TaskDAO taskDao;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testListTasks () throws P6DataAccessException {
		List<Task> tasks = taskDao.listTasks();
		Assert.assertNotNull(tasks);
		for ( Task task : tasks ){
			Assert.assertNotNull(task.getTaskId());
		}
	}
	
}
