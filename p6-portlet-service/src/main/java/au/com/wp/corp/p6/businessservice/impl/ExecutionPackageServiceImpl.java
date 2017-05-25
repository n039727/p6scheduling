/**
 * 
 */
package au.com.wp.corp.p6.businessservice.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.IExecutionPackageService;
import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.utils.DateUtils;
import au.com.wp.corp.p6.utils.P6Constant;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;

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
	P6SchedulingBusinessService p6SchedulingService;
	
	@Autowired
	P6WSClient p6wsClient;
	
	private List<ExecutionPackageDTO> executionPackageDTOFoP6List;
	private List<String> workOrdersForExcnPkgDelP6 = new ArrayList<String>();
	@Override
	public List<ExecutionPackageDTO> getExecutionPackageDTDOFoP6() {
		return executionPackageDTOFoP6List;
	}
	@Override
	public void setExecutionPackageDTDOFoP6(List<ExecutionPackageDTO> executionPackageDTDOFoP6) {
		this.executionPackageDTOFoP6List = executionPackageDTDOFoP6;
	}
	@Override
	public List<String> getWorkOrdersForExcnPkgDelP6() {
		return workOrdersForExcnPkgDelP6;
	}
	@Override
	public void setWorkOrdersForExcnPkgDelP6(List<String> workOrdersForExcnPkgDelP6) {
		this.workOrdersForExcnPkgDelP6 = workOrdersForExcnPkgDelP6;
	}
	
	
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
		final StringBuilder pkgSchedulerCmt = new StringBuilder();
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
						ExecutionPackage oldExecutionPackage = task.getExecutionPackage();
						oldExecutionPackage.getTasks().remove(task);
						executionPackages.add(oldExecutionPackage);
					}
					if(null != task.getCmts()){
						pkgSchedulerCmt.append(task.getCmts()+ " ");
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
					if(null != task.getCmts()){
						pkgSchedulerCmt.append(task.getCmts()+ " ");
					}
					tasks.add(task);
				}
			}
			executionPackage.setTasks(tasks);
			execPackgDTO.setCrewNames(crewNames.toString());
			execPackgDTO.setExecSchdlrCmt(pkgSchedulerCmt.toString());
			executionPackage.setScheduledStartDate(dateUtils.toDateFromDD_MM_YYYY(scheduledStartDate));
		}

		executionPackage.setCrtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setCrtdUsr(userName);
		executionPackage.setLstUpdtdTs(new Timestamp(System.currentTimeMillis()));
		executionPackage.setLstUpdtdUsr(userName);
		executionPackage.setExecSchdlrCmt(pkgSchedulerCmt.toString());
		executionPackageDao.createOrUpdateExecPackage(executionPackage);
		executionPackageDao.createOrUpdateTasks(executionPackage.getTasks());
		updateOldExecutionPackages(executionPackages);
		logger.info("execution package has been created with execution package id # {} ",
				execPackgDTO.getExctnPckgName());
		List<ExecutionPackageDTO> execPkgdtos = new ArrayList<ExecutionPackageDTO>();
		execPkgdtos.add(execPackgDTO);
		executionPackageDTOFoP6List = execPkgdtos;
		updateP6ForExecutionPackage(execPkgdtos);
		return execPackgDTO;

	}
	
	@Async
	private void updateP6ForExecutionPackage(List<ExecutionPackageDTO> execPkgdtos) {
		logger.debug("Starting to execution package update with execPkgdtos "
				+ execPkgdtos);
		List<ExecutionPackageCreateRequest> request = new ArrayList<>();
		if (execPkgdtos == null) {
			return;
		}
		for (ExecutionPackageDTO executionPackageDTOForP6 : execPkgdtos) {

			if (executionPackageDTOForP6.getWorkOrders() != null
					&& (!executionPackageDTOForP6.getWorkOrders().isEmpty())) {
			}

			List<WorkOrder> workOrders = executionPackageDTOForP6.getWorkOrders();
			if (workOrders != null) {
				for (WorkOrder workOrder : workOrders) {
					/*
					 * <v1:Text>18-05-2017_023711511</v1:Text> <!--Optional:-->
					 * <v1:UDFTypeDataType>Text</v1:UDFTypeDataType>
					 * <!--Optional:-->
					 * <v1:UDFTypeObjectId>5920</v1:UDFTypeObjectId>
					 * <!--Optional:-->
					 * <v1:UDFTypeSubjectArea>Activity</v1:UDFTypeSubjectArea>
					 * <!--Optional:--> <v1:UDFTypeTitle>Execution
					 * Grouping</v1:UDFTypeTitle>
					 */

					ExecutionPackageCreateRequest executionPackageCreateRequest = new ExecutionPackageCreateRequest();
					Integer foreignObjId = p6wsClient.getWorkOrderIdMap().get(workOrder.getWorkOrderId());
					executionPackageCreateRequest.setForeignObjectId(foreignObjId);
					executionPackageCreateRequest.setText(executionPackageDTOForP6.getExctnPckgName());
					executionPackageCreateRequest.setUdfTypeDataType(P6Constant.TEXT);
					executionPackageCreateRequest.setUdfTypeObjectId(5920);
					executionPackageCreateRequest.setUdfTypeSubjectArea(P6Constant.ACTIVITY);
					executionPackageCreateRequest.setUdfTypeTitle(P6Constant.EXECUTION_GROUPING);
					request.add(executionPackageCreateRequest);
				}
				ExecutionPackageDTO createdExecutionPackage = null;
				try {
					createdExecutionPackage = p6wsClient.createExecutionPackage(request);
					if (createdExecutionPackage != null) {
						logger.info("execution package created in P6 for {} with work orders {}",
								createdExecutionPackage.getExctnPckgName(), createdExecutionPackage.getWorkOrders());
						executionPackageDTOForP6.getWorkOrders().clear();
					}
				} catch (P6ServiceException e) {
					e.printStackTrace();
				}

			}

		}
		executionPackageDTOFoP6List = null;
		/*List<String>  workorderIds = getWorkOrdersForExcnPkgDelP6();
		if(workorderIds != null && workorderIds.size() >0){
			try {
				logger.debug("Calling to remove execution package for work orders {}",workorderIds);
				boolean isSuccess =p6wsClient.removeExecutionPackage(workorderIds);
				logger.debug("Removal suceeeded {}",isSuccess);
			} catch (P6ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			workorderIds.clear();
			workorderIds = null;
		}*/
	}
		
	private void updateOldExecutionPackages(Set<ExecutionPackage> executionPackages) throws P6BusinessException{
		if(null != executionPackages){
			logger.debug("Number of old Execution package>> {} ", executionPackages.size());
			String OldExePkgleadCrew = "";
			boolean crewMatches = Boolean.FALSE;
			for (ExecutionPackage executionPackage : executionPackages) {
				OldExePkgleadCrew = executionPackage.getLeadCrewId();
				Set<Task> tasks = executionPackage.getTasks();
				if(null == tasks || tasks.isEmpty()){
					//Delete the empty old execution package
					logger.debug("No tasks exists for the  Execution package>> {} ", executionPackage.getExctnPckgNam());
					executionPackageDao.deleteExecPackage(executionPackage);
				}
				else{
					//update the lead crew as null
					for(Task task : tasks){
						if(task.getCrewId().equals(OldExePkgleadCrew)){
							crewMatches = Boolean.TRUE;
							break;
						}
					}
					if(!crewMatches){
						executionPackage.setLeadCrewId(null);
					}
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
	@Transactional
	public List<WorkOrder> searchByExecutionPackage(WorkOrderSearchRequest input) throws P6BaseException {
		List<WorkOrder> listWOData = p6SchedulingService.retrieveWorkOrders(input);
		List<Task> tasksInDb = fetchListOfTasksForWorkOrders(listWOData);
		for (WorkOrder workOrder : listWOData) {
			if (workOrder.getWorkOrders() != null) {
				for (String workOrderId : workOrder.getWorkOrders()) {
					Optional<Task> task = findTask(tasksInDb, workOrderId);
					//Task dbTask = workOrderDAO.fetch(workOrderId); //2
					Task dbTask = task.isPresent() ? task.get() : new Task();
					logger.debug("Rerieved task in db for the the given workder in String array {}",workOrderId);
					if (dbTask.getExecutionPackage() != null) {
						logger.debug("Execution package obtained ===={}",dbTask.getExecutionPackage());
						String dbWOExecPkg = dbTask.getExecutionPackage().getExctnPckgNam();
						workOrder.setExctnPckgName(dbWOExecPkg);
						workOrder.setLeadCrew(dbTask.getExecutionPackage().getLeadCrewId());
					}
					workOrder.setScheduleDate(dateUtils.convertDateDDMMYYYY(workOrder.getScheduleDate()));
				}
			}
		}
		logger.debug("final grouped work orders size {}",listWOData.size());
		return listWOData;
	}
	private Optional<Task> findTask(final List<Task> list, final String woId) {
	    return list.stream()
	        .filter(p -> p.getTaskId().equals(woId)).findAny();
	}
	private List<Task> fetchListOfTasksForWorkOrders(List<WorkOrder> listWOData) throws P6BusinessException{
		long startTime = System.currentTimeMillis();
		List<String> worOrders = new ArrayList<String>();
		if (listWOData != null) {
			listWOData.forEach(workOrder->{
				worOrders.add(workOrder.getWorkOrderId());
				});
			
		}
		logger.debug("Total time taken to updateTasksInDB {}",System.currentTimeMillis() - startTime );
		return workOrderDao.fetchTasks(worOrders);
	}

}
