/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.integration.wsclient.logging.RequestTrackingId;
import junit.framework.Assert;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class RequestTrakingIdTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	public void setUp(){
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testRequestTrackingId_1 (){
		thrown.expect(IllegalStateException.class);
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestTrackingId trackingId = RequestTrackingId.fromRequest(request);
		
		Assert.assertNotNull(trackingId);
	}
	
	@Test
	public void testRequestTrackingId_2 (){
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestTrackingId trackingId = new RequestTrackingId(request);
		
		Assert.assertNotNull(trackingId);
	}
}
