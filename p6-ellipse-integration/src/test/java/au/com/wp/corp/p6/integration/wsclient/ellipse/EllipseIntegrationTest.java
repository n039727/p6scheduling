/**
 * 
 */
package au.com.wp.corp.p6.integration.wsclient.ellipse;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.wsclient.ellipse.impl.EllipseWSClientImpl;

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
		activity.setWorkOrderTaskId("05234288001");
		activity.setPlannedStartDate("06/08/2015 10:12:30");
		activity.setTaskUserStatus("AL");
		activity.setCalcDurFlag("Y");
		activities.add(activity);
		boolean status = ellipseWSClient.updateActivitiesEllipse(activities);
		Assert.assertTrue(status);
	}

	@Test
	public void testEllipseWorkOrdertaskUpdate_1() throws P6ServiceException {
		
		List<EllipseActivityDTO> activities = new ArrayList<>();

		EllipseActivityDTO activity = new EllipseActivityDTO();
		activity.setWorkOrderTaskId("05236401002");
		activity.setWorkGroup("MOST10");
		activity.setPlannedStartDate(null);
		activity.setPlannedFinishDate(null);
		activity.setTaskUserStatus("AL");
		activities.add(activity);
		boolean status = ellipseWSClient.updateActivitiesEllipse(activities);
		Assert.assertTrue(status);

	}
	
	@Test
	public void testEllipseWorkOrdertaskUpdate_2() throws P6ServiceException {
		List<EllipseActivityDTO> activities = new ArrayList<>();
		EllipseActivityDTO activity = new EllipseActivityDTO();
		activity.setWorkOrderTaskId("05419074002");
		activity.setWorkGroup("EJERSCH");
		activity.setPlannedStartDate(null);
		activity.setPlannedFinishDate(null);
		activity.setTaskUserStatus("AL");
		activity.setCalcDurFlag("Y");
		activities.add(activity);
		boolean status = ellipseWSClient.updateActivitiesEllipse(activities);
		Assert.assertTrue(status);

	}
	
	
	@Test
	public void testEllipseWorkOrderUpdate_1() throws P6ServiceException {
		
		String transId = ellipseWSClient.startTransaction();
		
		List<EllipseActivityDTO> activities = new ArrayList<>();

		EllipseActivityDTO activity = new EllipseActivityDTO();
		activity.setWorkOrderTaskId("05419074002");
		activity.setCalcDurFlag("N");
		activities.add(activity);
		boolean status = ellipseWSClient.updateWorkOrderEllipse(activities, transId);
		ellipseWSClient.commitTransaction(transId);

		Assert.assertTrue(status);

	}
	
		
}
