package au.com.wp.corp.p6.wsclient;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.wsclient.cleint.impl.P6WSClientImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class P6WSClientImplIntegrationTest {

	private List<P6ActivityDTO> p6Activities = null;
	
	@Autowired
	P6WSClientImpl p6WsclientImpl;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void test_1_CreateActivitiesP6() throws P6ServiceException {
		System.out.println("Create activity test in P6");
		List<P6ActivityDTO> activities = new ArrayList<>();

		P6ActivityDTO activityDTO = new P6ActivityDTO();

		activityDTO.setActivityId("W245976001");
		activityDTO.setActivityName("12 MTHS PATROL (GROUND)");
		activityDTO.setActivityStatus("Not Started");
		activityDTO.setPlannedStartDate("2017-07-06  8:00:00");
		activityDTO.setWorkGroup("TWSL");
		activityDTO.setOriginalDuration(33);
		activityDTO.setRemainingDuration(33);
		activityDTO.seteGIUDF("TX_LINE_OH");
		activityDTO.setEllipseStandardJobUDF("KT1408");
		activityDTO.setProjectObjectId(263779);
		activities.add(activityDTO);
		p6WsclientImpl.createActivities(activities);

	}

	@Test
	public void test_2_UpdateActivitiesP6() {
		System.out.println("Update activity test in P6");
	}

	@Test
	public void test_3_ReadActivities() throws P6ServiceException {
		System.out.println("Reading activity test in P6");
		p6Activities = p6WsclientImpl.readActivities();
		Assert.assertNotNull(p6Activities);
	}

	@Test
	public void test_4_DeleteActivitiesP6() throws P6ServiceException {
		System.out.println("Delete activity test in P6");
		
		p6Activities = p6WsclientImpl.readActivities();
		List<P6ActivityDTO> activities = new ArrayList<>();

		P6ActivityDTO activityDTO = new P6ActivityDTO();

		activityDTO.setActivityId("W245976001");
		activityDTO.setActivityName("12 MTHS PATROL (GROUND)");
		activityDTO.setActivityStatus("Not Started");
		activityDTO.setPlannedStartDate("2017-07-06  8:00:00");
		activityDTO.setWorkGroup("TWSL");
		activityDTO.setOriginalDuration(33);
		activityDTO.setRemainingDuration(33);
		activityDTO.seteGIUDF("TX_LINE_OH");
		activityDTO.setEllipseStandardJobUDF("KT1408");
		activityDTO.setProjectObjectId(263779);
		activities.add(activityDTO);
		System.out.println(p6Activities);
		for ( P6ActivityDTO activityDTO2 : p6Activities )
		{
			if ( activityDTO.getActivityId().equals(activityDTO2.getActivityId()))
			activityDTO.setActivityObjectId(activityDTO2.getActivityObjectId());
		}
		
		p6WsclientImpl.deleteActivities(activities);
		
		
	}

}
