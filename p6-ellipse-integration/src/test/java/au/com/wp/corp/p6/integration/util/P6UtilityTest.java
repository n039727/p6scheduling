/**
 * 
 */
package au.com.wp.corp.p6.integration.util;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.wp.corp.p6.integration.util.P6Utility;

/**
 * @author N039126
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/servlet-context.xml" })
public class P6UtilityTest {

	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testCovertStringToDouble () {
		double value = P6Utility.covertStringToDouble("2.0");
		Assert.assertEquals(2.0, value, 0);
	}
	
	@Test
	public void testCovertStringToDouble_Error1 () {
		double value = P6Utility.covertStringToDouble("");
		Assert.assertEquals(0, value, 0);
	}

	
	@Test
	public void testCovertStringToLong () {
		long value = P6Utility.covertStringToLong("7");
		Assert.assertEquals(7, value);
	}
	
	@Test
	public void testCovertStringToLong_Error1 () {
		long value = P6Utility.covertStringToLong("");
		Assert.assertEquals(0, value);
	}
	
	
	@Test
	public void testCovertStringToInteger () {
		int value = P6Utility.covertStringToInteger("7");
		Assert.assertEquals(7, value);
	}

	@Test
	public void testCovertStringToInteger_Error1 () {
		int value = P6Utility.covertStringToInteger("");
		Assert.assertEquals(0, value);
	}
	
	
	@Test
	public void testCovertDoubleToString () {
		String value = P6Utility.covertDoubleToString(7.0);
		Assert.assertEquals("7.0", value);
	}
	
	@Test
	public void testCovertIntegerToString () {
		String value = P6Utility.covertIntegerToString(7);
		Assert.assertEquals("7", value);
	}
	
	@Test
	public void testCovertLongToString () {
		String value = P6Utility.covertLongToString(7L);
		Assert.assertEquals("7", value);
	}

	@Test
	public void testIsEqual(){
		Assert.assertTrue(P6Utility.isEqual(7.0, 7.0));
	}
	
	@Test
	public void testIsEqual1(){
		Assert.assertFalse(P6Utility.isEqual(7.0, 6.0));
	}
	
	@Test
	public void testGetStreetName () {
		Assert.assertEquals("Street Name", P6Utility.getStreetName("1 Street Name"));
	}
}

