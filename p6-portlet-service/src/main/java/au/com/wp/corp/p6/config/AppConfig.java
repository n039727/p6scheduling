package au.com.wp.corp.p6.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.businessservice.impl.P6SchedulingBusinessServiceImpl;

@Configuration
@ComponentScan("au.com.wp.corp.p6")
@EnableWebMvc
@PropertySource("classpath:application.properties")
public class AppConfig {

	@Bean
    public P6SchedulingBusinessService p6Service(){
    	
    	return new P6SchedulingBusinessServiceImpl();
    }
}
