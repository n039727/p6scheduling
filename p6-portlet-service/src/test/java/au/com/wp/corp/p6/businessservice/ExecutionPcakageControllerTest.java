/**
 * 
 */
package au.com.wp.corp.p6.businessservice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.wp.corp.p6.businessservice.impl.ExecutionPackageServiceImpl;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.service.impl.ExecutionPackageContoller;

/**
 * @author n039126
 *
 */
//@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
//@RunWith(SpringJUnit4ClassRunner.class)
public class ExecutionPcakageControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	ExecutionPackageContoller execPckgCtrl;

	@Mock
	private ExecutionPackageServiceImpl executionPckg;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(execPckgCtrl).build();
	}

	//@Test
	public void testCreateOrUpdateExecutionPackages() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ExecutionPackageDTO executionPackageDTO = new ExecutionPackageDTO();

		executionPackageDTO.setExctnPckgName("123457");

		WorkOrder workOrder = new WorkOrder();
		workOrder.setWorkOrderId("WO1234");
		List<WorkOrder> workOrders = new ArrayList<>();
		workOrders.add(workOrder);
		executionPackageDTO.setWorkOrders(workOrders);
		Mockito.when(executionPckg.createOrUpdateExecutionPackage(executionPackageDTO, "Test User"))
				.thenReturn(executionPackageDTO);

		ResultActions actions = mockMvc.perform(post("/p6-portal-service/scheduler/executionpackage/createOrUpdate")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.content(mapper.writeValueAsString(executionPackageDTO))).andExpect(status().isOk());
	}
}
