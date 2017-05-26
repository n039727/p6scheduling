package au.com.wp.corp.p6.test.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("au.com.wp.corp.p6")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@PropertySources({ @PropertySource("file:/${properties.dir}/p6portal.properties"),
		@PropertySource("file:/${properties.dir}/p6portal-db.properties") })
@EnableTransactionManagement
public class AppConfig {

	@Autowired
	private Environment environment;

	@Bean(name = "dataSource")
	public DriverManagerDataSource getDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(environment.getProperty("p6.db.connection.url"));
		dataSource.setUsername(environment.getProperty("p6.db.connection.username"));
		dataSource.setPassword(environment.getProperty("p6.db.connection.password"));
		return dataSource;
	}
	@Bean(name = "elipseDataSource")
	public DataSource getElipsDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setUrl(environment.getProperty("elips.db.connection.url"));
		dataSource.setUsername(environment.getProperty("elips.db.connection.username"));
		dataSource.setPassword(environment.getProperty("elips.db.connection.password"));
		return dataSource;
	}

	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DriverManagerDataSource dataSource) {
		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
		sessionBuilder.scanPackages("au.com.wp.corp.p6.model");
		sessionBuilder.setProperty("hibernate.show_sql", "true");
		sessionBuilder.addProperties(getHibernateProperties());
		SessionFactory factory = sessionBuilder.buildSessionFactory();
		System.out.println("factory == "+ factory);
		return factory;
	}
	
	@Autowired
	@Bean(name = "elipsSessionFactory")
	public SessionFactory getElipsSessionFactory(DataSource elipseDataSource) {
	    LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(elipseDataSource);
	    sessionBuilder.scanPackages("au.com.wp.corp.p6.model.elipse");
	    sessionBuilder.setProperty("hibernate.show_sql", "true");
	    sessionBuilder.addProperties(getElipsHibernateProperties());
	    SessionFactory factory = sessionBuilder.buildSessionFactory();
	    System.out.println("elipsfactory == "+ factory);
	    return factory;
	}

	private Properties getHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
		properties.put("hibernate.default_schema", "P6PORTAL");
		return properties;
	}
	
	private Properties getElipsHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.show_sql", "true");
		properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
		properties.put("hibernate.default_schema", "NELL");
		return properties;
	}

	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
		return transactionManager;
	}
	
}
