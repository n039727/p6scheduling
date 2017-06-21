/**
 * 
 */

package au.com.wp.corp.p6.scheduling.businessservice;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.scheduling.businessservice.impl.UserAuthorizationServiceImpl;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039603
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class UserAuthorizationServiceIntegrationTest {

	
	@Autowired
	UserAuthorizationServiceImpl userAuthorizationService;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUserName() {
		String user = userAuthorizationService.getUserName("n039957");
		Assert.assertNotNull(user);
	}


}
