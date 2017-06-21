/**
 * 
 */
package au.com.wp.corp.p6.utils;

import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.test.config.AppConfig;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
public class DateUtilTest {

	@Autowired
	DateUtils dateUtil;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testConvertDate() {
		String date = dateUtil.convertDate("2017-05-19T00:00:00.000Z");
		Assert.assertNotNull(date);
	}

	@Test
	public void testConvertDateDDMMYYYY() {
		String date = dateUtil.convertDateDDMMYYYY("2017-05-19T00:00:00");
		Assert.assertNotNull(date);
	}

	@Test
	public void testConvertDateDDMMYYYY_1() {
		String date = dateUtil.convertDateDDMMYYYY("19/05/2017", "/");
		Assert.assertNotNull(date);
	}

	@Test
	public void testConvertDateYYYYMMDD() {
		Assert.assertNotNull(dateUtil.convertDateYYYYMMDD("2017/04/19","/"));
	}
	
	@Test
	public void testToStringYYYY_MM_DD() {
		Assert.assertNotNull(dateUtil.toStringYYYY_MM_DD(new Date()));
	}
	
	@Test
	public void testToStringDD_MM_YYYY() {
		Assert.assertNotNull(dateUtil.toStringDD_MM_YYYY(new Date()));
	}
	
	@Test
	public void testToDateFromYYYY_MM_DD() {
		Assert.assertNotNull(dateUtil.toDateFromYYYY_MM_DD("2017-04-19"));
	}
	
	@Test
	public void testGetCurrentDateWithTimeStamp() {
		Assert.assertNotNull(dateUtil.getCurrentDateWithTimeStamp());
	}
	
	@Test
	public void testGetCurrentDateWithTimeStampNoSeparator() {
		Assert.assertNotNull(dateUtil.getCurrentDateWithTimeStampNoSeparator());
	}
	
	@Test
	public void testToDateFromDD_MM_YYYY() {
		Assert.assertNotNull(dateUtil.toDateFromDD_MM_YYYY("19/04/2017"));
	}

		
}
