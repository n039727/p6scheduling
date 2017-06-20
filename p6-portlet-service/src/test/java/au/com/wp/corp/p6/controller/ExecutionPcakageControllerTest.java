/**
 * 
 */
package au.com.wp.corp.p6.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.wp.corp.p6.businessservice.IExecutionPackageService;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.service.impl.ExecutionPackageContoller;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.validation.Validator;

/**
 * @author n039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class ExecutionPcakageControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	ExecutionPackageContoller execPckgCtrl;

	@Mock
	private IExecutionPackageService executionPckg;
	
	@Mock(answer=Answers.CALLS_REAL_METHODS)
	private Validator validator;
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(execPckgCtrl).build();
	}

	@Test
	public void testCreateOrUpdateExecutionPackages() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		ExecutionPackageDTO executionPackageDTO = new ExecutionPackageDTO();

		executionPackageDTO.setExctnPckgName("123457");

		WorkOrder workOrder = new WorkOrder();
		workOrder.setWorkOrderId("WO1234");
		workOrder.setScheduleDate("2017-06-14");
		List<WorkOrder> workOrders = new ArrayList<>();
		workOrders.add(workOrder);
		executionPackageDTO.setWorkOrders(workOrders);
		Mockito.when(executionPckg.createOrUpdateExecutionPackage(executionPackageDTO))
				.thenReturn(executionPackageDTO);
		executionPckg.setExecutionPackageDTDOFoP6(Arrays.asList(executionPackageDTO));
		executionPckg.updateP6ForExecutionPackage();
		ResultActions actions = mockMvc.perform(post("/executionpackage/createOrUpdate")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.content(mapper.writeValueAsString(executionPackageDTO))).andExpect(status().is(HttpStatus.CREATED.value()));
	}
	
	@Test
	public void testSearchByExecutionPackage () throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("15-05-2017'T'00:00:00.000Z");
		
		WorkOrder workOrder = new WorkOrder();
		workOrder.setWorkOrderId("WO1234");
		workOrder.setScheduleDate("2017-05-15");
		List<WorkOrder> workOrders = new ArrayList<>();
		workOrders.add(workOrder);
		
		Mockito.when(executionPckg.searchByExecutionPackage(request))
		.thenReturn(workOrders);
		
		ResultActions actions = mockMvc.perform(post("/executionpackage/searchByExecutionPackage")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk());

	}
	
	@Test
	public void testSearchByExecutionPackage_ERROR1 () throws Exception {
		thrown.expect(NestedServletException.class);
		ObjectMapper mapper = new ObjectMapper();
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("15-05-2017'T'00:00:00.000Z");
		
		WorkOrder workOrder = new WorkOrder();
		workOrder.setWorkOrderId("WO1234");
		workOrder.setScheduleDate("2017-05-15");
		List<WorkOrder> workOrders = new ArrayList<>();
		workOrders.add(workOrder);
		
		Mockito.when(executionPckg.searchByExecutionPackage(request))
		.thenReturn(workOrders);
		
		ResultActions actions = mockMvc.perform(post("/executionpackage/searchByExecutionPackage")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				).andExpect(status().isBadRequest());

	}
}
