/**
 * 
 */
package au.com.wp.corp.p6.integration.controller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import au.com.wp.corp.p6.integration.business.P6PortalIntegrationService;
import au.com.wp.corp.p6.integration.rest.controller.P6PortalIntegrationController;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * Contains test cases to invoke {@link P6PortalIntegrationController} service
 * 
 * @author N039126
 * @version 1.0
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6PortalIntegrationControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	P6PortalIntegrationController controller;

	@Mock
	P6PortalIntegrationService p6PortalService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	@Test
	public void testStartIntegartion() throws Exception {/*
		Mockito.when(p6EllipseService.start()).thenReturn(true);
		ResultActions actions = mockMvc.perform(get("/integration/p6-ellipse").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk()).andExpect(content().string("OK"));
		
	*/}
	
	
	@Test
	public void testStartIntegartion_Error() throws Exception {/*
		Mockito.when(p6EllipseService.start()).thenThrow(P6BusinessException.class);
		ResultActions actions = mockMvc.perform(get("/integration/p6-ellipse").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk()).andExpect(content().string("NOTOK"));
	*/}
	
	

}
