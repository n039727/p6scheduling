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
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.mock.CreateP6MockData;
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
	WorkOrderDAO workOrderDao;

	@Autowired
	DateUtils dateUtils;
	@Autowired
	CreateP6MockData mockData;
	/*
	 * (non-Javadoc)
	 * 
	 * @see au.com.wp.corp.p6.businessservice.IExecutionPackageService#
	 * createOrUpdateExecutionPackage(au.com.wp.corp.p6.model.ExecutionPackage)
	 */
	@Transactional
	@Override
	public ExecutionPackageDTO createOrUpdateExecutionPackage(ExecutionPackageDTO execPackgDTO, String userName)
			throws P6BusinessException {
		logger.info("calling create or update execution package with # {}, and user name# {}", execPackgDTO, userName);
		execPackgDTO.setExctnPckgName(createExceutionPackageId());
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setExctnPckgNam(execPackgDTO.getExctnPckgName());
		executionPackage.setLeadCrewId(execPackgDTO.getLeadCrew());
		final List<WorkOrder> workOrders = execPackgDTO.getWorkOrders();
		final StringBuilder crewNames = new StringBuilder();
		Set<ExecutionPackage> executionPackages = new HashSet<>();
		if (workOrders != null && !workOrders.isEmpty()) {
			logger.debug("work orders size {}", workOrders.size());
			Set<Task> tasks = new HashSet<>();
			String scheduledStartDate = "";
			for (WorkOrder workOrder : workOrders) {
				logger.debug("For each workorder {} corresponding Task is fecthed", workOrder.getWorkOrderId());
				if (null != crewNames.toString() && !crewNames.toString().contains(workOrder.getCrewNames())){
					crewNames.append(crewNames.length() > 0 ? "," : "");
					crewNames.append(workOrder.getCrewNames());
				}
				logger.debug("crew names added --- {} ", crewNames.toString());
				Task task = workOrderDao.fetch(workOrder.getWorkOrderId());
				scheduledStartDate = workOrder.getScheduleDate();
				if (task != null) {
					logger.debug("Task {} is fecthed", task.getTaskId());
					if(null != task.getExecutionPackage()){
						logger.debug("Old Execution fatched {} for the  task {}", task.getExecutionPackage().getExctnPckgNam(), task.getTaskId());
						executionPackages.add(task.getExecutionPackage());
					}
					task.setExecutionPackage(executionPackage);
					task.setLstUpdtdUsr(userName);
					task.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
					tasks.add(task);
				} else {
					task = new Task();
					task.setCrewId(workOrder.getCrewNames());
					task.setTaskId(workOrder.getWorkOrderId());
					task.setSchdDt(dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate()));
					task.setExecutionPackage(executionPackage);
					task.setCrtdUsr(userName);
					task.setCrtdTs(new Timestamp(System.currentTimeMillis()));
					task.setLstUpdtdUsr(userName);
					task.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
					tasks.add(task);
				}
			}
			executionPackage.setTasks(tasks);
			execPackgDTO.setCrewNames(crewNames.toString());
			executionPackage.setScheduledStartDate(dateUtils.toDateFromDD_MM_YYYY(scheduledStartDate));
		}

		executionPackage.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setCrtdUsr(userName);
		executionPackage.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setLstUpdtdUsr(userName);
		executionPackageDao.createOrUpdateExecPackage(executionPackage);
		executionPackageDao.createOrUpdateTasks(executionPackage.getTasks());
		updateOldExecutionPackages(executionPackages);
		logger.info("execution package has been created with execution package id # {} ",
				execPackgDTO.getExctnPckgName());
		return execPackgDTO;

	}
	
	private void updateOldExecutionPackages(Set<ExecutionPackage> executionPackages) throws P6BusinessException{
		if(null != executionPackages){
			logger.debug("Number of old Execution package>> {} ", executionPackages.size());
			for (ExecutionPackage executionPackage : executionPackages) {
				Set<Task> tasks = executionPackage.getTasks();
				if(null == tasks || tasks.isEmpty()){
					//Delete the empty old execution package
					logger.debug("No tasks exists for the  Execution package>> {} ", executionPackage.getExctnPckgNam());
					executionPackageDao.deleteExecPackage(executionPackage);
				}
				else{
					//update the action as N for the old execution package
					logger.debug("Updating the Actioned field as N for the  Execution package>> {} ", executionPackage.getExctnPckgNam());
					executionPackage.setActioned("N");
					executionPackageDao.createOrUpdateExecPackage(executionPackage);
				}
				
			}
		}
	}

	private String createExceutionPackageId() {
		final String execPckgId = dateUtils.getCurrentDateWithTimeStamp();
		logger.info("execution package id has been created # {} ", execPckgId);
		return execPckgId;
	}
	@Override
	public List<WorkOrder> searchByExecutionPackage(WorkOrderSearchRequest input) throws P6DataAccessException {
		List<WorkOrder> mockWOData = mockData.search(input);
		for (WorkOrder workOrder : mockWOData) {
			if (workOrder.getWorkOrders() != null) {
				for (String workOrderId : workOrder.getWorkOrders()) {
					Task dbTask = workOrderDao.fetch(workOrderId);
					dbTask = dbTask == null ? new Task() : dbTask;
					logger.debug("Rerieved task in db for the the given workder in String array {}",workOrderId);
					if (dbTask.getExecutionPackage() != null) {
						logger.debug("Execution package obtained ===={}",dbTask.getExecutionPackage());
						String dbWOExecPkg = dbTask.getExecutionPackage().getExctnPckgNam();
						workOrder.setExctnPckgName(dbWOExecPkg);
					}
					
				}
			}
		}
		logger.debug("final grouped work orders size {}",mockWOData.size());
		return mockWOData;
	}
}
