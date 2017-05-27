package au.com.wp.corp.p6.wsclient;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.cleint.impl.P6WSClientImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class P6WSClientImplIntegrationTest {

	@Autowired
	P6WSClientImpl p6WsclientImpl;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testReadActivities() throws P6ServiceException {
		List<P6ActivityDTO>  p6Activities = p6WsclientImpl.readActivities();
		Assert.assertNotNull(p6Activities);
	}

}
