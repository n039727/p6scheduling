package au.com.wp.corp.p6.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import au.com.wp.corp.p6.businessservice.P6SchedulingService;
import au.com.wp.corp.p6.businessservice.P6SchedulingserviceImpl;

@Configuration
@ComponentScan("au.com.wp.corp.p6")
@EnableWebMvc
@PropertySource("classpath:application.properties")
public class AppConfig {

	@Bean
    public P6SchedulingService welcomeservice(){
    	
    	return new P6SchedulingserviceImpl();
    }
}
