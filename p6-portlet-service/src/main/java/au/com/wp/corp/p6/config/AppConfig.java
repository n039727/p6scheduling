package au.com.wp.corp.p6.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl;
import au.com.wp.corp.p6.dataservice.TaskDAO;
import au.com.wp.corp.p6.dataservice.impl.TaskDAOImpl;

@Configuration
@ComponentScan("au.com.wp.corp.p6")
@EnableWebMvc
@PropertySource("file:/${properties.dir}/application.properties")
@EnableTransactionManagement
public class AppConfig {

	@Autowired
	private Environment environment; 
	
	
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		return dataSourceLookup.getDataSource(environment.getProperty("jndi.datasource"));
	  
	}
	
	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) {
	 
	    LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
	    sessionBuilder.scanPackages("au.com.wp.corp.p6.model");
	    sessionBuilder.setProperty("hibernate.show_sql", "true");
	    
	    sessionBuilder.addProperties(getHibernateProperties());
	    return sessionBuilder.buildSessionFactory();
	}
	
	
	private Properties getHibernateProperties() {
	    Properties properties = new Properties();
	    properties.put("hibernate.show_sql", "true");
	    properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	    properties.put("hibernate.default_schema" ,"P6PORTAL");
	    return properties;
	}
	
	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(
	        SessionFactory sessionFactory) {
	    HibernateTransactionManager transactionManager = new HibernateTransactionManager(
	            sessionFactory);
	 
	    return transactionManager;
	}
	
	@Autowired
	@Bean(name = "taskDAO")
	public TaskDAO gettaskDAO() {
	   
	    return new TaskDAOImpl();
	}
	
	
}
