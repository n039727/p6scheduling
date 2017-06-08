/**
 * 
 */
package au.com.wp.corp.p6.wsclient.ellipse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.util.DateUtil;
import au.com.wp.corp.p6.wsclient.ellipse.impl.EllipseWSClientImpl;
import au.com.wp.corp.p6.wsclient.ellipse.impl.EllipseWorkOrderTaskServiceImpl;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EllipseWSClientTest {

	@InjectMocks
	EllipseWSClientImpl ellipseWSClient;

	@Mock
	EllipseWorkOrderTaskServiceImpl ellipseWorkOrdertaskService;

	@Mock
	DateUtil dateUtil;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

	}

	@Test
	public void testUpdateActivitiesEllipse() throws P6ServiceException {

		List<EllipseActivityDTO> activities = new ArrayList<>();

		EllipseActivityDTO activity = new EllipseActivityDTO();
		activity.setWorkOrderTaskId("04790653002");
		activity.setWorkGroup("MOMT2");
		activity.setPlannedStartDate("06/08/2017 10:12:30");
		activity.setTaskUserStatus("AL");
		activities.add(activity);
		Mockito.when(dateUtil.convertDateToString(activity.getPlannedStartDate(), DateUtil.ELLIPSE_DATE_FORMAT,
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP)).thenReturn("06082017");
		
		
		ellipseWSClient.updateActivitiesEllipse(activities);
	}

}
