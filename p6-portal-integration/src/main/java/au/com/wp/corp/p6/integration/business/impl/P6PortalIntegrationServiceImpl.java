/**
 * 
 */
package au.com.wp.corp.p6.integration.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.integration.business.P6PortalIntegrationService;
import au.com.wp.corp.p6.integration.dao.P6PortalDAO;
import au.com.wp.corp.p6.integration.dto.ExecutionPackageCreateRequest;
import au.com.wp.corp.p6.integration.dto.WorkOrder;
import au.com.wp.corp.p6.integration.exception.P6BaseException;
import au.com.wp.corp.p6.integration.exception.P6BusinessException;
import au.com.wp.corp.p6.integration.exception.P6DataAccessException;
import au.com.wp.corp.p6.integration.exception.P6IntegrationExceptionHandler;
import au.com.wp.corp.p6.integration.exception.P6ServiceException;
import au.com.wp.corp.p6.integration.util.CacheManager;
import au.com.wp.corp.p6.integration.util.DateUtil;
import au.com.wp.corp.p6.integration.util.P6Constant;
import au.com.wp.corp.p6.integration.util.ProcessStatus;
import au.com.wp.corp.p6.integration.util.ReadWriteProcessStatus;
import au.com.wp.corp.p6.integration.wsclient.cleint.P6WSClient;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;

/**
 * @author N039126
 *
 */
@Service
public class P6PortalIntegrationServiceImpl implements P6PortalIntegrationService {
	private static final Logger logger = LoggerFactory.getLogger(P6PortalIntegrationServiceImpl.class);

	public static final String POLING_TIME_TO_CHECK_READ_STATUS_INMILI = "POLING_TIME_TO_CHECK_READ_STATUS_INMILI";

	public static final String INTEGRATION_RUN_STARTEGY = "INTEGRATION_RUN_STARTEGY";

	public static final String USER_STATUS_AL = "AL";

	public static final String TASK_STATUS_COMPLETED = "Completed";

	@Autowired
	P6WSClient p6WSClient;

	@Autowired
	P6PortalDAO p6PortalDAO;

	@Autowired
	DateUtil dateUtil;

	@Autowired
	P6IntegrationExceptionHandler exceptionHandler;

	private List<WorkOrder> listWOData = new ArrayList<WorkOrder>();
	private List<String> crewListAll = new ArrayList();
	private Map<String, List<String>> depotCrewMap = new HashMap();



	@Override
	public void clearApplicationMemory() {
		logger.debug("Clearing cache memory........");
		CacheManager.getProjectWorkgroupListMap().clear();
		CacheManager.getWsHeaders().clear();
		CacheManager.getProjectsMap().clear();
		CacheManager.getDeletetedexecpkaglist().clear();
		CacheManager.getTasksforupdate().clear();
		CacheManager.getTasksforremove().clear();
		CacheManager.getExecpkglistforupdate().clear();
		CacheManager.getExecutionpackagenameforupdate().clear();
		listWOData = null;
	}



	@Override
	@Transactional
	public boolean startPortalToP6Integration()
			throws P6BusinessException {
		boolean status = Boolean.FALSE;
		try {
			if(crewListAll.isEmpty()||depotCrewMap.isEmpty()){
				depotCrewMap = p6PortalDAO.fetchAllResourceDetail();
				crewListAll = depotCrewMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
			}

			Set<Task> tasksForRemove = CacheManager.getTasksforremove();
			List<Task> tasksInDb = getAlltasksInPortal();
			listWOData = retrieveWorkOrdersFromP6(tasksInDb);
			for(Task task: tasksInDb){
				Optional<WorkOrder> workOrder = findWOByTaskId(listWOData, task.getTaskId());
				if(!workOrder.isPresent()){
					if(task.getTodoAssignments() == null || task.getTodoAssignments().isEmpty()){
						tasksForRemove.add(task);
					}
				}
			}
			for(WorkOrder workOrder : listWOData) {
				if (workOrder.getWorkOrders() != null) {
					String[] workOrders = workOrder.getWorkOrders()
							.toArray(new String[workOrder.getWorkOrders().size()]);
					for (String workOrderId : workOrders) {
						Optional<Task> task = findTaskByWoId(tasksInDb, workOrderId);
						Task dbTask = task.isPresent() ? task.get() : new Task();
						ExecutionPackage executionPackage = dbTask.getExecutionPackage();
						WorkOrder workOrderNew = prepareWorkOrder(workOrder, dbTask);
						//TODO:Async call 
						status = Boolean.TRUE;
					}
				}
			}
			updateTasksAndExecutionPackageInP6AndDB();
		} catch (P6BusinessException e) {
			status = Boolean.FALSE;
			logger.debug("error- ", e);
			exceptionHandler.handleException(e);
			throw e;
		} finally {
			p6WSClient.logoutFromP6();
			clearApplicationMemory();
		}
		return status;
	}


	private boolean isWOInboxed(String crewAssignedForWorkOrder){
		boolean result = true;
		if (!StringUtils.isEmpty(crewAssignedForWorkOrder)) {
			String[] crewListFromWorkOrder = StringUtils.split(crewAssignedForWorkOrder, ",");
			for(String crew : crewListFromWorkOrder){
				if(crewListAll.contains(crew)){
					result = false;
				}else{
					result = true;
				}
			}
		}
		return result;
	}
	/**
	 * Method to sync work orders between Portal DB and P6.
	 * 
	 * @param workOrder
	 * @param dbTask
	 * @param tasksForUpdate
	 * @param deletetedExecPkagList
	 * @param execPkgListForUpdate
	 * @return
	 * @throws P6DataAccessException
	 */
	private WorkOrder prepareWorkOrder(WorkOrder workOrder, Task dbTask) throws P6BusinessException {
		logger.debug("work order id in workorder returned from P6 =={}", workOrder.getWorkOrderId());
		Date scheduledDateForWorkOrder = dateUtil.convertStringToDate(workOrder.getScheduleDate());
		Date scheduledDateInTask = dbTask.getSchdDt();
		String crewAssignedForWorkOrder = workOrder.getCrewNames() == null ? "" : workOrder.getCrewNames();
		logger.debug("crewAssignedForWorkOrder in workorder returned from P6 =={}", crewAssignedForWorkOrder);
		String crewAssignedForTask = dbTask.getCrewId() == null ? "" : dbTask.getCrewId();
		logger.debug("crewAssignedForTask in workorder returned from Portal db =={}", crewAssignedForTask);
		String leadCrewWorkOrder = workOrder.getLeadCrew() == null ? "" : workOrder.getLeadCrew();
		WorkOrder workOrderNew = new WorkOrder();
		workOrderNew.setWorkOrders(workOrder.getWorkOrders());
		logger.debug("work orders in workorder returned from P6 =={}", workOrder.getWorkOrders());
		workOrderNew.setCrewNames(crewAssignedForWorkOrder);
		if (!StringUtils.isEmpty(crewAssignedForWorkOrder)) {
			workOrderNew.getCrewAssigned().add(crewAssignedForWorkOrder);
		}
		workOrderNew.setScheduleDate(workOrder.getScheduleDate());

		//	dbTask.setCrtdUsr(userTokenRequest.getUserPrincipal());
		//dbTask.setLstUpdtdUsr(userTokenRequest.getUserPrincipal());
		Set<Task> tasksForUpdate = CacheManager.getTasksforupdate();
		Set<ExecutionPackage> executionPackageUpdateList = CacheManager.getExecpkglistforupdate();
		Set<WorkOrder> executionPackageNameForUpdate = CacheManager.getExecutionpackagenameforupdate();

		ExecutionPackage executionPackage = dbTask.getExecutionPackage();
		String executionPackageWo = workOrder.getExctnPckgName();
		if(executionPackage != null && (!executionPackage.getExctnPckgNam().equals(executionPackageWo))){
			workOrder.setExctnPckgName(executionPackage.getExctnPckgNam());
			executionPackageNameForUpdate.add(workOrder);
		}
		if(isWOInboxed(crewAssignedForWorkOrder)){
			if (executionPackage != null) {
				removOrRetainExecutionPackageFromTask(dbTask, workOrder);
			}
		}else if (scheduledDateForWorkOrder != null && scheduledDateInTask != null) {
			logger.debug("in prepare workorder scheduledDateForWorkOrder {}", scheduledDateForWorkOrder);
			logger.debug("in prepare workorder scheduledDateInTask {}", scheduledDateInTask);
			if (scheduledDateForWorkOrder.compareTo(scheduledDateInTask) != 0) {
				logger.debug("in prepare workorder scheduledDateInTask is different so removing exec pkg",
						scheduledDateInTask);
				dbTask.setSchdDt(scheduledDateForWorkOrder);
				// if date is changed then execution package is null
				if (executionPackage != null) {
					removOrRetainExecutionPackageFromTask(dbTask, workOrder);
				}
			}
		}
		// to update remaining fields from p6 if exec pkg is not null
		if (executionPackage != null) {
			workOrderNew.setLeadCrew(executionPackage.getLeadCrewId());
			workOrderNew.setExctnPckgName(executionPackage.getExctnPckgNam());
			workOrderNew.setActioned(executionPackage.getActioned());
		} else {
			workOrderNew.setLeadCrew(leadCrewWorkOrder);
			workOrderNew.setExctnPckgName("");
			workOrderNew.setActioned(dbTask.getActioned());
		}
		if (!crewAssignedForWorkOrder.equalsIgnoreCase(crewAssignedForTask)) {
			logger.debug("prepare work order crew id updating in  task {} updating for this work order ==={}",
					crewAssignedForTask, crewAssignedForWorkOrder);
			dbTask.setCrewId(crewAssignedForWorkOrder);
			if (executionPackage == null) {
				dbTask.setLeadCrewId(leadCrewWorkOrder);
			} else {
				updateExecutionPackageLeadCrew(executionPackage, dbTask, true);
				executionPackageUpdateList.add(executionPackage);
			}

			if(!StringUtils.isEmpty(dbTask.getTaskId())){
				tasksForUpdate.add(dbTask);
			}
		}


		return workOrderNew;

	}
	/**
	 * determind if the task from its execution package should be Removed/dis associated.
	 * also Removes/dis associates  the task from its execution package.
	 * @param task, workOrder
	 */
	private void removOrRetainExecutionPackageFromTask(Task task, WorkOrder workOrder) {
		/********* Using Cachemanager for temp data store ************/
		Set<Task> tasksForUpdate = CacheManager.getTasksforupdate();
		Set<String> deletetedExecPkagList = CacheManager.getDeletetedexecpkaglist();
		Set<ExecutionPackage> execPkgListForUpdate = CacheManager.getExecpkglistforupdate();
		Set<WorkOrder> executionPackageNameForUpdate = CacheManager.getExecutionpackagenameforupdate();
		/********* Using Cachemanager for temp data store ************/
		ExecutionPackage executionpackage = task.getExecutionPackage();
		/*
		 * Before removing check of other tasks on the execution package if for
		 * all other tasks the dates are same then don't remove the package(TBD)
		 * Remove execution package if work order in P6 moved to scheduling inbox
		 */
		if (isWOInboxed(workOrder.getCrewNames() == null ? "" : workOrder.getCrewNames())||!isExecutionPackageTobeRetained(task)) {
			executionpackage.removeTask(task);
			updateExecutionPackageLeadCrew(executionpackage, task, false);
			// update for scheduled date or any change in task
			if (!StringUtils.isEmpty(task.getTaskId())) {
				tasksForUpdate.add(task);
			}
			// executionpackage.setCrtdUsr(userTokenRequest.getUserPrincipal());
			// executionpackage.setLstUpdtdUsr(userTokenRequest.getUserPrincipal());
			execPkgListForUpdate.add(executionpackage);
			deletetedExecPkagList.add(task.getTaskId());
			executionPackageNameForUpdate.remove(workOrder);
		}else{
			tasksForUpdate.add(task);
		}

	}

	/**
	 * Update execution package lead crew and actioned field as blank.
	 * @param executionpackage
	 * @param task
	 * @param updateAble
	 */
	private void updateExecutionPackageLeadCrew(ExecutionPackage executionpackage, Task task, boolean updateAble) {
		Set<Task> tasksInExecutionPkg = executionpackage.getTasks();
		StringBuilder crewPresent = new StringBuilder();
		tasksInExecutionPkg.forEach(taskInPkg -> {
			if (!updateAble) {
				if (!taskInPkg.getTaskId().equals(task.getTaskId())) {
					logger.debug("in removExecutionPackageFromTask  creew id from task ()", taskInPkg.getCrewId());
					crewPresent.append(taskInPkg.getCrewId());
				}
			} else {
				logger.debug("in removExecutionPackageFromTask  creew id from task ()", taskInPkg.getCrewId());
				crewPresent.append(taskInPkg.getCrewId());
			}
		});
		String executionPkgLeadCrew = executionpackage.getLeadCrewId() == null ? "" : executionpackage.getLeadCrewId();
		logger.debug("in removExecutionPackageFromTask creew id from exec pakage ()", executionPkgLeadCrew);
		/* turn blank the lead crew if matching remove the task */
		if ((!"".equals(executionPkgLeadCrew)) && (!StringUtils.contains(crewPresent, executionPkgLeadCrew))) {
			logger.debug("in prepare workorder removed lead creew id from exec pakage ()",
					executionpackage.getLeadCrewId());
			executionpackage.setLeadCrewId("");
			executionpackage.setActioned("N");
		}

	}
	/**
	 * Checks for execution package retaintion logic.
	 * 
	 * @param task
	 * @return
	 */
	private boolean isExecutionPackageTobeRetained(Task task) {
		boolean retValue = false;
		if ((!CollectionUtils.isEmpty(listWOData))) {
			ExecutionPackage executionPackage = task.getExecutionPackage();
			if (executionPackage != null) {
				Set<Task> associatedTasks = executionPackage.getTasks();
				// find all the the work orders in the search results
				Set<String> taskIdsInexecPkg = associatedTasks.stream().map(Task::getTaskId)
						.collect(Collectors.toSet());
				/*
				 * collect those 
				 * work orders which are having the 
				 * same ids as of the 
				 * tasks within the	execution package 
				 */
				Set<WorkOrder> acceptableWorkOrders = listWOData.stream()
						.filter(id -> taskIdsInexecPkg.contains(id.getWorkOrderId()))
						.collect(Collectors.toSet());
				/*
				 * if both the sets are having same size 
				 * i.e returned as part of the same date 
				 * as in the search result.
				 */
				if (acceptableWorkOrders.size() > 0 && (acceptableWorkOrders.size() == taskIdsInexecPkg.size())) {
					for (WorkOrder workOrder : acceptableWorkOrders) {
						String packageName = workOrder.getExctnPckgName();	
						/*
						 * Check if all the work orders belong to 
						 * the same execution package a of the corresponding task 
						 * to be deleted.
						 */
						if(!packageName.equalsIgnoreCase(executionPackage.getExctnPckgNam())){
							retValue = false;
							break;
						}
					}

				}else{
					retValue = true;
				}				
			}
		}
		return retValue;
	}
	private List<WorkOrder> retrieveWorkOrdersFromP6(List<Task> taskList) throws P6BusinessException {
		long starttime = System.currentTimeMillis();
		List<String> taskIds = taskList.stream()
				.map(task -> task.getTaskId())
				.collect(Collectors.toList());
		List<WorkOrder> workOrders = null;
		try {
			workOrders = p6WSClient.readActivities(taskIds);
		} catch (P6ServiceException e) {
			logger.error("An error occurs while reading P6 activity : ", e);
			parseException(e);
		}
		logger.info("Time taken to invoke P6 and return workorders =="+ (System.currentTimeMillis() -starttime));
		return workOrders;
	}



	private List<Task> getAlltasksInPortal() {
		List<Task> taskList = null;
		try {
			taskList =  p6PortalDAO.getALlTasks();
		} catch (P6BaseException e) {
			logger.error("An error occurs during batch processing - ",e);
			logger.error("error occurs during batch processing - error message# - {}",e.getMessage());
		}
		return taskList;
	}
	private Optional<Task> findTaskByWoId(final List<Task> list, final String woId) {
		return list.stream().filter(p -> p.getTaskId().equals(woId)).findAny();
	}

	private Optional<WorkOrder> findWOByTaskId(final List<WorkOrder> list, final String taskId) {
		return list.stream().filter(p -> p.getWorkOrderId().equals(taskId)).findAny();
	}

	/**
	 * Updates Task and ExecutionPackage in POrtal DB and P6.
	 * @throws P6BusinessException
	 */
	private void updateTasksAndExecutionPackageInP6AndDB() throws P6BusinessException {
		long startTime = System.currentTimeMillis();
		Set<Task> tasksForUpdate = CacheManager.getTasksforupdate();
		Set<Task> tasksForRemove = CacheManager.getTasksforremove();
		Set<ExecutionPackage> execPkgList = CacheManager.getExecpkglistforupdate();
		Set<WorkOrder> updateExecPkgNameList = CacheManager.getExecutionpackagenameforupdate();
		try {
			if (!CollectionUtils.isEmpty(tasksForUpdate)) {
				logger.debug("updateTasksInDB called with tasks # {}", tasksForUpdate.size());// can
				// be
				// considerd
				// for
				// add
				// batch
				for (Iterator<Task> iterator = tasksForUpdate.iterator(); iterator.hasNext();) {
					Task task = (Task) iterator.next();
					logger.debug(
							"Task with task id {} being updated for execution package of work order sync in portlet db",
							task.getTaskId());
					p6PortalDAO.saveTask(task);
				}
				for (Iterator<Task> iterator = tasksForRemove.iterator(); iterator.hasNext();) {
					Task task = (Task) iterator.next();
					logger.debug(
							"Task with task id {} being deleted from portal DB of work order sync in portlet db",
							task.getTaskId());
					p6PortalDAO.removeTask(task);
				}
			}
		} catch (P6DataAccessException e) {
			logger.error("Entire Exception : ", e);
			logger.error("Error occurred in DB persist {}", e.getCause().getMessage());
		}
		try {
			if (!CollectionUtils.isEmpty(execPkgList)) {
				for (Iterator<ExecutionPackage> iterator = execPkgList.iterator(); iterator.hasNext();) {// can
					// be
					// considerd
					// for
					// add
					// batch
					ExecutionPackage exectionPkg = (ExecutionPackage) iterator.next();
					logger.debug("Execution Package {} being updated in portlet db", exectionPkg.getExctnPckgNam());
					p6PortalDAO.createOrUpdateExecPackage(exectionPkg);
				}
			}
		} catch (P6DataAccessException e) {
			logger.error("Entire Exception : ", e);
			logger.error("Error occurred in DB persist {}", e.getCause().getMessage());
		}
		logger.debug("Total time taken to updateTasksInDB {}", System.currentTimeMillis() - startTime);
		removeExecutionPackageinP6();
		updateExecutionPackage(updateExecPkgNameList);
	}

	/**
	 * Method to remove execution package in P6.
	 * @throws P6BusinessException
	 */
	private void removeExecutionPackageinP6() throws P6BusinessException {
		long startTime = System.currentTimeMillis();
		Set<String> workOrderIds = CacheManager.getDeletetedexecpkaglist();

		if (!CollectionUtils.isEmpty(workOrderIds)) {
			try {
				logger.debug("removeExecutionPackageinP6 called with tasks # {}", workOrderIds.size());
				logger.debug("Calling to remove execution package for work orders {}", workOrderIds);
				Map<String, Integer> workOrderIdMap = p6WSClient.getWorkOrderIdMap();
				List<Integer> listOfObjectId = new ArrayList<Integer>();
				workOrderIds.forEach(workOrderId -> {
					Integer objectId = workOrderIdMap.get(workOrderId);
					if ((!workOrderIdMap.containsKey(workOrderId)) || objectId == null) {
						logger.info("input id # {} ", workOrderId);
						workOrderIdMap.put(workOrderId, 0);
					}
					listOfObjectId.add(workOrderIdMap.get(workOrderId));
				});
				boolean isSuccess = p6WSClient.removeExecutionPackage(listOfObjectId,true);
				logger.debug("Removal suceeeded {}", isSuccess);
			} catch (P6ServiceException e) {
				logger.error("An error occurs while removing data from P6 activity : ", e);
				CacheManager.getSystemReadWriteStatusMap().put(ProcessStatus.P6_ACTIVITY_READ_STATUS, ReadWriteProcessStatus.FAILED);
				parseException(e);
			}

		}
		logger.debug("Total time taken to removeExecutionPackageinP6 {}", System.currentTimeMillis() - startTime);
	}
	/**
	 * Method to update execution package name in P6.
	 * @throws P6BusinessException
	 */
	private Boolean updateExecutionPackage(Set<WorkOrder> woList) throws P6BusinessException {
		List<ExecutionPackageCreateRequest> request = new ArrayList<>();
		for (WorkOrder workOrder : woList) {
			ExecutionPackageCreateRequest executionPackageCreateRequest = new ExecutionPackageCreateRequest();
			Integer foreignObjId = p6WSClient.getWorkOrderIdMap().get(workOrder.getWorkOrderId());
			logger.debug("foreignObjId {} returned for workOrderId {}",foreignObjId,workOrder.getWorkOrderId());
			executionPackageCreateRequest.setForeignObjectId(foreignObjId);
			executionPackageCreateRequest.setText(workOrder.getExctnPckgName());
			executionPackageCreateRequest.setUdfTypeDataType(P6Constant.TEXT);
			executionPackageCreateRequest.setUdfTypeSubjectArea(P6Constant.ACTIVITY);
			executionPackageCreateRequest.setUdfTypeTitle(P6Constant.EXECUTION_GROUPING);
			request.add(executionPackageCreateRequest);
		}
		Boolean createdExecutionPackage = false;
		try {
			createdExecutionPackage = p6WSClient.updateExecutionPackage(request);
		}catch (P6ServiceException e) {
			logger.error("An error occurs while updating data in P6 for executionPackage : ", e);
			parseException(e);
		}
		return createdExecutionPackage;
	}
}
