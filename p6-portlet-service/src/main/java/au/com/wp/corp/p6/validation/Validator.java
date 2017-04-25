package au.com.wp.corp.p6.validation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.wp.corp.p6.dto.EPCreateDTO;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6ExceptionMapper;

@Component
public class Validator implements P6ExceptionMapper{

	private static final Logger logger = LoggerFactory.getLogger(Validator.class);
	
	public void validate(ExecutionPackageDTO executionPackageDTO) throws P6BusinessException{
		List<EPCreateDTO> workOrders = executionPackageDTO.getCreateDTO();
		
		if (workOrders != null && workOrders.size() > 0) {
			logger.debug("work orders size {}", workOrders.size());
			String scheduleDate = workOrders.get(0).getScheduleDate();
			for (EPCreateDTO workOrder : workOrders) {
				if(! workOrder.getScheduleDate().equals(scheduleDate)){
					throw new P6BusinessException(CREATE_EXEC_PCKG_VALIDATION_ERROR_2001);
				}
			}
			
		}

	}
}
