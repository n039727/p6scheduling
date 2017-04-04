package au.com.wp.corp.p6.service;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import au.com.wp.corp.p6.businessservice.P6SchedulingService;
import au.com.wp.corp.p6.businessservice.WorkOrder;

@RestController
public class P6SchedulingRestService {

    @RequestMapping("/home")
    @ResponseBody
    String home() {
        return "Hello World!";
    }
    @Inject
    P6SchedulingService welcomeservice;

    @RequestMapping(value = "/retrieveWorkOrders" , 
    		method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    List<WorkOrder> retrieveWorkOrders() {
        return welcomeservice.retrieveWorkOrders(null);
    }
    
    @RequestMapping(value = "/retrieveJobs" , 
    		method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    List<WorkOrder> retrieveJobs() {
        return welcomeservice.retrieveWorkOrders(null);
    }
}

