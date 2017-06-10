package au.com.wp.corp.p6.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import au.com.wp.corp.p6.aspect.P6PortalLoggingAspect;

@Configuration
@EnableWebMvc
@ComponentScan("au.com.wp.corp.p6")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySource("file:/${properties.dir}/p6portal.properties")
@EnableTransactionManagement
@EnableAsync
public class AppConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	private Environment environment; 
	
	@Bean
    public P6PortalLoggingAspect myAspect() {
        return new P6PortalLoggingAspect();
    }
	
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		return dataSourceLookup.getDataSource(environment.getProperty("p6.portal.jndi.datasource"));
	}
	
	@Bean(name = "elipseDataSource")
	public DataSource getElipsDataSource() {
		JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
		return dataSourceLookup.getDataSource(environment.getProperty("elips.portal.jndi.datasource"));
		/*DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl("jdbc:oracle:thin:@oracle-dwdev:1521:dwdev");
		dataSource.setUsername("NELLDAPI7");
		dataSource.setPassword("Gfsdhy76657sdsd");
		return dataSource;*/
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
	@Autowired
	@Bean(name = "elipsSessionFactory")
	public SessionFactory getElipsSessionFactory(DataSource elipseDataSource) {
	    LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(elipseDataSource);
	    sessionBuilder.scanPackages("au.com.wp.corp.p6.model.elipse");
	    sessionBuilder.setProperty("hibernate.show_sql", "true");
	    Properties properties = new Properties();
	    properties.put("hibernate.show_sql", "true");
	    properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
	    properties.put("hibernate.default_schema" ,"NELL");
	    sessionBuilder.addProperties(properties);
	    return sessionBuilder.buildSessionFactory();
	}

	private Properties getHibernateProperties() {
	    Properties properties = new Properties();
	    properties.put("hibernate.show_sql", "true");
	    properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
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
	/*@Bean(autowire= Autowire.BY_NAME,name = "p6Executor")
	@Qualifier("p6Executor")
    public TaskExecutor p6Executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(100);
        executor.initialize();
        return executor;
    }*/
}
