/**
 * 
 */
package au.com.wp.corp.p6.exception;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.com.wp.corp.p6.test.config.AppConfig;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { AppConfig.class })
public class P6ExceptionTest {

	@Test
	public void testP6BaseException()
	{
		Exception exc = new Exception();
		
		P6BaseException e = new P6BaseException();
		Assert.assertNull(e.getMessage());
		
		e = new P6BaseException("test exception");
		Assert.assertEquals("test exception", e.getMessage());
		e = new P6BaseException(exc);
		Assert.assertEquals(e.getCause(), exc);
		e = new P6BaseException("test exception", exc);
		Assert.assertEquals("test exception", e.getMessage());
		Assert.assertEquals(e.getCause(), exc);
		e = new P6BaseException("test exception", exc, true, true);
		Assert.assertEquals("test exception", e.getMessage());
		Assert.assertEquals(e.getCause(), exc);
		
		
	}
	
	
	@Test
	public void testP6BusinessException()
	{
		Exception exc = new Exception();
		
		P6BaseException e = new P6BusinessException();
		Assert.assertNull(e.getMessage());
		
		e = new P6BusinessException("test exception");
		Assert.assertEquals("test exception", e.getMessage());
		e = new P6BusinessException(exc);
		Assert.assertEquals(e.getCause(), exc);
		e = new P6BusinessException("test exception", exc);
		Assert.assertEquals("test exception", e.getMessage());
		Assert.assertEquals(e.getCause(), exc);
		e = new P6BusinessException("test exception", exc, true, true);
		Assert.assertEquals("test exception", e.getMessage());
		Assert.assertEquals(e.getCause(), exc);
		
	}
	
	

	@Test
	public void testP6DataAccessException()
	{
		Exception exc = new Exception();
		
		P6BaseException e = new P6DataAccessException();
		Assert.assertNull(e.getMessage());
		
		e = new P6DataAccessException("test exception");
		Assert.assertEquals("test exception", e.getMessage());
		e = new P6DataAccessException(exc);
		Assert.assertEquals(e.getCause(), exc);
		e = new P6DataAccessException("test exception", exc);
		Assert.assertEquals("test exception", e.getMessage());
		Assert.assertEquals(e.getCause(), exc);
		e = new P6DataAccessException("test exception", exc, true, true);
		Assert.assertEquals("test exception", e.getMessage());
		Assert.assertEquals(e.getCause(), exc);
		
	}
	
	

	@Test
	public void testP6ServiceException()
	{
		Exception exc = new Exception();
		
		P6BaseException e = new P6ServiceException();
		Assert.assertNull(e.getMessage());
		
		e = new P6ServiceException("test exception");
		Assert.assertEquals("test exception", e.getMessage());
		e = new P6ServiceException(exc);
		Assert.assertEquals(e.getCause(), exc);
		e = new P6ServiceException("test exception", exc);
		Assert.assertEquals(e.getCause(), exc);
		Assert.assertEquals("test exception", e.getMessage());
		e = new P6ServiceException("test exception", exc, true, true);
		Assert.assertEquals("test exception", e.getMessage());
		Assert.assertEquals(e.getCause(), exc);
		
		
	}
}
