/**
 * 
 */
package au.com.wp.corp.p6.util;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class DateUtilTest {
	
	@Autowired
	DateUtil dateUtil;
	
	
	@Test
	public void testGetCurrentDate (){
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
		dateUtil.convertDateToString("2017-05-28T08:00:00", DateUtil.ELLIPSE_DATE_FORMAT_WITH_TIMESTAMP, DateUtil.P6_DATE_FORMAT_WITH_TIMESTAMP);
	}
	
	@Test
	public void testConvertStringToXMLGregorianClalander(){
		dateUtil.convertStringToXMLGregorianClalander("2017-04-19T08:00:00");
	}
}



