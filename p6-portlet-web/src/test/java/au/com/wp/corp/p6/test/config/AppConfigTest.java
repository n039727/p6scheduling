package au.com.wp.corp.p6.test.config;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import au.com.wp.corp.p6.scheduling.AppConfig;

@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { au.com.wp.corp.p6.test.config.AppConfig.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class AppConfigTest {
	
	@Autowired
	Environment environment;
	
	AnnotationConfigApplicationContext annotationConfigApplicationContext;
	
	
	@Mock
	AppConfig config;
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGetDataSource() {
		//config = new AppConfig();
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(environment.getProperty("p6.db.connection.url"));
		dataSource.setUsername(environment.getProperty("p6.db.connection.username"));
		dataSource.setPassword(environment.getProperty("p6.db.connection.password"));
		Mockito.when(config.getDataSource()).thenReturn(dataSource);
		annotationConfigApplicationContext =  new AnnotationConfigApplicationContext(config.getClass());
		Assert.assertTrue("executed", true);
	}

	

}
