/**
 * 
 */
package au.com.wp.corp.p6.dataservice;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.model.FunctionAccess;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039603
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class FunctionAccessDAOIntegrationTest {
	
	@Autowired
	FunctionAccessDAO functionAccessDAO;

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
	 * Test method for {@link au.com.wp.corp.p6.dataservice.impl.FunctionAccessDAOImpl#getAccess(java.lang.String)}.
	 */
	@Transactional
	@Rollback(true)
	@Test
	public void testGetAccess() {
	
		List<FunctionAccess> accesses = functionAccessDAO.getAccess("P6_TEM_LEDR_SCHDLR");
		if(accesses != null){
			for(FunctionAccess access:accesses){
				Assert.assertEquals("Add_Scheduling_To_Do", access.getPortalFunction().getFuncNam());
				Assert.assertEquals("Y",access.getWriteFlg());
			}
		}
		Assert.assertNotNull(accesses);
	}

}
