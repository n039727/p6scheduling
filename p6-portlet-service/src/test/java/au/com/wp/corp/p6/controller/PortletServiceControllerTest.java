/**
 * 
 */
package au.com.wp.corp.p6.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.wp.corp.p6.businessservice.IExecutionPackageService;
import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dto.UserTokenRequest;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.service.impl.PortletServiceEndpointImpl;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.validation.Validator;

/**
 * @author N039126
 *
 */

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class PortletServiceControllerTest {
	private MockMvc mockMvc;

	@InjectMocks
	PortletServiceEndpointImpl portletServiceEndpoint;

	@Mock
	private P6SchedulingBusinessService p6SchedulingBusinessService;

	@Mock
	private IExecutionPackageService executionPackageService;
	
	@Mock
	Validator validator;
	@Mock
	private UserTokenRequest userTokenRequest;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(portletServiceEndpoint).build();
	}

	@Test
	public void testFetchToDoItems() throws Exception {
		ResultActions actions = mockMvc.perform(get("/scheduler/fetchMetadata")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}

	@Test
	public void testFetchWorkOrdersForViewToDoStatus() throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setExecPckgName("15-05-2017_123456");

		ResultActions actions = mockMvc
				.perform(post("/scheduler/fetchWOForTODOStatus").contentType(MediaType.APPLICATION_JSON_VALUE)
						.accept(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}

	
	@Test
	public void testFetchWorkOrdersForAddUpdateToDo () throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setExecPckgName("15-05-2017_123456");

		ResultActions actions = mockMvc
				.perform(post("/scheduler/fetchWOForAddUpdateToDo").contentType(MediaType.APPLICATION_JSON_VALUE)
						.accept(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}
	
	
	@Test
	public void testSearch () throws JsonProcessingException, Exception {
		ObjectMapper mapper = new ObjectMapper();

		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-05-15'T'00:00:00.000Z");

		ResultActions actions = mockMvc
				.perform(post("/scheduler/search").contentType(MediaType.APPLICATION_JSON_VALUE).principal(new UserPrincipal())
						.accept(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(request)))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testSearch_ERROR1 () throws JsonProcessingException, Exception {
		thrown.expect(NestedServletException.class);
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("2017-05-15'T'00:00:00.000Z");

		ResultActions actions = mockMvc
				.perform(post("/scheduler/search").contentType(MediaType.APPLICATION_JSON_VALUE).principal(new UserPrincipal())
						.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testSaveWorkOrder () throws JsonProcessingException, Exception{
		ObjectMapper mapper = new ObjectMapper();

		WorkOrder request = new WorkOrder();
		request.setCrewNames("MOST1");
		request.setWorkOrderId("WO11");
		request.setScheduleDate("15/05/2017");

		ResultActions actions = mockMvc
				.perform(post("/scheduler/saveWorkOrder").contentType(MediaType.APPLICATION_JSON_VALUE).principal(new UserPrincipal())
						.accept(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(request)))
				.andExpect(status().is(HttpStatus.CREATED.value()));
	}
	
	@Test
	public void testSaveViewToDoStatus () throws JsonProcessingException, Exception {
		ObjectMapper mapper = new ObjectMapper();

		ViewToDoStatus request = new ViewToDoStatus();
		request.setDeportComment("test comments depot");
		request.setSchedulingComment("test comments scheduler");
		request.setScheduleDate("15/05/2017");
		List<String> workOrders = new ArrayList<>();
		workOrders.add("WO11");
		request.setWorkOrders(workOrders);
		ResultActions actions = mockMvc
				.perform(post("/scheduler/saveWorkOrderForViewToDoStatus").contentType(MediaType.APPLICATION_JSON_VALUE).principal(new UserPrincipal())
						.accept(MediaType.APPLICATION_JSON_VALUE).content(mapper.writeValueAsString(request)))
				.andExpect(status().is(HttpStatus.CREATED.value()));
		
	}
	
	
	
}
