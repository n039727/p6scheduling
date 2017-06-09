/**
 * 
 */
package au.com.wp.corp.p6.wsclient.ellipse;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.ellipse.impl.EllipseWSClientImpl;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EllipseIntegrationTest {

	@Autowired
	EllipseWSClientImpl ellipseWSClient;

	@Test
	public void testEllipseWorkOrdertaskUpdate() throws P6ServiceException {

		List<EllipseActivityDTO> activities = new ArrayList<>();

		EllipseActivityDTO activity = new EllipseActivityDTO();
		activity.setWorkOrderTaskId("04790653002");
		activity.setWorkGroup("MOMT2");
		activity.setPlannedStartDate("06/08/2017 10:12:30");
		activity.setTaskUserStatus("AL");
		activities.add(activity);

		ellipseWSClient.updateActivitiesEllipse(activities);

	}

}
