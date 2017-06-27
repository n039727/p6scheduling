/**
 * 
 */
package au.com.wp.corp.p6.integration.dto;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.integration.dto.EllipseActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ActivityDTO;
import au.com.wp.corp.p6.integration.dto.P6ProjWorkgroupDTO;
import au.com.wp.corp.p6.integration.dto.UDFTypeDTO;
import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039126
 *
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class P6IntegrationDTOTest {

	@Test
	public void testActivityDTO () {
		P6ActivityDTO activityDTO = new P6ActivityDTO();
		Date date = new Date();
		activityDTO.setActivityId("8095956633283");
		activityDTO.setActivityJDCodeUDF("JD123");
		activityDTO.setActivityName("Test Name");
		activityDTO.setActivityStatus("In Progress");
		activityDTO.setAddressUDF("Perth WA");
		activityDTO.seteGIUDF("EGI123");
		activityDTO.setEquipmentCodeUDF("E123");
		activityDTO.setEquipmentNoUDF("12345");
		activityDTO.setLocationInStreetUDF("Willington St.");
		activityDTO.setRemainingDuration(20.0);
		activityDTO.setOriginalDuration(20.0);
		activityDTO.setEstimatedLabourHours(20.0);
		activityDTO.setPickIdUDF("P123");
		activityDTO.setPlannedStartDate(date.toString());
		activityDTO.setRequiredByDateUDF(date.toString());
		activityDTO.setSlippageCodeUDF("S123");
		activityDTO.setTaskDescriptionUDF("Test Task");
		activityDTO.setTaskUserStatusUDF("In Progress");
		
		Assert.assertEquals("8095956633283", activityDTO.getActivityId());
		Assert.assertEquals("JD123", activityDTO.getActivityJDCodeUDF());
		Assert.assertEquals("Test Name", activityDTO.getActivityName());
		Assert.assertEquals("In Progress", activityDTO.getActivityStatus());
		Assert.assertEquals("Perth WA", activityDTO.getAddressUDF());
		Assert.assertEquals("EGI123", activityDTO.geteGIUDF());
		Assert.assertEquals("E123", activityDTO.getEquipmentCodeUDF());
		Assert.assertEquals("12345", activityDTO.getEquipmentNoUDF());
		Assert.assertEquals("Willington St.", activityDTO.getLocationInStreetUDF());
		Assert.assertEquals(20.0, activityDTO.getOriginalDuration(),0);
		Assert.assertEquals(20.0, activityDTO.getRemainingDuration(),0);
		Assert.assertEquals("P123", activityDTO.getPickIdUDF());
		Assert.assertEquals(date.toString(), activityDTO.getPlannedStartDate());
		Assert.assertEquals(date.toString(), activityDTO.getRequiredByDateUDF());
		Assert.assertEquals("S123", activityDTO.getSlippageCodeUDF());
		Assert.assertEquals("Test Task", activityDTO.getTaskDescriptionUDF());
		Assert.assertEquals("In Progress", activityDTO.getTaskUserStatusUDF());
		Assert.assertNotNull(activityDTO.hashCode());
		
		
		P6ActivityDTO activityDTO1 = new P6ActivityDTO();
		activityDTO1.setActivityId(activityDTO.getActivityId());
		
		Assert.assertTrue(activityDTO.equals(activityDTO1));
		Assert.assertTrue(activityDTO.equals(activityDTO));
		Assert.assertFalse(activityDTO.equals(null));
		Assert.assertFalse(activityDTO.equals(new Object()));
		activityDTO1.setActivityId("12234");
		Assert.assertFalse(activityDTO.equals(activityDTO1));
		activityDTO1.setActivityId(null);
		Assert.assertFalse(activityDTO.equals(activityDTO1));
	}
	
	@Test
	public void testEllipseActivityDTO (){
		EllipseActivityDTO ellipseDTO = new EllipseActivityDTO();
		ellipseDTO.setAddress("Perth");
		ellipseDTO.setEGI("EGI123");
		ellipseDTO.setEllipseStandardJob("STJOb");
		ellipseDTO.setEquipmentCode("EQ123");
		ellipseDTO.setEquipmentNo("EN123");
		ellipseDTO.setEstimatedLabourHours("8.0");
		ellipseDTO.setFeeder("Feeder");
		ellipseDTO.setJdCode("JD123");
		ellipseDTO.setLocationInStreet("LocationStreet");
		ellipseDTO.setSlippageCode("SP123");
		
	}
	
	@Test
	public void testP6ProjWorkgroupDTO (){
		P6ProjWorkgroupDTO projWorkgroupDTO = new P6ProjWorkgroupDTO();
		projWorkgroupDTO.setRoleId(123);
		projWorkgroupDTO.setResourceName("Test resource");
		projWorkgroupDTO.setRoleName("test role");
		projWorkgroupDTO.setPrimaryResourceYN("Y");
		
		Assert.assertEquals(123, projWorkgroupDTO.getRoleId());
		Assert.assertEquals("Test resource", projWorkgroupDTO.getResourceName());
		Assert.assertEquals("test role", projWorkgroupDTO.getRoleName());
		Assert.assertEquals("Y", projWorkgroupDTO.getPrimaryResourceYN());
	}
	
	@Test
	public void testUDFTypeDTO (){
		UDFTypeDTO udfType = new UDFTypeDTO();
		udfType.setDataType("Text");
		Assert.assertEquals("Text", udfType.getDataType());
	}
}
