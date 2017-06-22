/**
 * 
 */
package au.com.wp.corp.p6.scheduling.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Contains test cases to invoke {@link HomeController} service
 * 
 * @author N039126
 * @version 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/portletWebController-context.xml" })
@Ignore
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
	@Ignore
	public void testStartIntegartion() throws Exception {
		ResultActions actions = mockMvc.perform(get("/auth/login").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk());

	}

	@Test
	@Ignore
	public void testStartIntegartion_Home() throws Exception {
		ResultActions actions = mockMvc.perform(get("/home").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	@Ignore
	public void testStartIntegartion_Error() throws Exception {
		ResultActions actions = mockMvc.perform(get("/auth/error").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk());
	}
	
	@Test
	@Ignore
	public void testStartIntegartion_Logout() throws Exception {
		ResultActions actions = mockMvc.perform(get("/logout").contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk());
	}

}
