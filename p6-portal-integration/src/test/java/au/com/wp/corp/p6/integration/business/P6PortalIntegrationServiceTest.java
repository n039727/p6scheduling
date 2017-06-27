/**
 * 
 */
package au.com.wp.corp.p6.integration.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.integration.business.impl.P6PortalIntegrationServiceImpl;
import au.com.wp.corp.p6.integration.dao.P6PortalDAOImpl;
import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.wsclient.cleint.impl.P6WSClientImpl;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6PortalIntegrationServiceTest {
	@Mock
	P6PortalDAOImpl p6PortalDAO;

	@InjectMocks
	P6PortalIntegrationServiceImpl p6PortalIntegrationService;

	@Mock
	P6WSClientImpl p6WSClient;

	
	@Mock
	DateUtil dateUtil;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	List<String> workgroupList = null;
	Map<String, Integer> projWorkgroupDTOs = new HashMap<>();
	
	@Before
	public void setup() throws P6BusinessException {
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Read all configuration project and workgroup mapping from P6 database
	 * 
	 * @throws P6BusinessException
	 */

	@Test
	public void testReadActivities() throws P6BusinessException {}

	/**
	 * WO task is in Authorised status and assigned to the list of work group
	 * when the WO task is read from Ellipse and corresponding activity is not
	 * present in P6 then a new activity will be created in P6 with all the
	 * required details including Planned start date.
	 * 
	 * @throws P6BusinessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testConstructP6ActivityDTO_US300_AC1() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	/**
	 * For the WO task is read from Ellipse when the corresponding activity
	 * needs to be created in P6 with all the details then it should be created
	 * in the project based on the work group that is assigned on the task
	 * 
	 * @throws P6BusinessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testConstructP6ActivityDTO_US300_AC2() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	/**
	 * the activity is getting created in P6 when Planned start date is blank in
	 * Ellipse then the Planned Start date in P6 will be updated to 1st of the
	 * financial year (1-July) based on the required by date in Ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testConstructP6ActivityDTO_US300_AC3() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	/**
	 * An activity in P6 is moved from 1 work group to another which is part of
	 * a new project when comparing the data between P6 and Ellipse then in the
	 * 1st run Ellipse will be updated with the new work group and in the next
	 * run activity in P6 will be deleted from the 1st project (old Work Group)
	 * and a new activity will be created in the 2nd project (new Work Group)
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testConstructP6ActivityDTO_US300_AC4() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	/**
	 * 
	 * On WO task their are changes in the data which flows to P6 when comparing
	 * the data between P6 and Ellipse then P6 needs to be updated with the new
	 * data (based on the condition and master system)
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC1() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	/**
	 * 
	 * In Ellipse WO task ='RR' and Planned Start Date = 'Todays Date' when
	 * comparing the data between P6 and Ellipse then user status in P6 will not
	 * be updated and it is left as it is else update the user status from
	 * Ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC2() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	
	/**
	 * 
	 * In Ellipse WO task ='RR' and Planned Start Date = 'Todays Date' when
	 * comparing the data between P6 and Ellipse then user status in P6 will not
	 * be updated and it is left as it is else update the user status from
	 * Ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC2_1() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	
	/**
	 * 
	 * In Ellipse WO task ='RR' and Planned Start Date = 'Todays Date' when
	 * comparing the data between P6 and Ellipse then user status in P6 will not
	 * be updated and it is left as it is else update the user status from
	 * Ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC2_2() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP);
		Date date = new Date();
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERT01");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("AL");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("7.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate(sdf.format(date));
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");
		ellipseActivity.setAddress("WA");

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("NGERT01");
		p6Activity.setActivityJDCodeUDF("EI");
		p6Activity.setActivityObjectId(123456);
		p6Activity.setActivityStatus("NA");
		p6Activity.setAddressUDF("NA");
		p6Activity.seteGIUDF("TEST");
		p6Activity.setEllipseStandardJobUDF("TEST");
		p6Activity.setEquipmentCodeUDF("");
		p6Activity.setEquipmentNoUDF("");
		p6Activity.setEstimatedLabourHours(8.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Mockito.when(dateUtil.isCurrentDate(sdf.format(date))).thenReturn(true);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();
/*
		Method method = P6PortalIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6PortalIntegrationService, p6Activity,
				ellipseActivity, projectWorkGropMap);
		
		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertNotEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());
 		*/
	}

	
	/**
	 * 
	 * In Ellipse WO task ='RR' and Planned Start Date = 'Todays Date' when
	 * comparing the data between P6 and Ellipse then user status in P6 will not
	 * be updated and it is left as it is else update the user status from
	 * Ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC2_3() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP);
		Date date = new Date();
		
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERT01");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("RR");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("7.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate(sdf.format(date));
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");
		ellipseActivity.setAddress("WA");

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("NGERT01");
		p6Activity.setActivityJDCodeUDF("EI");
		p6Activity.setActivityObjectId(123456);
		p6Activity.setActivityStatus("NA");
		p6Activity.setAddressUDF("NA");
		p6Activity.seteGIUDF("TEST");
		p6Activity.setEllipseStandardJobUDF("TEST");
		p6Activity.setEquipmentCodeUDF("");
		p6Activity.setEquipmentNoUDF("");
		p6Activity.setEstimatedLabourHours(8.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);
		p6Activity.setTaskUserStatusUDF("AL");

		Mockito.when(dateUtil.isCurrentDate(sdf.format(date))).thenReturn(true);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		/*Method method = P6PortalIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6PortalIntegrationService, p6Activity,
				ellipseActivity, projectWorkGropMap);
		
		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertNotEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertNull(p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());
*/
	}
	
	
	/**
	 * WO task user group and Work group against activity in P6 is assigned to
	 * the Scheduler Inbox when comparing the data between P6 and Ellipse then
	 * the Execution Package field in P6 should be updated to blank
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */

	@Test
	public void testSyncEllipseP6Activity_US301_AC3() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERSCH");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("27/05/2017 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");
		ellipseActivity.setAddress("WA");

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("NGERT01");
		p6Activity.setActivityJDCodeUDF("EI");
		p6Activity.setActivityObjectId(123456);
		p6Activity.setActivityStatus("NA");
		p6Activity.setAddressUDF("NA");
		p6Activity.seteGIUDF("TEST");
		p6Activity.setEllipseStandardJobUDF("TEST");
		p6Activity.setEquipmentCodeUDF("");
		p6Activity.setEquipmentNoUDF("");
		p6Activity.setEstimatedLabourHours(7.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setExecutionPckgUDF("TEST123");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-05-27T08:00:00");

		/*Method method = P6PortalIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6PortalIntegrationService, p6Activity,
				ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		// Assert.assertNotEquals(ellipseActivity.getWorkGroup(),
		// p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

		Assert.assertEquals("", p6AtivityDTO.getExecutionPckgUDF());*/
	}

	/**
	 * the activity is getting created in P6 when Planned start date is blank in
	 * Ellipse then the Planned Start date in P6 will be updated to 1st of the
	 * financial year (1-July) based on the required by date in Ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */

	@Test
	public void testSyncEllipseP6Activity_US301_AC4() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERSCH");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");
		ellipseActivity.setAddress("WA");

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("NGERT01");
		p6Activity.setActivityJDCodeUDF("EI");
		p6Activity.setActivityObjectId(123456);
		p6Activity.setActivityStatus("NA");
		p6Activity.setAddressUDF("NA");
		p6Activity.seteGIUDF("TEST");
		p6Activity.setEllipseStandardJobUDF("TEST");
		p6Activity.setEquipmentCodeUDF("");
		p6Activity.setEquipmentNoUDF("");
		p6Activity.setEstimatedLabourHours(7.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

	/*	Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		String startDateOfFiscalYear = dateUtil.getStartDateOfFiscalYear(new Date());
		Mockito.when(
				dateUtil.getStartDateOfFiscalYear(dateUtil.getCurrentDate(), DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn(startDateOfFiscalYear);

		Method method = P6PortalIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6PortalIntegrationService, p6Activity,
				ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		// Assert.assertNotEquals(ellipseActivity.getWorkGroup(),
		// p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(startDateOfFiscalYear, p6AtivityDTO.getPlannedStartDate());

		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

		Assert.assertNull(p6AtivityDTO.getExecutionPckgUDF());*/

	}

	/**
	 * Against an activity in P6, Planned start or the Work group is changed
	 * when comparing the data between Ellipse and P6 then update the User
	 * Status in Ellipse as 'AL' and leave the User Status in P6 as it is for
	 * the 1st run and in next run it will update the status from Ellipse to P6
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US306_AC1() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERSCH");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.00");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("27/07/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("NGERT01");
		p6Activity.setActivityJDCodeUDF("EI");
		p6Activity.setActivityObjectId(123456);
		p6Activity.setActivityStatus("NA");
		p6Activity.setAddressUDF("NA");
		p6Activity.seteGIUDF("TEST");
		p6Activity.setEllipseStandardJobUDF("TEST");
		p6Activity.setEquipmentCodeUDF("");
		p6Activity.setEquipmentNoUDF("");
		p6Activity.setEstimatedLabourHours(7.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		/*Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6PortalIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6PortalIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Assert.assertEquals(p6Activity.getWorkGroup(), ellipseAtivityDTO.getWorkGroup());
		Assert.assertEquals("AL", ellipseAtivityDTO.getTaskUserStatus());*/
	}

	/**
	 * In P6 the activity is in Scheduling Inbox when comparing the data between
	 * Ellipse and P6 then update the Planned Start date in Ellipse as blank and
	 * leave the Planned start date in P6 as it is
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US306_AC2() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MONT1");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.00");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("27/07/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("NGERSCH");
		p6Activity.setActivityJDCodeUDF("EI");
		p6Activity.setActivityObjectId(123456);
		p6Activity.setActivityStatus("NA");
		p6Activity.setAddressUDF("NA");
		p6Activity.seteGIUDF("TEST");
		p6Activity.setEllipseStandardJobUDF("TEST");
		p6Activity.setEquipmentCodeUDF("");
		p6Activity.setEquipmentNoUDF("");
		p6Activity.setEstimatedLabourHours(7.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

	/*	Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6PortalIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		EllipseActivityDTO ellipseActivityDTO = (EllipseActivityDTO) method.invoke(p6PortalIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(p6Activity.getActivityId(), ellipseActivityDTO.getWorkOrderTaskId());
		Assert.assertEquals(p6Activity.getWorkGroup(), ellipseActivityDTO.getWorkGroup());
		Assert.assertEquals("AL", ellipseActivityDTO.getTaskUserStatus());
		Assert.assertEquals("", ellipseActivityDTO.getPlannedStartDate());*/
	}

	/**
	 * In P6 activity is in Crew Work group when while comparing the Planned
	 * Start Date between Ellipse and P6 is different then update the planned
	 * start date from P6 to Ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US306_AC3() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MOMT2");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.00");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("27/07/2017 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2017 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");
		p6Activity.setActivityJDCodeUDF("EI");
		p6Activity.setActivityObjectId(123456);
		p6Activity.setActivityStatus("NA");
		p6Activity.setAddressUDF("NA");
		p6Activity.seteGIUDF("TEST");
		p6Activity.setEllipseStandardJobUDF("TEST");
		p6Activity.setEquipmentCodeUDF("");
		p6Activity.setEquipmentNoUDF("");
		p6Activity.setEstimatedLabourHours(7.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2017-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2017-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-07-27T08:00:00");

		Mockito.when(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("10/06/2017 08:00:00");
		/*

		Method method = P6PortalIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);

		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6PortalIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Mockito.when(dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-06-10 08:00:00");
		Assert.assertEquals(p6Activity.getPlannedStartDate(),
				dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		*/
	}

	/**
	 * In P6 activity Work group is changed when comparing the data between
	 * Ellipse and P6 then update the Crew from P6 to Work Group in Ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */

	@Test
	public void testSyncP6EllipseActivity_US306_AC4() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERSCH");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.00");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("27/07/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MONT1");
		p6Activity.setActivityJDCodeUDF("EI");
		p6Activity.setActivityObjectId(123456);
		p6Activity.setActivityStatus("NA");
		p6Activity.setAddressUDF("NA");
		p6Activity.seteGIUDF("TEST");
		p6Activity.setEllipseStandardJobUDF("TEST");
		p6Activity.setEquipmentCodeUDF("");
		p6Activity.setEquipmentNoUDF("");
		p6Activity.setEstimatedLabourHours(7.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();
/*
		Method method = P6PortalIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6PortalIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Assert.assertEquals(p6Activity.getWorkGroup(), ellipseAtivityDTO.getWorkGroup());*/
	}

	/**
	 * WO task in Ellipse is moved out of the list of Work Groups (Crews and
	 * Scheduling Inbox) and corresponding activity exist in P6 when the task
	 * and activity comparison is happening between Ellipse and P6 then the
	 * Activity needs to be deleted from P6
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */

	@Test
	public void testDeleteActivity_US304_AC1() throws P6BusinessException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
	}

	/**
	 * Create activity in p6 as the activity created in ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStartEllipseToP6Integration_CreateActivity_P6() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	

	}

	/**
	 * Update activity in p6 as the activity created in ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStartEllipseToP6Integration_UpdateActivity_P6() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
	}

	/**
	 * Update activity in p6 as the activity created in ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStartEllipseToP6Integration_UpdateActivity_P6_1() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	
	/**
	 * Update activity in p6 as the activity created in ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStartEllipseToP6Integration_UpdateActivity_P6_3() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}
	
	/**
	 * Update activity in p6 as the activity created in ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStartEllipseToP6Integration_1() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	
	/**
	 * Update activity in p6 as the activity created in ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStart() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {}

	
	@After
	public void testClearApplicationMemory() {
		p6PortalIntegrationService.clearApplicationMemory();
	}

}
