/**
 * 
 */
package au.com.wp.corp.p6.integration.business;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import au.com.wp.corp.p6.integration.business.impl.P6EllipseIntegrationServiceImpl;
import au.com.wp.corp.p6.integration.dao.P6EllipseDAOImpl;
import au.com.wp.corp.p6.integration.dao.P6PortalDAOImpl;
import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.integration.exception.P6ExceptionType;
import au.com.wp.corp.p6.integration.exception.P6IntegrationExceptionHandler;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.util.EllipseReadParameter;
import au.com.wp.corp.p6.integration.util.P6ReloadablePropertiesReader;
import au.com.wp.corp.p6.integration.wsclient.cleint.impl.P6WSClientImpl;
import au.com.wp.corp.p6.integration.wsclient.ellipse.impl.EllipseWSClientImpl;

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
	EllipseWSClientImpl ellipseWSClient;

	@Mock
	DateUtil dateUtil;

	@Mock
	P6IntegrationExceptionHandler exceptionHandler;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	List<String> workgroupList = null;
	Map<String, Integer> projWorkgroupDTOs = new HashMap<>();

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

		projWorkgroupDTOs.put("MONT1", 12345);
		projWorkgroupDTOs.put("MOMT2", 12346);
		projWorkgroupDTOs.put("MOMT3", 12346);
		projWorkgroupDTOs.put("NGERT01", 12347);
		projWorkgroupDTOs.put("NGERSCH", 12348);

		Mockito.when(p6WSClient.readResources()).thenReturn(projWorkgroupDTOs);

		Map<String, Integer> projectsMap = new HashMap<>();
		projectsMap.put("DxMetro", 263779);
		projectsMap.put("DxNorth", 263780);

		Mockito.when(p6WSClient.readProjects()).thenReturn(projectsMap);

		p6EllipseIntegrationService.readProjectWorkgroupMapping();

		workgroupList = new ArrayList<>();

		if (P6ReloadablePropertiesReader.getProperty("INTEGRATION_RUN_STARTEGY")
				.equals(EllipseReadParameter.ALL.name())) {
			final Set<String> keys = CacheManager.getProjectWorkgroupListMap().keySet();

			for (String key : keys) {
				workgroupList.addAll(CacheManager.getProjectWorkgroupListMap().get(key));
			}
		}
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
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, null);

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
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, null);

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
		ellipseActivity.setEstimatedLabourHours("10.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(2.00);
		ellipseActivity.setPlannedStartDate("");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2018 08:00:00");
		ellipseActivity.setTaskDescription("");
		ellipseActivity.setUpStreamSwitch("DOF 8473");

		List<EllipseActivityDTO> activities = new ArrayList<>();
		activities.add(ellipseActivity);

		String startDateOfFiscalYear = dateUtil.getStartDateOfFiscalYear(ellipseActivity.getRequiredByDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP);
		Mockito.when(dateUtil.getStartDateOfFiscalYear(ellipseActivity.getRequiredByDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP)).thenReturn(startDateOfFiscalYear);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);
		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, null);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertEquals(ellipseActivity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
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
		ellipseActivity.setEstimatedLabourHours("10.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(2.00);
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
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, p6Activity.getWorkGroup());

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
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

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(p6Activity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * An activity will be created in P6 if doesn't exist along with
	 * 
	 * original duration value 0 if the estimated labors hours is 0; and
	 * 
	 * estimated labors hours value 0
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testConstructP6ActivityDTO_US635_AC1() throws P6BusinessException, NoSuchMethodException,
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
		ellipseActivity.setEstimatedLabourHours("0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(0);
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
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, p6Activity.getWorkGroup());

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(p6AtivityDTO.getEstimatedLabourHours(), 0, 0);
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(p6AtivityDTO.getOriginalDuration(), 0, 0);
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

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(p6Activity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * An activity will be created in P6 if doesn't exist along with
	 * 
	 * original duration value 1 if the estimated labors hours is greater than 0
	 * and less than 1; and
	 * 
	 * estimated labors hours value 1
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testConstructP6ActivityDTO_US635_AC2() throws P6BusinessException, NoSuchMethodException,
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
		ellipseActivity.setEstimatedLabourHours("0.81");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(0);
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
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, p6Activity.getWorkGroup());

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(1, p6AtivityDTO.getEstimatedLabourHours(), 0);
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(1, p6AtivityDTO.getOriginalDuration(), 0);
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

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(p6Activity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * An activity will be created in P6 if doesn't exist along with
	 * 
	 * original duration value 1 if the estimated labors hours is greater than
	 * or equal to 1 and original duration value is less than 5; and
	 * 
	 * estimated labors hours value as it is in ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testConstructP6ActivityDTO_US635_AC3() throws P6BusinessException, NoSuchMethodException,
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
		ellipseActivity.setEstimatedLabourHours("4.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(1);
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
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, p6Activity.getWorkGroup());

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(4.0, p6AtivityDTO.getEstimatedLabourHours(), 0);
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(1, p6AtivityDTO.getOriginalDuration(), 0);
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

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(p6Activity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * An activity will be created in P6 if doesn't exist along with
	 * 
	 * original duration value as it is in ellipse if the estimated labors hours
	 * is greater than or equal to 5 and
	 * 
	 * estimated labors hours value it is in ellipse
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testConstructP6ActivityDTO_US635_AC4() throws P6BusinessException, NoSuchMethodException,
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
		ellipseActivity.setEstimatedLabourHours("12.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("");
		ellipseActivity.setLocationInStreet("");
		ellipseActivity.setOriginalDuration(2);
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
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT2");

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, p6Activity.getWorkGroup());

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());
		Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
		Assert.assertEquals(ellipseActivity.getWorkOrderDescription(), p6AtivityDTO.getActivityName());
		Assert.assertEquals(ellipseActivity.getEGI(), p6AtivityDTO.geteGIUDF());
		Assert.assertEquals(ellipseActivity.getAddress(), p6AtivityDTO.getAddressUDF());
		Assert.assertEquals(ellipseActivity.getEllipseStandardJob(), p6AtivityDTO.getEllipseStandardJobUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentCode(), p6AtivityDTO.getEquipmentCodeUDF());
		Assert.assertEquals(ellipseActivity.getEquipmentNo(), p6AtivityDTO.getEquipmentNoUDF());
		Assert.assertEquals(12.0, p6AtivityDTO.getEstimatedLabourHours(), 0);
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(2, p6AtivityDTO.getOriginalDuration(), 0);
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

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(p6Activity.getWorkGroup());
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
		ellipseActivity.setTaskUserStatus("00");
		ellipseActivity.setTaskStatus("Completed");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("11/06/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setActualStartDate("11/06/2012 08:00:00");
		ellipseActivity.setActualFinishDate("12/06/2012 08:00:00");
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

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getActualStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getActualFinishDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-12T08:00:00");

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
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());
		Assert.assertEquals(
				dateUtil.convertDateToString(ellipseActivity.getActualFinishDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP),
				p6AtivityDTO.getActualFinishDate());
		Assert.assertEquals(
				dateUtil.convertDateToString(ellipseActivity.getActualStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP),
				p6AtivityDTO.getActualStartDate());
		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * 
	 * On WO task their are changes in the data which flows to P6 when comparing
	 * the data between P6 and Ellipse then P6 needs to be updated with the new
	 * data (based on the condition and master system)
	 * 
	 * if the task status is not Completed, the actual start date and actual
	 * finish date should not be updated
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC1_ERROR() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERT01");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("00");
		ellipseActivity.setTaskStatus("Not Started");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("11/06/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setActualStartDate("11/06/2012 08:00:00");
		ellipseActivity.setActualFinishDate("12/06/2012 08:00:00");
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

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getActualStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getActualFinishDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-12T08:00:00");

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
		Assert.assertNull(p6AtivityDTO.getActualFinishDate());
		Assert.assertNull(p6AtivityDTO.getActualStartDate());
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

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-05-27T08:00:00");

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
	public void testSyncEllipseP6Activity_US301_AC2_1() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-05-27T08:00:00");

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
		Assert.assertNotNull(p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * 
	 * if the original duration is 0 and estimated labors hours is 0 in ellipse,
	 * then update P6 with same value *
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC_PLANNEDDURATION_1()
			throws P6BusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
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
		ellipseActivity.setEstimatedLabourHours("0.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(0);
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
		p6Activity.setEstimatedLabourHours(4);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(1);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(8.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);
		p6Activity.setTaskUserStatusUDF("AL");

		Mockito.when(dateUtil.isCurrentDate(sdf.format(date))).thenReturn(true);

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
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertNotNull(p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * 
	 * if estimated labors hours is greater than 0 and less than 1 in ellipse,
	 * then update P6 with original duration 1 and estimated labors hours 1 *
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC_PLANNEDDURATION_2()
			throws P6BusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
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
		ellipseActivity.setEstimatedLabourHours("0.67");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(0);
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
		p6Activity.setEstimatedLabourHours(4);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(0);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(8.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);
		p6Activity.setTaskUserStatusUDF("AL");

		Mockito.when(dateUtil.isCurrentDate(sdf.format(date))).thenReturn(true);

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
		Assert.assertEquals(p6AtivityDTO.getEstimatedLabourHours(), 1, 0);
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(1, p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertNotNull(p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * 
	 * if the estimated labors hours is greater than or equal to 1 and less than
	 * 5 in ellipse,
	 * 
	 * then update P6 with estimated labors units with same vale as ellipse and
	 * original duration as 1
	 * 
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC_PLANNEDDURATION_3()
			throws P6BusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
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
		ellipseActivity.setEstimatedLabourHours("4.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(1);
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
		p6Activity.setEstimatedLabourHours(0);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(0);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(8.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);
		p6Activity.setTaskUserStatusUDF("AL");

		Mockito.when(dateUtil.isCurrentDate(sdf.format(date))).thenReturn(true);

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
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(p6AtivityDTO.getOriginalDuration(), 1, 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertNotNull(p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());

	}

	/**
	 * 
	 * if the estimated labors hours is greater than or equal to 5 in ellipse,
	 * 
	 * then update P6 with estimated labors units with same vale as ellipse and
	 * original duration as same value in ellipse
	 * 
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncEllipseP6Activity_US301_AC_PLANNEDDURATION_4()
			throws P6BusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
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
		ellipseActivity.setEstimatedLabourHours("10.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test Location");
		ellipseActivity.setOriginalDuration(2);
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
		p6Activity.setEstimatedLabourHours(4);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(1);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(8.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);
		p6Activity.setTaskUserStatusUDF("AL");

		Mockito.when(dateUtil.isCurrentDate(sdf.format(date))).thenReturn(true);

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
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(p6AtivityDTO.getOriginalDuration(), 2, 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertNotNull(p6AtivityDTO.getTaskUserStatusUDF());
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
		ellipseActivity.setTaskUserStatus("AL");
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

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncEllipseP6Activity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, p6Activity,
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

		Assert.assertEquals("", p6AtivityDTO.getExecutionPckgUDF());
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
		ellipseActivity.setTaskUserStatus("AL");
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

		Assert.assertNull(p6AtivityDTO.getExecutionPckgUDF());

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
		p6Activity.setTaskUserStatusUDF("MR");
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
		p6Activity.setEstimatedLabourHours(7.00);
		p6Activity.setFeederUDF("");
		p6Activity.setLocationInStreetUDF("");
		p6Activity.setOriginalDuration(7.00);
		p6Activity.setPickIdUDF("");
		p6Activity.setPlannedStartDate("2012-06-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2012-07-10 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setTaskUserStatusUDF("RR");
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
		Assert.assertEquals("NULL", ellipseActivityDTO.getPlannedStartDate());
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
		ellipseActivity.setWorkGroup("MONT2");
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
		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("MOMT2"))).thenReturn(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("MOMT2"));
		Assert.assertEquals(0, deleteActivites.size());

		for (P6ActivityDTO p6AtivityDTO : deleteActivites) {
			Assert.assertEquals(p6Activity.getActivityId(), p6AtivityDTO.getActivityId());
			Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
			Assert.assertEquals(p6Activity.getActivityName(), p6AtivityDTO.getActivityName());
			Assert.assertEquals(p6Activity.geteGIUDF(), p6AtivityDTO.geteGIUDF());
			Assert.assertEquals(p6Activity.getAddressUDF(), p6AtivityDTO.getAddressUDF());
			Assert.assertEquals(p6Activity.getEllipseStandardJobUDF(), p6AtivityDTO.getEllipseStandardJobUDF());
			Assert.assertEquals(p6Activity.getEquipmentCodeUDF(), p6AtivityDTO.getEquipmentCodeUDF());
			Assert.assertEquals(p6Activity.getEquipmentNoUDF(), p6AtivityDTO.getEquipmentNoUDF());
			Assert.assertEquals(String.valueOf(p6Activity.getEstimatedLabourHours()),
					String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
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

		Map<String, EllipseActivityDTO> ellipseActivities = CacheManager.getEllipseActivitiesMap();

		ellipseActivities.put(ellipseActivity.getWorkOrderTaskId(), ellipseActivity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("Y3940943002");
		p6Activity.setWorkGroup("MOMT2");
		p6Activity.setActivityName("Test");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("MOMT2"))).thenReturn(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("MOMT2"));
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
			Assert.assertEquals(String.valueOf(p6Activity.getEstimatedLabourHours()),
					String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
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
	public void testStartEllipseToP6Integration_CreateActivity_P6_Error()
			throws P6BusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("Y3940943002");
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

		Map<String, EllipseActivityDTO> ellipseActivities = CacheManager.getEllipseActivitiesMap();

		ellipseActivities.put(ellipseActivity.getWorkOrderTaskId(), ellipseActivity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("Y3940943002");
		p6Activity.setWorkGroup("MOMT2");
		p6Activity.setActivityName("Test");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.doThrow(new P6ServiceException("invalid work order")).when(p6WSClient).createActivities(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("MOMT2"));
		Assert.assertEquals(0, deleteActivites.size());
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

		Map<String, EllipseActivityDTO> ellipseActivities = CacheManager.getEllipseActivitiesMap();

		ellipseActivities.put(ellipseActivity.getWorkOrderTaskId(), ellipseActivity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("NGERSCH");
		p6Activity.setActivityName("Test");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("NGERSCH"))).thenReturn(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("NGERSCH"));
		Assert.assertEquals(0, deleteActivites.size());

		for (P6ActivityDTO p6AtivityDTO : deleteActivites) {
			Assert.assertEquals(p6Activity.getActivityId(), p6AtivityDTO.getActivityId());
			Assert.assertEquals(p6Activity.getWorkGroup(), p6AtivityDTO.getWorkGroup());
			Assert.assertEquals(p6Activity.getActivityName(), p6AtivityDTO.getActivityName());
			Assert.assertEquals(p6Activity.geteGIUDF(), p6AtivityDTO.geteGIUDF());
			Assert.assertEquals(p6Activity.getAddressUDF(), p6AtivityDTO.getAddressUDF());
			Assert.assertEquals(p6Activity.getEllipseStandardJobUDF(), p6AtivityDTO.getEllipseStandardJobUDF());
			Assert.assertEquals(p6Activity.getEquipmentCodeUDF(), p6AtivityDTO.getEquipmentCodeUDF());
			Assert.assertEquals(p6Activity.getEquipmentNoUDF(), p6AtivityDTO.getEquipmentNoUDF());
			Assert.assertEquals(String.valueOf(p6Activity.getEstimatedLabourHours()),
					String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
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

		Map<String, EllipseActivityDTO> ellipseActivities = CacheManager.getEllipseActivitiesMap();

		ellipseActivities.put(ellipseActivity.getWorkOrderTaskId(), ellipseActivity);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT1");
		p6Activity.setActivityName("Test");
		p6Activity.setPlannedStartDate("2017-07-28'T'08:00:00");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("MOMT1"))).thenReturn(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("MOMT1"));
		Assert.assertEquals(0, deleteActivites.size());
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
	public void testStartEllipseToP6Integration_UpdateActivity_P6_Error()
			throws P6BusinessException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
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

		Map<String, EllipseActivityDTO> ellipseActivities = CacheManager.getEllipseActivitiesMap();

		ellipseActivities.put(ellipseActivity.getWorkOrderTaskId(), ellipseActivity);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT1");
		p6Activity.setActivityName("Test");
		p6Activity.setPlannedStartDate("2017-07-28'T'08:00:00");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("MOMT1"))).thenReturn(p6Activities);
		Mockito.doThrow(new P6ServiceException()).when(p6WSClient).updateActivities(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("MOMT1"));
		Assert.assertEquals(0, deleteActivites.size());
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
	public void testStartEllipseToP6Integration_UpdateActivity_P6_3() throws P6BusinessException, NoSuchMethodException,
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

		Map<String, EllipseActivityDTO> ellipseActivities = CacheManager.getEllipseActivitiesMap();

		ellipseActivities.put(ellipseActivity.getWorkOrderTaskId(), ellipseActivity);
		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT3");
		p6Activity.setActivityName("Test");
		p6Activity.setPlannedStartDate("2017-07-28'T'08:00:00");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("MOMT3"))).thenReturn(p6Activities);

		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("MOMT3"));
		Assert.assertEquals(0, deleteActivites.size());
	}

	/**
	 * throwing exception when reading activity in p6
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testStartEllipseToP6Integration_ReadError() throws P6BusinessException {
		thrown.expect(P6BusinessException.class);
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

		Map<String, EllipseActivityDTO> ellipseActivities = CacheManager.getEllipseActivitiesMap();

		ellipseActivities.put(ellipseActivity.getWorkOrderTaskId(), ellipseActivity);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943002");
		p6Activity.setWorkGroup("MOMT1");
		p6Activity.setActivityName("Test");
		p6Activity.setPlannedStartDate("2017-07-28'T'08:00:00");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenThrow(P6DataAccessException.class);
		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("MOMT1")))
				.thenThrow(new P6ServiceException(P6ExceptionType.DATA_ERROR.name()));
		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("MOMT1"));
		Assert.assertEquals(0, deleteActivites.size());
	}

	@Test
	public void testStartEllipseToP6Integration_EllipseUpdateError() throws P6BusinessException, NoSuchMethodException,
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

		List<EllipseActivityDTO> ellipseActivities = new ArrayList<>();
		ellipseActivities.add(ellipseActivity);

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

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);

		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("NGERT01"))).thenReturn(p6Activities);
		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(ellipseActivities);

		Mockito.doThrow(new P6ServiceException()).when(ellipseWSClient).updateActivitiesEllipse(ellipseActivities);
		List<P6ActivityDTO> deleteActivites = p6EllipseIntegrationService.startEllipseToP6Integration(workgroupList,
				projWorkgroupDTOs.get("MOMT1"));
		System.out.println(CacheManager.getEllipseActivitiesMap().size());
		Assert.assertEquals(0, deleteActivites.size());
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
	public void testStart() throws P6BusinessException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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

		Map<String, EllipseActivityDTO> ellipseActivities = CacheManager.getEllipseActivitiesMap();

		ellipseActivities.put(ellipseActivity.getWorkOrderTaskId(), ellipseActivity);

		P6ActivityDTO p6Activity = new P6ActivityDTO();
		p6Activity.setActivityId("03940943001");
		p6Activity.setWorkGroup("MOMT1");
		p6Activity.setActivityName("Test");
		p6Activity.setPlannedStartDate("2017-07-28'T'08:00:00");

		List<P6ActivityDTO> p6Activities = new ArrayList<>();
		p6Activities.add(p6Activity);
		Mockito.when(p6WSClient.readActivities(projWorkgroupDTOs.get("MOMT1"))).thenReturn(p6Activities);

		boolean status = p6EllipseIntegrationService.start();
		Assert.assertTrue(status);

	}

	/**
	 * Given that a WO required by date is less than WO task PSD set in P6 by
	 * scheduler when the batch job between Ellipse and P6 is running then the
	 * PSD in Ellipse should be updated to the PSD set in P6 (for both the
	 * condition CURDURFLAG = 'Y' or CURDURFLAG = 'N')
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US643_AC1() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MONT2");
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
		ellipseActivity.setCalcDurFlag("N");

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
		p6Activity.setPlannedStartDate("2017-08-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2017-07-27 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-07-27T08:00:00");

		Mockito.when(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("10/08/2017 08:00:00");

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);

		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);
		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Mockito.when(dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-08-10 08:00:00");
		Assert.assertEquals(p6Activity.getPlannedStartDate(),
				dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		Assert.assertEquals("Y", ellipseAtivityDTO.getCalcDurFlag());
	}

	/**
	 * Given that a WO required start date is greater than WO task PSD set in P6
	 * by scheduler when the batch job between Ellipse and P6 is running then
	 * the PSD in Ellipse should be updated to the PSD set in P6 (for both the
	 * condition CURDURFLAG = 'Y' or CURDURFLAG = 'N')
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US643_AC2() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MONT2");
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
		ellipseActivity.setCalcDurFlag(" ");

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
		p6Activity.setPlannedStartDate("2017-08-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2017-07-27 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-07-27T08:00:00");

		Mockito.when(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("10/08/2017 08:00:00");

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);

		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);
		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Mockito.when(dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-08-10 08:00:00");
		Assert.assertEquals(p6Activity.getPlannedStartDate(),
				dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		Assert.assertEquals("Y", ellipseAtivityDTO.getCalcDurFlag());
	}

	/**
	 * Given that a PSD set in P6 by scheduler against the WO task, is in
	 * between WO required start date and WO required by date in Ellipse when
	 * the batch job between Ellipse and P6 is running then the PSD in Ellipse
	 * should be updated to the PSD set in P6 (for both the condition CURDURFLAG
	 * = 'Y' or CURDURFLAG = 'N')
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US643_AC3() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MONT2");
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
		ellipseActivity.setCalcDurFlag("N");

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
		p6Activity.setPlannedStartDate("2017-08-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2017-07-27 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-07-27T08:00:00");

		Mockito.when(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("10/08/2017 08:00:00");

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);

		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);
		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Mockito.when(dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-08-10 08:00:00");
		Assert.assertEquals(p6Activity.getPlannedStartDate(),
				dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		Assert.assertEquals("Y", ellipseAtivityDTO.getCalcDurFlag());
	}

	/**
	 * Given that a PSD set in P6 by scheduler against the WO task, is in
	 * between WO required start date and WO required by date in Ellipse when
	 * the batch job between Ellipse and P6 is running then the PSD in Ellipse
	 * should be updated to the PSD set in P6 (for both the condition CURDURFLAG
	 * = 'Y' or CURDURFLAG = 'N')
	 * 
	 * The calc dur flag is Y so the work order updates with calc dur flag is
	 * not required.
	 * 
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US643_AC4() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MONT2");
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
		ellipseActivity.setCalcDurFlag("Y");

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
		p6Activity.setPlannedStartDate("2017-08-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2017-07-27 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-07-27T08:00:00");

		Mockito.when(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("10/08/2017 08:00:00");

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);

		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);
		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Mockito.when(dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-08-10 08:00:00");
		Assert.assertEquals(p6Activity.getPlannedStartDate(),
				dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		Assert.assertNull(ellipseAtivityDTO.getCalcDurFlag());
	}

	/**
	 * Given that a WO required by date is less than WO task PSD set in P6 by
	 * scheduler when the batch job between Ellipse and P6 is running then the
	 * PSD in Ellipse should be updated to the PSD set in P6 (for both the
	 * condition CURDURFLAG = 'Y' or CURDURFLAG = 'N')
	 * 
	 * The calc dur flag is Y so the work order updates with calc dur flag is
	 * not required.
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US643_AC5() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MONT2");
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
		ellipseActivity.setCalcDurFlag("Y");

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
		p6Activity.setPlannedStartDate("2017-08-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2017-07-27 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-07-27T08:00:00");

		Mockito.when(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("10/08/2017 08:00:00");

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);

		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);
		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Mockito.when(dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-08-10 08:00:00");
		Assert.assertEquals(p6Activity.getPlannedStartDate(),
				dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		Assert.assertNull(ellipseAtivityDTO.getCalcDurFlag());
	}

	/**
	 * Given that a WO required start date is greater than WO task PSD set in P6
	 * by scheduler when the batch job between Ellipse and P6 is running then
	 * the PSD in Ellipse should be updated to the PSD set in P6 (for both the
	 * condition CURDURFLAG = 'Y' or CURDURFLAG = 'N')
	 * 
	 * The "CALC DUR FLAG" is Y so the work order updates with "CALC DUR FLAG"
	 * is not required.
	 * 
	 * @throws P6BusinessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@Test
	public void testSyncP6EllipseActivity_US643_AC6() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("MONT2");
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
		ellipseActivity.setCalcDurFlag("Y");

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
		p6Activity.setPlannedStartDate("2017-08-10 08:00:00");
		p6Activity.setRemainingDuration(7.00);
		p6Activity.setRequiredByDateUDF("2017-07-27 08:00:00");
		p6Activity.setTaskDescriptionUDF("");
		p6Activity.setUpStreamSwitchUDF("");
		p6Activity.setProjectObjectId(263780);

		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-07-27T08:00:00");

		Mockito.when(dateUtil.convertDateToString(p6Activity.getPlannedStartDate(),
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("10/08/2017 08:00:00");

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("syncP6EllipseActivity",
				P6ActivityDTO.class, EllipseActivityDTO.class, Map.class);
		method.setAccessible(true);

		EllipseActivityDTO ellipseAtivityDTO = (EllipseActivityDTO) method.invoke(p6EllipseIntegrationService,
				p6Activity, ellipseActivity, projectWorkGropMap);
		Assert.assertEquals(p6Activity.getActivityId(), ellipseAtivityDTO.getWorkOrderTaskId());
		Mockito.when(dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2017-08-10 08:00:00");
		Assert.assertEquals(p6Activity.getPlannedStartDate(),
				dateUtil.convertDateToString(ellipseAtivityDTO.getPlannedStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
		Assert.assertNull(ellipseAtivityDTO.getCalcDurFlag());
	}

	/**
	 * Given that WO task is ready to be pulled into P6 when the Batch job
	 * between Ellipse and P6 runs then it should add the Suburb and Street Name
	 * (without the Street Number) into P6.
	 * 
	 * @throws P6BusinessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testConstructP6ActivityDTO_US655_AC1() throws P6BusinessException, NoSuchMethodException,
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
		ellipseActivity.setStreetName("1 AINGER RD");
		ellipseActivity.setSuburb("CANNINGTON");

		List<EllipseActivityDTO> activities = new ArrayList<>();
		activities.add(ellipseActivity);

		Mockito.when(p6EllipseDAO.readElipseWorkorderDetails(workgroupList)).thenReturn(activities);
		Map<String, P6ProjWorkgroupDTO> projectWorkGropMap = CacheManager.getP6ProjectWorkgroupMap();

		Method method = P6EllipseIntegrationServiceImpl.class.getDeclaredMethod("constructP6ActivityDTO",
				EllipseActivityDTO.class, Map.class, String.class);
		method.setAccessible(true);
		P6ActivityDTO p6AtivityDTO = (P6ActivityDTO) method.invoke(p6EllipseIntegrationService, ellipseActivity,
				projectWorkGropMap, null);

		Assert.assertEquals(ellipseActivity.getWorkOrderTaskId(), p6AtivityDTO.getActivityId());

		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());

		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());
		Assert.assertEquals(ellipseActivity.getSuburb(), p6AtivityDTO.getSuburbUDF());
		Assert.assertEquals("AINGER RD", p6AtivityDTO.getStreetNameUDF());
		

	}

	/**
	 * Given that I have changed the Suburb or Street Name in P6 by mistake when
	 * the next batch job between Ellipse and P6 runs then the correct Suburb
	 * and Street Name (without the street number) is updated in P6 from Ellipse
	  * 
	 * @throws P6BusinessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@Test
	public void testSyncEllipseP6Activity_US655_AC2() throws P6BusinessException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		EllipseActivityDTO ellipseActivity = new EllipseActivityDTO();
		ellipseActivity.setWorkOrderTaskId("03940943001");
		ellipseActivity.setWorkGroup("NGERT01");
		ellipseActivity.setWorkOrderDescription("TCS: HV Cross Arm");
		ellipseActivity.setTaskUserStatus("00");
		ellipseActivity.setTaskStatus("Completed");
		ellipseActivity.setEGI("PWOD");
		ellipseActivity.setEllipseStandardJob("STD01");
		ellipseActivity.setEquipmentCode("PINT");
		ellipseActivity.setEquipmentNo("000001076909");
		ellipseActivity.setEstimatedLabourHours("8.0");
		ellipseActivity.setFeeder("MSS 505.0 L327 FREMANTLE RD");
		ellipseActivity.setJdCode("EA");
		ellipseActivity.setLocationInStreet("Test location");
		ellipseActivity.setOriginalDuration(8.00);
		ellipseActivity.setPlannedStartDate("11/06/2012 08:00:00");
		ellipseActivity.setPlantNoOrPickId("S72257");
		ellipseActivity.setRemainingDuration(8.00);
		ellipseActivity.setRequiredByDate("27/07/2012 08:00:00");
		ellipseActivity.setActualStartDate("11/06/2012 08:00:00");
		ellipseActivity.setActualFinishDate("12/06/2012 08:00:00");
		ellipseActivity.setTaskDescription("Test Desc");
		ellipseActivity.setUpStreamSwitch("DOF 8473");
		ellipseActivity.setAddress("WA");
		ellipseActivity.setStreetName("1 AINGER RD");
		ellipseActivity.setSuburb("CANNINGTON");

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
		p6Activity.setSuburbUDF("WEST PERTH");
		p6Activity.setStreetNameUDF("Test Street");

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getPlannedStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getActualStartDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-11T08:00:00");

		Mockito.when(dateUtil.convertDateToString(ellipseActivity.getActualFinishDate(),
				DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP))
				.thenReturn("2012-06-12T08:00:00");

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
		Assert.assertEquals(ellipseActivity.getEstimatedLabourHours(),
				String.valueOf(p6AtivityDTO.getEstimatedLabourHours()));
		Assert.assertEquals(ellipseActivity.getFeeder(), p6AtivityDTO.getFeederUDF());
		Assert.assertEquals(ellipseActivity.getJdCode(), p6AtivityDTO.getActivityJDCodeUDF());
		Assert.assertEquals(ellipseActivity.getLocationInStreet(), p6AtivityDTO.getLocationInStreetUDF());
		Assert.assertEquals(ellipseActivity.getOriginalDuration(), p6AtivityDTO.getOriginalDuration(), 0);
		Assert.assertEquals(ellipseActivity.getPlantNoOrPickId(), p6AtivityDTO.getPickIdUDF());
		Assert.assertEquals(ellipseActivity.getRequiredByDate(), p6AtivityDTO.getRequiredByDateUDF());
		Assert.assertEquals(ellipseActivity.getTaskDescription(), p6AtivityDTO.getTaskDescriptionUDF());
		Assert.assertEquals(ellipseActivity.getTaskStatus(), p6AtivityDTO.getActivityStatus());
		Assert.assertEquals(ellipseActivity.getTaskUserStatus(), p6AtivityDTO.getTaskUserStatusUDF());
		Assert.assertEquals(ellipseActivity.getUpStreamSwitch(), p6AtivityDTO.getUpStreamSwitchUDF());
		Assert.assertEquals(
				dateUtil.convertDateToString(ellipseActivity.getActualFinishDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP),
				p6AtivityDTO.getActualFinishDate());
		Assert.assertEquals(
				dateUtil.convertDateToString(ellipseActivity.getActualStartDate(),
						DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP),
				p6AtivityDTO.getActualStartDate());
		P6ProjWorkgroupDTO projWG = CacheManager.getP6ProjectWorkgroupMap().get(ellipseActivity.getWorkGroup());
		Assert.assertEquals(projWG.getProjectObjectId(), p6AtivityDTO.getProjectObjectId());
		Assert.assertEquals(ellipseActivity.getSuburb(), p6AtivityDTO.getSuburbUDF());
		Assert.assertEquals("AINGER RD", p6AtivityDTO.getStreetNameUDF());

	}

	@After
	public void testClearApplicationMemory() {
		p6EllipseIntegrationService.clearApplicationMemory();
	}

}
