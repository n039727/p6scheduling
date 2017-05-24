/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.dataservice.impl.ResourceDetailDAOImpl;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039603
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ResourceDetailDAOIntegrationTest {

	@Autowired
	ResourceDetailDAOImpl resourceDetailDAOImpl;
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

	/**
	 * Test method for {@link au.com.wp.corp.p6.dataservice.impl.ResourceDetailDAOImpl#fetchAllResourceDetail()}.
	 */
	@Transactional
	@Test
	public void testFetchAllResourceDetail() {
		Map<String, List<String>> depotCrewMap = resourceDetailDAOImpl.fetchAllResourceDetail();
		Assert.assertNotNull(depotCrewMap);
		Set<String> keys = depotCrewMap.keySet();
		for(String depot : keys){
			Assert.assertNotNull(depotCrewMap.get(depot));
		}
		
	}

}
