/**
 * 
 */
package au.com.wp.corp.p6.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.business.impl.P6EllipseIntegrationServiceImpl;
import au.com.wp.corp.p6.dao.P6EllipseDAOImpl;
import au.com.wp.corp.p6.dao.P6PortalDAOImpl;
import au.com.wp.corp.p6.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.dto.P6ActivityDTO;
import au.com.wp.corp.p6.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.util.CacheManager;
import au.com.wp.corp.p6.util.DateUtil;
import au.com.wp.corp.p6.util.EllipseReadParameter;
import au.com.wp.corp.p6.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.wsclient.cleint.impl.P6WSClientImpl;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class P6EllipseIntegrationServiceTest {
	@Mock
	P6PortalDAOImpl p6PortalDAO;

	@InjectMocks
	P6EllipseIntegrationServiceImpl p6EllipseIntegrationService;

	@Mock
	P6WSClientImpl p6WSClient;

	@Mock
	P6EllipseDAOImpl p6EllipseDAO;

	@Mock
	DateUtil dateUtil;

	List<String> workgroupList = null;

	@Before
	public void setup() throws P6BusinessException {
		MockitoAnnotations.initMocks(this);

		List<P6ProjWorkgroupDTO> projWorkgroups = new ArrayList<>();
		P6ProjWorkgroupDTO projWorkgroupDTO = new P6ProjWorkgroupDTO();
		projWorkgroupDTO.setProjectObjectId(263779);
		projWorkgroupDTO.setPrimaryResourceId("MONT1");
		projWorkgroupDTO.setPrimaryResourceYN("Y");
		projWorkgroupDTO.setProjectName("DxMetro");
		projWorkgroups.add(projWorkgroupDTO);
		projWorkgroupDTO = new P6ProjWorkgroupDTO();
		projWorkgroupDTO.setProjectObjectId(263779);
		projWorkgroupDTO.setPrimaryResourceId("MOMT2");
		projWorkgroupDTO.setPrimaryResourceYN("Y");
		projWorkgroupDTO.setProjectName("DxMetro");
		projWorkgroups.add(projWorkgroupDTO);
		projWorkgroupDTO = new P6ProjWorkgroupDTO();
		projWorkgroupDTO.setProjectObjectId(263780);
		projWorkgroupDTO.setPrimaryResourceId("NGERT01");
		projWorkgroupDTO.setPrimaryResourceYN("Y");
		projWorkgroupDTO.setProjectName("DxNorth");

		projWorkgroups.add(projWorkgroupDTO);

		projWorkgroupDTO = new P6ProjWorkgroupDTO();
		projWorkgroupDTO.setProjectObjectId(263780);
		projWorkgroupDTO.setPrimaryResourceId("NGERSCH");
		projWorkgroupDTO.setSchedulerinbox("Y");
		projWorkgroupDTO.setProjectName("DxNorth");

		projWorkgroups.add(projWorkgroupDTO);

		Mockito.when(p6PortalDAO.getProjectResourceMappingList()).thenReturn(projWorkgroups);
		p6EllipseIntegrationService.readProjectWorkgroupMapping();

		workgroupList = new ArrayList<>();

		if (P6ReloadablePropertiesReader.getProperty("ELLIPSE_READING_STRATEGY")
				.equals(EllipseReadParameter.ALL.name())) {
			final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet();

			for (String key : keys) {
				workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
			}
		}
		Mockito.when(dateUtil.getStartDateOfFiscalYear(new Date())).thenReturn("2017-06-01 00:00:00");
	}

	/**
	 * Read all configuration project and workgroup mapping from P6 database
	 * 
	 * @throws P6BusinessException
	 */

	@Test
	public void testReadActivities() throws P6BusinessException {
		boolean status = p6EllipseIntegrationService.readProjectWorkgroupMapping();
		Assert.assertTrue(status);
	}

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
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();

		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MOMT2");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("11/06/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		List<EllipseActivityDTO> activities = new ArrayList<>();
		activities.add(ellipseActivity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);
		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());

		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

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
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MOMT2");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("11/06/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		List<EllipseActivityDTO> activities = new ArrayList<>();
		activities.add(ellipseActivity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);
		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

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
	public void testConstructP6ActivityDTO_US300_AC3() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MOMT2");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		List<EllipseActivityDTO> activities = new ArrayList<>();
		activities.add(ellipseActivity);

		String startDateOfFiscalYear = dateUtil.getStartDateOfFiscalYear(new Date());
		Mockito.when(
				dateUtil.getStartDateOfFiscalYear(dateUtil.getCurrentDate(), DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn(startDateOfFiscalYear);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);
		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(), p6AtivityDTO.getEstimatedLabourHoursUDF());
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(dateUtil.getStartDateOfFiscalYear(new Date()), p6AtivityDTO.getPlannedStartDate());

		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());
	}

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
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERT01");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("11/06/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		List<EllipseActivityDTO> activities = new ArrayList<>();
		activities.add(ellipseActivity);

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-11 08:00:00");

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(), p6AtivityDTO.getEstimatedLabourHoursUDF());
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(
				dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP),
				p6AtivityDTO.getPlannedStartDate());

		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

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
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERT01");
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
		ellipseActivity.setLocationInStreet("Test location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("11/06/2012 08:00:00");
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
		p6Activity.setEstimatedLabourHoursUDF("7.00");
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

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-11 08:00:00");

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, p6Activity,
				ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertNotEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(), p6AtivityDTO.getEstimatedLabourHoursUDF());
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(
				dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP),
				p6AtivityDTO.getPlannedStartDate());

		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

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
	public void testSyncEllipseP6Activity_US301_AC2() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERT01");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("7.00");
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
		p6Activity.setEstimatedLabourHoursUDF("8.00");
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

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-05-27 08:00:00");

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, p6Activity,
				ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertNotEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(), p6AtivityDTO.getEstimatedLabourHoursUDF());
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(
				dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP),
				p6AtivityDTO.getPlannedStartDate());

		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

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
		ellipseActivity.setEstimatedLabourHours("8.00");
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
		p6Activity.setEstimatedLabourHoursUDF("7.00");
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

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-05-27 08:00:00");

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, p6Activity,
				ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertNotEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(), p6AtivityDTO.getEstimatedLabourHoursUDF());
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(
				dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP),
				p6AtivityDTO.getPlannedStartDate());

		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

		Assert.assertEquals(false, p6AtivityDTO.isExecutionPckgUDF());
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
		ellipseActivity.setEstimatedLabourHours("8.00");
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
		p6Activity.setEstimatedLabourHoursUDF("7.00");
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

		String startDateOfFiscalYear = dateUtil.getStartDateOfFiscalYear(new Date());
		Mockito.when(
				dateUtil.getStartDateOfFiscalYear(dateUtil.getCurrentDate(), DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn(startDateOfFiscalYear);

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, p6Activity,
				ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertNotEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(), p6AtivityDTO.getEstimatedLabourHoursUDF());
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

		Assert.assertEquals(false, p6AtivityDTO.isExecutionPckgUDF());

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
		p6Activity.setEstimatedLabourHoursUDF("7.00");
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

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Assert.assertEquals(p6Activity.getWorkGroup(), ellipseAtivityDTO.getWorkGroup());
		Assert.assertEquals("AL", ellipseAtivityDTO.getTaskUserStatus());
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
		p6Activity.setEstimatedLabourHoursUDF("7.00");
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

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		EllipseActivityDTO ellipseActivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(p6Activity.getActivityId(), ellipseActivityDTO.getWorkOrderTaskId());
		Assert.assertEquals(p6Activity.getWorkGroup(), ellipseActivityDTO.getWorkGroup());
		Assert.assertEquals("AL", ellipseActivityDTO.getTaskUserStatus());
		Assert.assertEquals("", ellipseActivityDTO.getPlannedStartDate());
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
		p6Activity.setEstimatedLabourHoursUDF("7.00");
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
				.thenReturn("2017-07-27 08:00:00");

		Mockito.when(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("10/06/2017 08:00:00");

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);

		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Mockito.when(dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-06-10 08:00:00");
		Assert.assertEquals(p6Activity.getPlannedStartDate(),
				dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
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
		p6Activity.setEstimatedLabourHoursUDF("7.00");
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

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);

		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Assert.assertEquals(p6Activity.getWorkGroup(), ellipseAtivityDTO.getWorkGroup());
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
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERT01");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("11/06/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		List<EllipseActivityDTO> activities = new ArrayList<>();
		activities.add(ellipseActivity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");
		p6Activity.setActivityName("Test");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.when(p6WSClient.readActivities()).thenReturn(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration();
		Assert.assertEquals(1, deleteActivites.size());

		for (P6ActivityDTO p6AtivityDTO : deleteActivites) {
			Assert.assertEquals(p6Activity.getActivityId(), p6AtivityDTO.getActivityId());
			Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
			Assert.assertEquals(p6Activity.getActivityName(), p6AtivityDTO.getActivityName());
			Assert.assertEquals(p6Activity.geteGIUDF(), p6AtivityDTO.geteGIUDF());
			Assert.assertEquals(p6Activity.getAddressUDF(), p6AtivityDTO.getAddressUDF());
			Assert.assertEquals(p6Activity.getEllipseStandardJobUDF(), p6AtivityDTO.getEllipseStandardJobUDF());
			Assert.assertEquals(p6Activity.getEquipmentCodeUDF(), p6AtivityDTO.getEquipmentCodeUDF());
			Assert.assertEquals(p6Activity.getEquipmentNoUDF(), p6AtivityDTO.getEquipmentNoUDF());
			Assert.assertEquals(p6Activity.getEstimatedLabourHoursUDF(), p6AtivityDTO.getEstimatedLabourHoursUDF());
			Assert.assertEquals(p6Activity.getFeederUDF(), p6AtivityDTO.getFeederUDF());
			Assert.assertEquals(p6Activity.getActivityJDCodeUDF(), p6AtivityDTO.getActivityJDCodeUDF());
			Assert.assertEquals(p6Activity.getLocationInStreetUDF(), p6AtivityDTO.getLocationInStreetUDF());
			Assert.assertEquals(p6Activity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
			Assert.assertEquals(p6Activity.getPlannedStartDate(), p6AtivityDTO.getPlannedStartDate());

			Assert.assertEquals(p6Activity.getPickIdUDF(), p6AtivityDTO.getPickIdUDF());
			Assert.assertEquals(p6Activity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
			Assert.assertEquals(p6Activity.getRequiredByDateUDF(), p6AtivityDTO.getRequiredByDateUDF());
			Assert.assertEquals(p6Activity.getTaskDescriptionUDF(), p6AtivityDTO.getTaskDescriptionUDF());
			Assert.assertEquals(p6Activity.getActivityStatus(), p6AtivityDTO.getActivityStatus());
			Assert.assertEquals(p6Activity.getTaskUserStatusUDF(), p6AtivityDTO.getTaskUserStatusUDF());
			Assert.assertEquals(p6Activity.getUpStreamSwitchUDF(), p6AtivityDTO.getUpStreamSwitchUDF());
			Assert.assertEquals(p6Activity.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

		}
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
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MOMT2");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setTaskDescription("");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		List<EllipseActivityDTO> activities = new ArrayList<>();
		activities.add(ellipseActivity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");
		p6Activity.setActivityName("Test");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.when(p6WSClient.readActivities()).thenReturn(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration();
		Assert.assertEquals(1, deleteActivites.size());

		for (P6ActivityDTO p6AtivityDTO : deleteActivites) {
			Assert.assertEquals(p6Activity.getActivityId(), p6AtivityDTO.getActivityId());
			Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
			Assert.assertEquals(p6Activity.getActivityName(), p6AtivityDTO.getActivityName());
			Assert.assertEquals(p6Activity.geteGIUDF(), p6AtivityDTO.geteGIUDF());
			Assert.assertEquals(p6Activity.getAddressUDF(), p6AtivityDTO.getAddressUDF());
			Assert.assertEquals(p6Activity.getEllipseStandardJobUDF(), p6AtivityDTO.getEllipseStandardJobUDF());
			Assert.assertEquals(p6Activity.getEquipmentCodeUDF(), p6AtivityDTO.getEquipmentCodeUDF());
			Assert.assertEquals(p6Activity.getEquipmentNoUDF(), p6AtivityDTO.getEquipmentNoUDF());
			Assert.assertEquals(p6Activity.getEstimatedLabourHoursUDF(), p6AtivityDTO.getEstimatedLabourHoursUDF());
			Assert.assertEquals(p6Activity.getFeederUDF(), p6AtivityDTO.getFeederUDF());
			Assert.assertEquals(p6Activity.getActivityJDCodeUDF(), p6AtivityDTO.getActivityJDCodeUDF());
			Assert.assertEquals(p6Activity.getLocationInStreetUDF(), p6AtivityDTO.getLocationInStreetUDF());
			Assert.assertEquals(p6Activity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
			Assert.assertEquals(p6Activity.getPlannedStartDate(), p6AtivityDTO.getPlannedStartDate());

			Assert.assertEquals(p6Activity.getPickIdUDF(), p6AtivityDTO.getPickIdUDF());
			Assert.assertEquals(p6Activity.getRemainingDuration(), p6AtivityDTO.getRemainingDuration(), 0);
			Assert.assertEquals(p6Activity.getRequiredByDateUDF(), p6AtivityDTO.getRequiredByDateUDF());
			Assert.assertEquals(p6Activity.getTaskDescriptionUDF(), p6AtivityDTO.getTaskDescriptionUDF());
			Assert.assertEquals(p6Activity.getActivityStatus(), p6AtivityDTO.getActivityStatus());
			Assert.assertEquals(p6Activity.getTaskUserStatusUDF(), p6AtivityDTO.getTaskUserStatusUDF());
			Assert.assertEquals(p6Activity.getUpStreamSwitchUDF(), p6AtivityDTO.getUpStreamSwitchUDF());
			Assert.assertEquals(p6Activity.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

		}
		
	}

	@After
	public void testClearApplicationMemory() {
		p6EllipseIntegrationService.clearApplicationMemory();
	}

}
