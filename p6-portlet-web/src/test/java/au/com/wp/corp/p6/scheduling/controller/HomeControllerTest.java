/**
 * 
 */
package au.com.wp.corp.p6.scheduling.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * Contains test cases to invoke {@link HomeController} service
 * 
 * @author N039126
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
public class HomeControllerTest {

	private MockMvc mockMvc;

	@InjectMocks
	HomeController controller;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}


	@Test
	public void testStartIntegartion_Home() throws Exception {
		ResultActions actions = mockMvc.perform(get("/home").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testStartIntegartion_Logout() throws Exception {
		ResultActions actions = mockMvc.perform(get("/logout").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().is(302));
	}

}
