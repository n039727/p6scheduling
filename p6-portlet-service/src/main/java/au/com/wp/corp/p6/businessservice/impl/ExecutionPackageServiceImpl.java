/**
 * 
 */
package au.com.wp.corp.p6.businessservice.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
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
import au.com.wp.corp.p6.dto.UserTokenRequest;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BaseException;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.utils.DateUtils;
import au.com.wp.corp.p6.utils.P6Constant;
import au.com.wp.corp.p6.utils.WorkOrderComparator;
import au.com.wp.corp.p6.utils.WorkOrderComparatorOnActioned;
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

	private P6SchedulingBusinessService p6SchedulingService;

	@Autowired
	P6WSClient p6wsClient;

	@Autowired
	UserTokenRequest userTokenRequest;


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
	public ExecutionPackageDTO createOrUpdateExecutionPackage(ExecutionPackageDTO execPackgDTO)
			throws P6BusinessException {
		String userName = "Test User";
		if(userTokenRequest != null && userTokenRequest.getUserPrincipal() != null){
			userName = userTokenRequest.getUserPrincipal();
		}
		logger.info("calling create or update execution package with # {}, and user name# {}", execPackgDTO, userName );
		execPackgDTO.setExctnPckgName(createExceutionPackageId());
		ExecutionPackage executionPackage = new ExecutionPackage();
		executionPackage.setExctnPckgNam(execPackgDTO.getExctnPckgName());
		executionPackage.setLeadCrewId(execPackgDTO.getLeadCrew());
		final List<WorkOrder> workOrders = execPackgDTO.getWorkOrders();
		final StringBuilder crewNames = new StringBuilder();
		final StringBuilder pkgSchedulerCmt = new StringBuilder();
		Set<ExecutionPackage> executionPackages = new HashSet<>();
		Set<String> oldPkgName = new HashSet<>();
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

						if(oldPkgName.add(task.getExecutionPackage().getExctnPckgNam())){
							pkgSchedulerCmt.append(task.getExecutionPackage().getExecSchdlrCmt()+ " ");
						}
						ExecutionPackage oldExecutionPackage = task.getExecutionPackage();
						oldExecutionPackage.getTasks().remove(task);
						executionPackages.add(oldExecutionPackage);
					}
					else {
						if (null != task.getCmts()){
							pkgSchedulerCmt.append(task.getCmts()+ " ");
						}
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
		return execPackgDTO;

	}

	@Override
	@Async
	public void updateP6ForExecutionPackage() throws P6BusinessException{
		logger.debug("Starting to execution package update with execPkgdtos "
				+ executionPackageDTOFoP6List);
		List<ExecutionPackageCreateRequest> request = new ArrayList<>();
		if (executionPackageDTOFoP6List == null) {
			return;
		}
		for (ExecutionPackageDTO executionPackageDTOForP6 : executionPackageDTOFoP6List) {

			List<WorkOrder> workOrders = executionPackageDTOForP6.getWorkOrders();
			if (workOrders != null) {
				for (WorkOrder workOrder : workOrders) {
					
					ExecutionPackageCreateRequest executionPackageCreateRequest = new ExecutionPackageCreateRequest();
					Integer foreignObjId = p6wsClient.getWorkOrderIdMap().get(workOrder.getWorkOrderId());
					executionPackageCreateRequest.setForeignObjectId(foreignObjId);
					executionPackageCreateRequest.setText(executionPackageDTOForP6.getExctnPckgName());
					executionPackageCreateRequest.setUdfTypeDataType(P6Constant.TEXT);
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
					parseException(e);
				}

			}

		}
		executionPackageDTOFoP6List.clear();
		executionPackageDTOFoP6List = null;
	
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
		
		final String execPckgId = dateUtils.getCurrentDateWithTimeStampNoSeparator();
		logger.info("execution package id has been created # {} ", execPckgId);
		return execPckgId;
	}
	@Override
	@Transactional
	public List<WorkOrder> searchByExecutionPackage(WorkOrderSearchRequest input) throws P6BaseException {
		List<WorkOrder> listWOData = p6SchedulingService.retrieveWorkOrdersForExecutionPackage(input);
		List<WorkOrder> listWODataWithEp = new ArrayList<WorkOrder>();
		List<WorkOrder> listWODataWithOutEp = new ArrayList<WorkOrder>();
		List<Task> tasksInDb = fetchListOfTasksForWorkOrders(listWOData);
		for (WorkOrder workOrder : listWOData) {
			if (workOrder.getWorkOrders() != null) {
				for (String workOrderId : workOrder.getWorkOrders()) {
					Optional<Task> task = findTask(tasksInDb, workOrderId);
					Task dbTask = task.isPresent() ? task.get() : new Task();
					logger.debug("Rerieved task in db for the the given workder in String array {}",workOrderId);
					if (!StringUtils.isEmpty(dbTask.getCrewId())) {					
						workOrder.getCrewAssigned().add(dbTask.getCrewId());
					}
					workOrder.setScheduleDate(dateUtils.convertDateDDMMYYYY(workOrder.getScheduleDate(),"/"));
					if (dbTask.getExecutionPackage() != null) {
						logger.debug("Execution package obtained ===={}",dbTask.getExecutionPackage());
						String dbWOExecPkg = dbTask.getExecutionPackage().getExctnPckgNam();
						workOrder.setExctnPckgName(dbWOExecPkg);
						workOrder.setLeadCrew(dbTask.getExecutionPackage().getLeadCrewId());
						listWODataWithEp.add(workOrder);
					}else{
						listWODataWithOutEp.add(workOrder);
					}
				}
			}
		}
		List<WorkOrder> workorders = new ArrayList<> (listWODataWithOutEp);
		Collections.sort(workorders, new WorkOrderComparatorOnActioned());
		workorders.addAll(listWODataWithEp);
		Collections.sort(workorders,new WorkOrderComparator());
		logger.debug("final grouped work orders size {}",workorders.size());
		return workorders;
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
	@Override
	public void setP6BusinessService(P6SchedulingBusinessServiceImpl p6SchedulingBusinessServiceImpl) {
		this.p6SchedulingService = p6SchedulingBusinessServiceImpl;
		
	}

}
