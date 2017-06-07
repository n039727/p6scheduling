/**
 * 
 */
package au.com.wp.corp.p6.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import au.com.wp.corp.p6.business.P6EllipseIntegrationService;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.rest.controller.P6EllipseIntegrationController;

/**
 * Contains test cases to invoke {@link P6EllipseIntegrationController} service
 * 
 * @author N039126
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class P6EllipseIntegrationControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	P6EllipseIntegrationController controller;

	@Mock
	P6EllipseIntegrationService p6EllipseService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void testStartIntegartion() throws Exception {
		ResultActions actions = mockMvc.perform(get("/integration/p6-ellipse").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk()).andExpect(content().string("OK"));
		
	}
	
	
	@Test
	public void testStartIntegartion_Error() throws Exception {
		Mockito.when(p6EllipseService.startEllipseToP6Integration()).thenThrow(P6BusinessException.class);
		ResultActions actions = mockMvc.perform(get("/integration/p6-ellipse").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk()).andExpect(content().string("NOTOK"));
	}
	
	

}
