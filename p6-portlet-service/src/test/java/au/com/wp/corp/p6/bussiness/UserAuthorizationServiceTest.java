/**
 * 
 */
package au.com.wp.corp.p6.bussiness;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.businessservice.impl.ExecutionPackageServiceImpl;
import au.com.wp.corp.p6.businessservice.impl.UserAuthorizationServiceImpl;
import au.com.wp.corp.p6.dataservice.impl.ExecutionPackageDaoImpl;
import au.com.wp.corp.p6.dataservice.impl.FunctionAccessDAOImpl;
import au.com.wp.corp.p6.dto.UserAuthorizationDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.FunctionAccess;
import au.com.wp.corp.p6.model.PortalFunction;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039603
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class UserAuthorizationServiceTest {

	
	@InjectMocks
	UserAuthorizationServiceImpl userAuthorizationService;

	@Mock
	FunctionAccessDAOImpl functionAccessDAO;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link au.com.wp.corp.p6.businessservice.impl.UserAuthorizationServiceImpl#getAccess(java.lang.String)}.
	 */
	@Test
	public void testGetAccess() {
		FunctionAccess functionAccess = new FunctionAccess();
		PortalFunction portalFunction = new PortalFunction();
		portalFunction.setFuncNam("Add_Scheduling_To_Do");
		functionAccess.setPortalFunction(portalFunction);
		functionAccess.setWriteFlg("Y");
		List<FunctionAccess> accesses = new ArrayList<FunctionAccess>();
		accesses.add(functionAccess);
		Mockito.when(functionAccessDAO.getAccess("P6_TEM_LEDR_SCHDLR")).thenReturn(accesses);
		List<UserAuthorizationDTO> returnVal = userAuthorizationService.getAccess("Add_Scheduling_To_Do");
		
		Assert.assertNotNull(returnVal);
		for (UserAuthorizationDTO dto: returnVal) {
			Assert.assertEquals("Add_Scheduling_To_Do", dto.getFunctionName());
			Assert.assertEquals(Boolean.TRUE, dto.isAccess());
		}
	}


}
