package au.com.wp.corp.p6.wsclient;

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

import au.com.wp.corp.p6.business.P6EllipseIntegrationService;
import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.util.CacheManager;
import au.com.wp.corp.p6.wsclient.cleint.impl.P6WSClientImpl;

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
		System.out.println("read resource test in P6");
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
		
		System.out.println("resource size from P6 == " +  projWorkgroupDTOs.size());
		
		System.out.println("resource id from P6 == " +  projWorkgroupDTOs.get("MONT1"));
		
		
		Map<String, P6ProjWorkgroupDTO> resourceMap = CacheManager.getP6ProjectWorkgroupMap();
		System.out.println("resourceMap ==  " + resourceMap.size());
		
		System.out.println("reasource id = " + resourceMap.get("MONT1").getPrimaryResourceObjectId());

	}
	
	
	
	@Test
	public void test_1_CreateActivitiesP6() throws P6BusinessException {
		System.out.println("Create activity test in P6");
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
		Map<String, P6ProjWorkgroupDTO> resourceMap = CacheManager.getP6ProjectWorkgroupMap();
		System.out.println("resourceMap ==  " + resourceMap.size());
		
		System.out.println("reasource id = " + resourceMap.get("MONT1").getPrimaryResourceObjectId());
		
		activityDTO.setPrimaryResorceObjectId(resourceMap.get("MONT1").getPrimaryResourceObjectId());

		activities.add(activityDTO);
		
		
		
		p6WsclientImpl.createActivities(activities);

	}

	@Test
	public void test_2_UpdateActivitiesP6() throws P6BusinessException {
		
		System.out.println("Update activity test in P6");
		p6serviceImpl.readUDFTypeMapping();
		p6Activities = p6WsclientImpl.readActivities();
		
		List<P6ActivityDTO> activities = new ArrayList<>();

		P6ActivityDTO activityDTO = new P6ActivityDTO();

		activityDTO.setActivityId("TW245976001");
		activityDTO.setActivityName("12 MTHS PATROL (GROUND)");
		activityDTO.setActivityStatus("Not Started");
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
		for ( P6ActivityDTO activityDTO2 : p6Activities )
		{
			if ( activityDTO.getActivityId().equals(activityDTO2.getActivityId())) {
			activityDTO.setActivityObjectId(activityDTO2.getActivityObjectId());
			System.out.println("activity object id  == "+ activityDTO.getActivityObjectId());
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
		System.out.println("Delete activity test in P6");
		
		p6Activities = p6WsclientImpl.readActivities();
		List<P6ActivityDTO> activities = new ArrayList<>();

		P6ActivityDTO activityDTO = new P6ActivityDTO();

		activityDTO.setActivityId("TW245976001");
		activityDTO.setActivityName("12 MTHS PATROL (GROUND)");
		activityDTO.setActivityStatus("Not Started");
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
		
		for ( P6ActivityDTO activityDTO2 : p6Activities )
		{
			if ( activityDTO.getActivityId().equals(activityDTO2.getActivityId()))
			activityDTO.setActivityObjectId(activityDTO2.getActivityObjectId());
		}
		activities.add(activityDTO);
		p6WsclientImpl.deleteActivities(activities);
		
		
	}

}
