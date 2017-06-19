package au.com.wp.corp.p6.integration.wsclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import au.com.wp.corp.p6.integration.business.P6EllipseIntegrationService;
import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.wsclient.cleint.impl.P6WSClientImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class P6WSClientImplIntegrationTest {

	private List<P6ActivityDTO> p6Activities = null;
	
	@Autowired
	P6WSClientImpl p6WsclientImpl;
	
	@Autowired
	P6EllipseIntegrationService p6serviceImpl;

	@Before
	public void setup() throws P6BusinessException {
		MockitoAnnotations.initMocks(this);
		p6serviceImpl.readUDFTypeMapping();
		p6serviceImpl.readProjectWorkgroupMapping();
		
	}

	
	@Test
	public void test_5_readResource() throws P6BusinessException {
		
		p6serviceImpl.readUDFTypeMapping();
		p6serviceImpl.readProjectWorkgroupMapping();
		List<P6ActivityDTO> activities = new ArrayList<>();

		P6ActivityDTO activityDTO = new P6ActivityDTO();

		activityDTO.setActivityId("TW245976001");
		activityDTO.setActivityName("12 MTHS PATROL (GROUND)");
		activityDTO.setActivityStatus("Not Started");
		activityDTO.setPlannedStartDate("2017-07-06T08:00:00");
		activityDTO.setWorkGroup("MONT1");
		activityDTO.setOriginalDuration(33);
		activityDTO.setRemainingDuration(33);
		activityDTO.seteGIUDF("TX_LINE_OH");
		activityDTO.setEllipseStandardJobUDF("KT1408");
		activityDTO.setActivityJDCodeUDF("EA");
		activityDTO.setEquipmentNoUDF("4700218");
		activityDTO.setEquipmentCodeUDF("PINT");
		activityDTO.setRequiredByDateUDF("2017-07-30T08:00:00");
		activityDTO.setUpStreamSwitchUDF("DOF 4187606");
		activityDTO.setTaskDescriptionUDF("Test task desc");
		activityDTO.setTaskUserStatusUDF("MR");
		activityDTO.setPickIdUDF("MH-PNJ 81");
		activityDTO.setFeederUDF("E 316.0 SOUTH ST");
		activityDTO.setAddressUDF("Perth WA");
		activityDTO.setProjectObjectId(263779);
		activities.add(activityDTO);
		
		Map<String, Integer> projWorkgroupDTOs = p6WsclientImpl.readResources();
		
				
		Map<String, P6ProjWorkgroupDTO> resourceMap = CacheManager.getP6ProjectWorkgroupMap();
		
	}
	
	
	
	@Test
	public void test_1_CreateActivitiesP6() throws P6BusinessException {
		
		p6serviceImpl.readUDFTypeMapping();
		p6serviceImpl.readProjectWorkgroupMapping();
		List<P6ActivityDTO> activities = new ArrayList<>();

		P6ActivityDTO activityDTO = new P6ActivityDTO();

		activityDTO.setActivityId("TW245976001");
		activityDTO.setActivityName("12 MTHS PATROL (GROUND)");
		activityDTO.setActivityStatus("Not Started");
		activityDTO.setPlannedStartDate("2017-07-06T08:00:00");
		activityDTO.setWorkGroup("MONT1");
		activityDTO.setOriginalDuration(33);
		activityDTO.setRemainingDuration(33);
		activityDTO.seteGIUDF("TX_LINE_OH");
		activityDTO.setEllipseStandardJobUDF("KT1408");
		activityDTO.setActivityJDCodeUDF("EA");
		activityDTO.setEquipmentNoUDF("4700218");
		activityDTO.setEquipmentCodeUDF("PINT");
		activityDTO.setRequiredByDateUDF("2017-07-30T08:00:00");
		activityDTO.setUpStreamSwitchUDF("DOF 4187606");
		activityDTO.setTaskDescriptionUDF("Test task desc");
		activityDTO.setTaskUserStatusUDF("MR");
		activityDTO.setPickIdUDF("MH-PNJ 81");
		activityDTO.setFeederUDF("E 316.0 SOUTH ST");
		activityDTO.setAddressUDF("Perth WA");
		activityDTO.setProjectObjectId(263779);
		activityDTO.setEstimatedLabourHours(8.0);
		activityDTO.setActualStartDate("2017-07-06T08:00:00");
		activityDTO.setActualFinishDate("2017-07-08T08:00:00");
		Map<String, P6ProjWorkgroupDTO> resourceMap = CacheManager.getP6ProjectWorkgroupMap();
		
		activityDTO.setPrimaryResorceObjectId(resourceMap.get("MONT1").getPrimaryResourceObjectId());

		activities.add(activityDTO);

		p6WsclientImpl.createActivities(activities);

	}

	@Test
	public void test_2_UpdateActivitiesP6() throws P6BusinessException {
		
		p6serviceImpl.readUDFTypeMapping();
		p6Activities = p6WsclientImpl.readActivities();
		
		List<P6ActivityDTO> activities = new ArrayList<>();

		P6ActivityDTO activityDTO = new P6ActivityDTO();

		activityDTO.setActivityId("TW245976001");
		activityDTO.setActivityName("12 MTHS PATROL (GROUND)");
		activityDTO.setActivityStatus("Completed");
		activityDTO.setPlannedStartDate("2017-07-06T08:00:00");
		activityDTO.setWorkGroup("MONT1");
		activityDTO.setOriginalDuration(33);
		activityDTO.setRemainingDuration(33);
		activityDTO.setEstimatedLabourHours(35);
		activityDTO.seteGIUDF("TX_LINE_OH_1");
		activityDTO.setEllipseStandardJobUDF("KT1409");
		activityDTO.setActivityJDCodeUDF("EA1");
		activityDTO.setEquipmentNoUDF("4700218");
		activityDTO.setEquipmentCodeUDF("PINT");
		activityDTO.setRequiredByDateUDF("2017-07-30T08:00:00");
		activityDTO.setUpStreamSwitchUDF("DOF 41876061");
		activityDTO.setTaskDescriptionUDF("Test task desc1");
		activityDTO.setTaskUserStatusUDF("MR1");
		activityDTO.setExecutionPckgUDF("");
		activityDTO.setPickIdUDF("MH-PNJ 812");
		activityDTO.setFeederUDF("E 316.0 SOUTH ST1");
		activityDTO.setAddressUDF("Perth WA");
		activityDTO.setProjectObjectId(263779);
		activityDTO.setActualStartDate("2017-07-06T08:00:00");
		activityDTO.setActualFinishDate("2017-07-08T08:00:00");
		for ( P6ActivityDTO activityDTO2 : p6Activities )
		{
			if ( activityDTO.getActivityId().equals(activityDTO2.getActivityId())) {
			activityDTO.setActivityObjectId(activityDTO2.getActivityObjectId());
			break;
			}
		}
		activities.add(activityDTO);
		p6WsclientImpl.updateActivities(activities);
	}

	@Test
	public void test_3_ReadActivities() throws P6ServiceException {
		p6Activities = p6WsclientImpl.readActivities();
		Assert.assertNotNull(p6Activities);
		
		for ( P6ActivityDTO activityDTO : p6Activities)
		{
			Assert.assertNotNull(activityDTO.getActivityId());
		}
	}

	@Test
	public void test_4_DeleteActivitiesP6() throws P6ServiceException {
		p6Activities = p6WsclientImpl.readActivities();
		List<P6ActivityDTO> activities = new ArrayList<>();

		P6ActivityDTO activityDTO = new P6ActivityDTO();

		activityDTO.setActivityId("TW245976001");
		activityDTO.setActivityName("12 MTHS PATROL (GROUND)");
		activityDTO.setActivityStatus("Completed");
		activityDTO.setPlannedStartDate("2017-07-06T08:00:00");
		activityDTO.setWorkGroup("MONT1");
		activityDTO.setOriginalDuration(33);
		activityDTO.setRemainingDuration(33);
		activityDTO.setEstimatedLabourHours(35);
		activityDTO.seteGIUDF("TX_LINE_OH_1");
		activityDTO.setEllipseStandardJobUDF("KT1409");
		activityDTO.setActivityJDCodeUDF("EA1");
		activityDTO.setEquipmentNoUDF("4700218");
		activityDTO.setEquipmentCodeUDF("PINT");
		activityDTO.setRequiredByDateUDF("2017-07-30T08:00:00");
		activityDTO.setUpStreamSwitchUDF("DOF 41876061");
		activityDTO.setTaskDescriptionUDF("Test task desc1");
		activityDTO.setTaskUserStatusUDF("MR1");
		activityDTO.setExecutionPckgUDF("");
		activityDTO.setPickIdUDF("MH-PNJ 812");
		activityDTO.setFeederUDF("E 316.0 SOUTH ST1");
		activityDTO.setAddressUDF("Perth WA");
		activityDTO.setProjectObjectId(263779);
		activityDTO.setActualStartDate("2017-07-06T08:00:00");
		activityDTO.setActualFinishDate("2017-07-08T08:00:00");
		for ( P6ActivityDTO activityDTO2 : p6Activities )
		{
			if ( activityDTO.getActivityId().equals(activityDTO2.getActivityId()))
			activityDTO.setActivityObjectId(activityDTO2.getActivityObjectId());
		}
		activities.add(activityDTO);
		p6WsclientImpl.deleteActivities(activities);
		
		
	}

	@Test
	public void test_6_ReadProjects() throws P6ServiceException{
		Map<String, Integer> projects = p6WsclientImpl.readProjects();
		for ( String key :  projects.keySet()){
			Assert.assertNotNull(key);
			Assert.assertNotNull(projects.get(key));
		}
		
	}
}
