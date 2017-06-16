/**
 * 
 */
package au.com.wp.corp.p6.integration.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.integration.util.DateUtil;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class DateUtilTest {

	@Autowired
	DateUtil dateUtil;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testGetCurrentDate() {
		String currentDate = dateUtil.getCurrentDate();
		Assert.assertNotNull(currentDate);
	}

	@Test
	public void testGetStartDateOfFiscalYear() {
		String fisrtDayFY = dateUtil.getStartDateOfFiscalYear(new Date());
		Assert.assertNotNull(fisrtDayFY);
	}

	@Test
	public void testCovertDatetoString() {
		Assert.assertNotNull(dateUtil.convertDateToString("2017-05-28T08:00:00",
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP));
	}

	@Test
	public void testConvertStringToXMLGregorianClalander() {
		Assert.assertNotNull(dateUtil.convertStringToXMLGregorianClalander("2017-04-19T08:00:00"));
	}

	@Test
	public void testConvertStringToXMLGregorianClalander_Error1() {
		Assert.assertNull(dateUtil.convertStringToXMLGregorianClalander("08:00:00"));
	}

	@Test
	public void testConvertDateToString() {
		String date = dateUtil.convertDateToString("09/06/2017 08:00:00", DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP);
		Assert.assertEquals("2017-06-09T08:00:00", date);

	}

	@Test
	public void testConvertDateToString_Error1() {
		String date = dateUtil.convertDateToString("09/06/2017", DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
				DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP);

		Assert.assertNull(date);

	}

	@Test
	public void testIsCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		Assert.assertTrue(dateUtil.isCurrentDate(sdf.format(new Date())));
	}

	@Test
	public void testIsCurrentDate_Error1() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

		Assert.assertFalse(dateUtil.isCurrentDate(sdf.format(new Date())));
	}

	@Test
	public void testIsSameDate() {
		Assert.assertTrue(dateUtil.isSameDate("2017-03-13T00:00:00", DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
				"13/03/2017 08:00:00", DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
	}

	@Test
	public void testIsSameDate1() {
		Assert.assertTrue(dateUtil.isSameDate("2017-06-20T08:00:00", DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
				"20/06/2017 08:00:00", DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
	}

	@Test
	public void testIsSameDate_ERORR1() {
		Assert.assertFalse(dateUtil.isSameDate("2017-06-20", DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
				"20/06/2017 08:00:00", DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
	}
	
	@Test
	public void testIsSameDate_ERROR2() {
		Assert.assertFalse(dateUtil.isSameDate("2017-06-21T08:00:00", DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
				"20/06/2017 08:00:00", DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP));
	}
	
	@Test
	public void testCompare (){
		String actualStartDate = "2017-06-20T08:01:00";
		String actualEndDate = "2017-06-20T08:00:00";
		
		int status = dateUtil.compare(actualStartDate, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP,
				actualEndDate, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP);
		Assert.assertEquals(1, status);
	}
	
	@Test
	public void testSubstractMinuteFromDate (){
		String excpectedActualStartDate = "2017-06-20T08:00:00";
		String actualEndDate = "2017-06-20T08:01:00";
		
		String actualStartDate = dateUtil.substractMinuteFromDate(actualEndDate, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP);
		
		
		Assert.assertEquals(excpectedActualStartDate, actualStartDate);
		
	}
	
}
