/**
 * 
 */
package au.com.wp.corp.p6.businessservice.impl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.IExecutionPackageService;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.utils.DateUtils;

/**
 * ExecutionPackageService performs following tasks regarding the execution
 * package such as a. createExecutionPackage b. updateExecutionPackage c.
 * mergeExecutionPackages d. splitExecutionPackage
 * 
 * 
 * @author n039126
 * @version 1.0
 */
@Service
public class ExecutionPackageServiceImpl implements IExecutionPackageService {

	private static final Logger logger = LoggerFactory.getLogger(ExecutionPackageServiceImpl.class);
	
	@Autowired
	ExecutionPackageDao executionPackageDao;
	
	@Autowired
	DateUtils dateUtils;
	
	/* (non-Javadoc)
	 * @see au.com.wp.corp.p6.businessservice.IExecutionPackageService#createOrUpdateExecutionPackage(au.com.wp.corp.p6.model.ExecutionPackage)
	 */
	@Transactional
	@Override
	public ExecutionPackageDTO createOrUpdateExecutionPackage(ExecutionPackageDTO execPackgDTO, String userName) throws P6BusinessException {
		logger.info("calling create or update execution package with # {}, and user name# {}", execPackgDTO, userName);
		execPackgDTO.setExctnPckgNam(createExceutionPackageId());
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setExctnPckgNam(execPackgDTO.getExctnPckgNam());
		executionPackage.setLeadCrewId(execPackgDTO.getLeadCrew());
		final List<WorkOrder> workOrders = execPackgDTO.getWorkOrders();
		if (workOrders != null && !workOrders.isEmpty()) {
			logger.debug("work orders size {}", workOrders.size());
			Set<Task> tasks = new HashSet<>();
			for (WorkOrder workOrder : workOrders) {
				logger.debug("For each workorder {} corresponding Task is fecthed", workOrder.getWorkOrderId());
				final Task task = executionPackageDao.getTaskbyId(workOrder.getWorkOrderId());
				if (task != null) {
					logger.debug("Task {} is fecthed", task.getTaskId());
					task.setExecutionPackage(executionPackage);
					task.setLstUpdtdUsr(userName);
					task.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
					tasks.add(task);
				} 
			}
			executionPackage.setTasks(tasks);

		}
		executionPackage.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setCrtdUsr(userName);
		executionPackage.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setLstUpdtdUsr(userName);
		executionPackageDao.createOrUpdateExecPackage(executionPackage);
		logger.info("execution package has been created with execution package id # {} ", execPackgDTO.getExctnPckgNam());
		return execPackgDTO;
		
	}
	
	
	private String createExceutionPackageId () {
		final String execPckgId = dateUtils.getCurrentDateWithTimeStamp();
		logger.info("execution package id has been created # {} ", execPckgId);
		return execPckgId;
	}
	
}
