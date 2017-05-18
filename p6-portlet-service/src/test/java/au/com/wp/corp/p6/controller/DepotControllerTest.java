/**
 * 
 */
package au.com.wp.corp.p6.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
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

import au.com.wp.corp.p6.businessservice.impl.DepotTodoServiceImpl;
import au.com.wp.corp.p6.businessservice.impl.ExecutionPackageServiceImpl;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.service.impl.DepotController;
import au.com.wp.corp.p6.service.impl.ExecutionPackageContoller;
import au.com.wp.corp.p6.test.config.AppConfig;
import au.com.wp.corp.p6.validation.Validator;

/**
 * @author N039603
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class DepotControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	DepotController depotController;

	@Mock
	private DepotTodoServiceImpl deportService;
	
	@Mock
	Validator validator;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(depotController).build();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link au.com.wp.corp.p6.service.impl.DepotController#viewDepotToDo(org.springframework.http.RequestEntity)}.
	 */
	@Test
	public void testViewDepotToDo() throws Exception{
		
		ObjectMapper mapper = new ObjectMapper();
		WorkOrderSearchRequest request = new WorkOrderSearchRequest();
		request.setFromDate("18-05-2017'T'00:00:00.000Z");
		
		ViewToDoStatus viewToDoStatus = new ViewToDoStatus();
		viewToDoStatus.setExctnPckgName("18-05-2017_02170252");
		
		Mockito.when(deportService.fetchDepotTaskForViewToDoStatus(request))
		.thenReturn(viewToDoStatus);
		
		ResultActions actions = mockMvc.perform(post("/depot/viewTodo")
				.contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE)
				.content(mapper.writeValueAsString(request))).andExpect(status().isOk());
	}

	/**
	 * Test method for {@link au.com.wp.corp.p6.service.impl.DepotController#updateDepotToDo(org.springframework.http.RequestEntity)}.
	 */
	/*@Test
	public void testUpdateDepotToDo() {
		fail("Not yet implemented");
	}*/

}
