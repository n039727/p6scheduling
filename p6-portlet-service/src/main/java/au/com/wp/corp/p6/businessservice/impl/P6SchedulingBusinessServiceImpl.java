package au.com.wp.corp.p6.businessservice.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import au.com.wp.corp.p6.businessservice.IExecutionPackageService;
import au.com.wp.corp.p6.businessservice.P6SchedulingBusinessService;
import au.com.wp.corp.p6.dataservice.ExecutionPackageDao;
import au.com.wp.corp.p6.dataservice.ResourceDetailDAO;
import au.com.wp.corp.p6.dataservice.TodoDAO;
import au.com.wp.corp.p6.dataservice.WorkOrderDAO;
import au.com.wp.corp.p6.dto.ActivitySearchRequest;
import au.com.wp.corp.p6.dto.ExecutionPackageDTO;
import au.com.wp.corp.p6.dto.MetadataDTO;
import au.com.wp.corp.p6.dto.ResourceDTO;
import au.com.wp.corp.p6.dto.ToDoAssignment;
import au.com.wp.corp.p6.dto.ToDoItem;
import au.com.wp.corp.p6.dto.UserTokenRequest;
import au.com.wp.corp.p6.dto.ViewToDoStatus;
import au.com.wp.corp.p6.dto.WorkOrder;
import au.com.wp.corp.p6.dto.WorkOrderSearchRequest;
import au.com.wp.corp.p6.exception.P6BusinessException;
import au.com.wp.corp.p6.exception.P6DataAccessException;
import au.com.wp.corp.p6.exception.P6ServiceException;
import au.com.wp.corp.p6.model.ExecutionPackage;
import au.com.wp.corp.p6.model.Task;
import au.com.wp.corp.p6.model.TodoAssignment;
import au.com.wp.corp.p6.model.TodoTemplate;
import au.com.wp.corp.p6.utils.CacheManager;
import au.com.wp.corp.p6.utils.DateUtils;
import au.com.wp.corp.p6.utils.P6Constant;
import au.com.wp.corp.p6.utils.WorkOrderComparator;
import au.com.wp.corp.p6.utils.WorkOrderComparatorOnActioned;
import au.com.wp.corp.p6.wsclient.cleint.P6WSClient;

@Service
public class P6SchedulingBusinessServiceImpl implements P6SchedulingBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(P6SchedulingBusinessServiceImpl.class);

	@Autowired
	TodoDAO todoDAO;
	@Autowired
	WorkOrderDAO workOrderDAO;

	@Autowired
	DateUtils dateUtils;

	@Autowired
	private ExecutionPackageDao executionPackageDao;

	private static String ACTIONED_Y = "Y";
	private static String ACTIONED_N = "N";

	@Autowired
	UserTokenRequest userTokenRequest;

	@Autowired
	P6WSClient p6wsClient;

	@Autowired
	ResourceDetailDAO resourceDetailDAO;

	Map<String, List<String>> depotCrewMap = new HashMap<String, List<String>>();
	List<WorkOrder> listWOData = null;
	List<Task> tasksInDb = null;
	@Autowired
	private IExecutionPackageService executionPackageService;

	@PostConstruct
	public void init() {
		executionPackageService.setP6BusinessService(this);
	}
	@Override
	public List<WorkOrder> retrieveWorkOrders(WorkOrderSearchRequest input) throws P6BusinessException {
		logger.info("input date # {} ", input.getFromDate());
		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		List<String> crewListAll = new ArrayList<String>();
		crewListAll = depotCrewMap.values().stream().flatMap(List::stream).collect(Collectors.toList());

		searchRequest.setCrewList(crewListAll);
		searchRequest
		.setPlannedStartDate(input.getFromDate() != null ? dateUtils.convertDate(input.getFromDate()) : null);
		searchRequest.setPlannedEndDate(input.getToDate() != null ? dateUtils.convertDate(input.getToDate()) : null);
		List<WorkOrder> workOrders = null;
		try {
			workOrders = p6wsClient.searchWorkOrder(searchRequest);
			logger.info("list of work orders from P6# {}", workOrders);
		} catch (P6ServiceException e) {
			parseException(e);
		}
		return workOrders;

	}
	@Override
	public List<WorkOrder> retrieveWorkOrdersForExecutionPackage(WorkOrderSearchRequest input) throws P6BusinessException {
		logger.info("input date # {} ", input.getFromDate());
		ActivitySearchRequest searchRequest = new ActivitySearchRequest();
		// if crew list is null then append all crew in the criteria
		if(input.getCrewList() == null || input.getCrewList().size() == 0){
			List<String> crewListAll = new ArrayList<String>();
			crewListAll = depotCrewMap.values().stream().flatMap(List::stream)
					.collect(Collectors.toList());
			input.setCrewList(crewListAll);
		}
		//if depot is there select all crew for that depot
		if(input.getDepotList() != null && input.getDepotList().size() >0){
			List<String> crewListAll = new ArrayList<String>();
			input.getDepotList().forEach(depot ->{
				crewListAll.addAll(depotCrewMap.get(depot));
			});
			input.setCrewList(crewListAll);
		}
		searchRequest.setCrewList(input.getCrewList());
		searchRequest.setPlannedStartDate(input.getFromDate() != null ? dateUtils.convertDate(input.getFromDate()): null);
		searchRequest.setPlannedEndDate(input.getToDate() != null ? dateUtils.convertDate(input.getToDate()) : null);
		searchRequest.setWorkOrder(input.getWorkOrderId());
		searchRequest.setDepotList(input.getDepotList());
		List<WorkOrder> workOrdersEp = null;
		try {
			workOrdersEp = p6wsClient.searchWorkOrder(searchRequest);
			logger.info("list of work orders from P6# {}", workOrdersEp);
		} catch (P6ServiceException e) {
			parseException(e);
		}
		return workOrdersEp;

	}

	private List<WorkOrder> applyFilters(List<WorkOrder> listWOData, WorkOrderSearchRequest input) {
		List<WorkOrder> resultListWOData = new ArrayList<WorkOrder>();
		List<String> listOfCrew = null;
		if (input.getWorkOrderId() != null && !input.getWorkOrderId().equals("")) {
			Optional<WorkOrder> wo = findWOByTaskId(listWOData, input.getWorkOrderId());
			if (wo.isPresent()) {
				resultListWOData.add(wo.get());
			}
		} else if (!CollectionUtils.isEmpty(input.getCrewList())) {
			listOfCrew = input.getCrewList();
		} else if (!CollectionUtils.isEmpty(input.getDepotList()) && (CollectionUtils.isEmpty(input.getCrewList()))) {
			List<String> crewListAll = new ArrayList<String>();
			input.getDepotList().forEach(depot -> {
				crewListAll.addAll(depotCrewMap.get(depot));
			});
			listOfCrew = crewListAll;
		} else {
			return listWOData;
		}
		if (!CollectionUtils.isEmpty(listOfCrew)) {
			for (String crew : listOfCrew) {
				List<WorkOrder> woList = findWoByCrew(listWOData, crew);
				if (null != woList && woList.size() > 0) {
					resultListWOData.addAll(woList);
				}
			}
		}
		return resultListWOData;

	}

	@Override
	@Transactional
	public List<WorkOrder> search(WorkOrderSearchRequest input) throws P6BusinessException {
		long startTime = System.currentTimeMillis();
		logger.debug("User logged in as ======================================={}",
				userTokenRequest.getUserPrincipal());
		Map<String, WorkOrder> mapOfExecutionPkgWO = new HashMap<String, WorkOrder>();
		List<WorkOrder> ungroupedWorkorders = new ArrayList<WorkOrder>();
		try {
			listWOData = retrieveWorkOrders(input); // 1 clear and nullify after
			// remove
			tasksInDb = fetchListOfTasksBySearchedDate(input, listWOData); // clear
			// and
			// nullify
			// after
			// remove
			tasksInDb.forEach(task -> {
				Optional<WorkOrder> wo = findWOByTaskId(listWOData, task.getTaskId());
				if (!wo.isPresent() && null != task.getExecutionPackage()) {
					removExecutionPackageFromTask(task);
				}
			});
			listWOData = applyFilters(listWOData, input);
			for (WorkOrder workOrder : listWOData) {
				if (workOrder.getWorkOrders() != null) {
					String[] workOrders = workOrder.getWorkOrders()
							.toArray(new String[workOrder.getWorkOrders().size()]);
					for (String workOrderId : workOrders) {
						Optional<Task> task = findTaskByWoId(tasksInDb, workOrderId);
						Task dbTask = task.isPresent() ? task.get() : new Task();
						ExecutionPackage executionPackage = dbTask.getExecutionPackage();
						WorkOrder workOrderNew = prepareWorkOrder(workOrder, dbTask);
						if (dbTask.getExecutionPackage() != null) {
							String dbWOExecPkgName = executionPackage.getExctnPckgNam(); // store
							// in
							// a
							// different
							// variable
							Set<Task> taskForExePak = executionPackage.getTasks();
							if (mapOfExecutionPkgWO.containsKey(dbWOExecPkgName)) {
								workOrderNew.getCrewAssigned().add(workOrderNew.getCrewNames());
								taskForExePak.remove(dbTask);
								regroupEPForOtherWO(listWOData, mapOfExecutionPkgWO, taskForExePak, dbWOExecPkgName);
							} else {
								if (!StringUtils.isEmpty(dbTask.getCrewId())) {
									workOrderNew.getCrewAssigned().add(workOrderNew.getCrewNames());
								}
								mapOfExecutionPkgWO.put(dbWOExecPkgName, workOrderNew);
								taskForExePak.remove(dbTask);
								regroupEPForOtherWO(listWOData, mapOfExecutionPkgWO, taskForExePak, dbWOExecPkgName);
							}

						} else {
							// create separate work order list
							ungroupedWorkorders.add(workOrderNew);
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			parseException(e);
		} catch (Exception e) {
			parseException(e);
		}

		logger.debug("final grouped work orders size {}",mapOfExecutionPkgWO.values().size());
		logger.debug("final grouped work orders = {}",mapOfExecutionPkgWO.values());
		List<WorkOrder> workorders = new ArrayList<> (ungroupedWorkorders);
		Collections.sort(workorders, new WorkOrderComparatorOnActioned());
		workorders.addAll(mapOfExecutionPkgWO.values());
		Collections.sort(workorders, new WorkOrderComparator());
		logger.debug("Total time taken to execute and return search results == {}",
				System.currentTimeMillis() - startTime);
		return workorders;
	}

	private void regroupEPForOtherWO(List<WorkOrder> listOfWo, Map<String, WorkOrder> mapOfExecutionPkgWO,
			Set<Task> taskForExePak, String dbWOExecPkg) {
		WorkOrder workOrdersalreadyinGroup = mapOfExecutionPkgWO.get(dbWOExecPkg);
		if (taskForExePak.size() > 0) {
			for (Task tsk : taskForExePak) {
				Optional<WorkOrder> wo = findWOByTaskId(listOfWo, tsk.getTaskId());
				if (wo.isPresent()) {
					WorkOrder workOrdr = wo.get();
					if (!workOrdersalreadyinGroup.getWorkOrders().contains(workOrdr.getWorkOrderId())) {
						workOrdersalreadyinGroup.getWorkOrders().add(workOrdr.getWorkOrderId());
						workOrdersalreadyinGroup.getCrewAssigned().add(workOrdr.getCrewNames());
					}
				} else {
					String workOrderId = tsk.getTaskId();
					if (!workOrdersalreadyinGroup.getWorkOrders().contains(workOrderId)) {
						workOrdersalreadyinGroup.getWorkOrders().add(workOrderId);
						workOrdersalreadyinGroup.getCrewAssigned().add(tsk.getCrewId());
					}
				}
			}
		}
	}

	private void removExecutionPackageFromTask(Task task) {
		/********* Using Cachemanager for temp data store ************/
		Set<Task> tasksForUpdate = CacheManager.getTasksforupdate();
		Set<String> deletetedExecPkagList = CacheManager.getDeletetedexecpkaglist();
		Set<ExecutionPackage> execPkgListForUpdate = CacheManager.getExecpkglistforupdate();
		/********* Using Cachemanager for temp data store ************/
		ExecutionPackage executionpackage = task.getExecutionPackage();

		/*
		 * Before removing check of other tasks on the execution package if for
		 * all other tasks the dates are same then don't remove the package(TBD)
		 */
		executionpackage.removeTask(task);
		updateExecutionPackageLeadCrew(executionpackage, task, false);
		// update for scheduled date or any change in task
		if (!StringUtils.isEmpty(task.getTaskId())) {
			tasksForUpdate.add(task);
		}
		executionpackage.setCrtdUsr(userTokenRequest.getUserPrincipal());
		executionpackage.setLstUpdtdUsr(userTokenRequest.getUserPrincipal());
		execPkgListForUpdate.add(executionpackage);
		deletetedExecPkagList.add(task.getTaskId());
	}

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
		if ((!executionPkgLeadCrew.equals("")) && (!StringUtils.contains(crewPresent, executionPkgLeadCrew))) {
			logger.debug("in prepare workorder removed lead creew id from exec pakage ()",
					executionpackage.getLeadCrewId());
			executionpackage.setLeadCrewId("");
			executionpackage.setActioned("N");
		}

	}

	private Optional<Task> findTaskByWoId(final List<Task> list, final String woId) {
		return list.stream().filter(p -> p.getTaskId().equals(woId)).findAny();
	}

	private Optional<WorkOrder> findWOByTaskId(final List<WorkOrder> list, final String taskId) {
		return list.stream().filter(p -> p.getWorkOrderId().equals(taskId)).findAny();
	}

	private List<WorkOrder> findWoByCrew(final List<WorkOrder> list, final String crew) {
		return list.stream().filter(p -> p.getCrewNames().equals(crew)).collect(Collectors.toList());
	}

	private List<Task> fetchListOfTasksBySearchedDate(WorkOrderSearchRequest input, List<WorkOrder> listWOData)
			throws P6BusinessException {
		long startTime = System.currentTimeMillis();
		List<String> worOrders = new ArrayList<String>();
		if (listWOData != null) {
			listWOData.forEach(workOrder -> {
				worOrders.add(workOrder.getWorkOrderId());
			});

		}
		List<Date> dateRange = new ArrayList<Date>();
		if (input != null) {
			dateRange.add(input.getFromDate() != null ? dateUtils.toDateFromYYYY_MM_DD(input.getFromDate()) : null);
			dateRange.add(input.getToDate() != null ? dateUtils.toDateFromYYYY_MM_DD(input.getToDate()) : null);

		}
		logger.debug("Total time taken to updateTasksInDB {}", System.currentTimeMillis() - startTime);
		if (worOrders.size() > 0) {
			return workOrderDAO.fetchTasksByDateAndWo(dateRange, worOrders);
		} else {
			return workOrderDAO.fetchTasksByOnlyDate(dateRange);
		}
	}

	@Override
	@Async
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateTasksAndExecutionPackageInP6AndDB() throws P6BusinessException {
		long startTime = System.currentTimeMillis();
		Set<Task> tasksForUpdate = CacheManager.getTasksforupdate();
		Set<ExecutionPackage> execPkgList = CacheManager.getExecpkglistforupdate();
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
					workOrderDAO.saveTask(task);
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
					executionPackageDao.createOrUpdateExecPackage(exectionPkg);
				}
			}
		} catch (P6DataAccessException e) {
			logger.error("Entire Exception : ", e);
			logger.error("Error occurred in DB persist {}", e.getCause().getMessage());
		}
		logger.debug("Total time taken to updateTasksInDB {}", System.currentTimeMillis() - startTime);
		removeExecutionPackageinP6();
	}

	private void removeExecutionPackageinP6() throws P6BusinessException {
		long startTime = System.currentTimeMillis();
		Set<String> workOrderIds = CacheManager.getDeletetedexecpkaglist();

		if (!CollectionUtils.isEmpty(workOrderIds)) {
			try {
				logger.debug("removeExecutionPackageinP6 called with tasks # {}", workOrderIds.size());
				logger.debug("Calling to remove execution package for work orders {}", workOrderIds);
				Map<String, Integer> workOrderIdMap = p6wsClient.getWorkOrderIdMap();
				List<Integer> listOfObjectId = new ArrayList<Integer>();
				workOrderIds.forEach(workOrderId -> {
					Integer objectId = workOrderIdMap.get(workOrderId);
					if ((!workOrderIdMap.containsKey(workOrderId)) || objectId == null) {
						logger.info("input id # {} ", workOrderId);
						workOrderIdMap.put(workOrderId, 0);
					}
					listOfObjectId.add(workOrderIdMap.get(workOrderId));
				});
				boolean isSuccess = p6wsClient.removeExecutionPackage(listOfObjectId);
				logger.debug("Removal suceeeded {}", isSuccess);
			} catch (P6ServiceException e) {
				parseException(e);
			}

		}
		clearApplicationMemory();
		logger.debug("Total time taken to removeExecutionPackageinP6 {}", System.currentTimeMillis() - startTime);
	}

	public boolean convertStringToBoolean(String completed) {
		return P6Constant.ACTIONED_Y.equals(completed);
	}

	public String convertBooleanToString(boolean isCompleted) {
		return isCompleted ? P6Constant.ACTIONED_Y : P6Constant.ACTIONED_N;
	}

	private boolean getCompletedStatus(Set<TodoAssignment> toDos) {
		if (null == toDos || toDos.isEmpty()) {
			return Boolean.FALSE;
		}
		for (TodoAssignment todo : toDos) {
			if (!P6Constant.STATUS_COMPLETED.equalsIgnoreCase(todo.getStat())) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
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
	private WorkOrder prepareWorkOrder(WorkOrder workOrder, Task dbTask) throws P6DataAccessException {
		logger.debug("work order id in workorder returned from P6 =={}", workOrder.getWorkOrderId());
		Date scheduledDateForWorkOrder = dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate());
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
		if (dbTask.getTodoAssignments() != null) {
			workOrderNew.setCompleted(convertBooleanToString(getCompletedStatus(dbTask.getTodoAssignments())));
		}
		dbTask.setCrtdUsr(userTokenRequest.getUserPrincipal());
		dbTask.setLstUpdtdUsr(userTokenRequest.getUserPrincipal());
		Set<Task> tasksForUpdate = CacheManager.getTasksforupdate();
		Set<ExecutionPackage> executionPackageUpdateList = CacheManager.getExecpkglistforupdate();

		ExecutionPackage executionPackage = dbTask.getExecutionPackage();
		if (scheduledDateForWorkOrder != null && scheduledDateInTask != null) {
			logger.debug("in prepare workorder scheduledDateForWorkOrder {}", scheduledDateForWorkOrder);
			logger.debug("in prepare workorder scheduledDateInTask {}", scheduledDateInTask);
			if (scheduledDateForWorkOrder.compareTo(scheduledDateInTask) != 0) {
				logger.debug("in prepare workorder scheduledDateInTask is different so removing exec pkg",
						scheduledDateInTask);
				dbTask.setSchdDt(scheduledDateForWorkOrder);
				// if date is changed then execution package is null
				if (executionPackage != null) {
					/*
					 * check of other tasks on the execution package if for all
					 * other tasks the dates are same then don't remove the
					 * package
					 */
					/*
					 * if(isExecutionPackageTobeRetained(dbTask)){ //update
					 * execution package in
					 * 
					 * }else{
					 */
					removExecutionPackageFromTask(dbTask);
					// }
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

	@Override
	@Transactional
	public MetadataDTO fetchMetadata() throws P6BusinessException {

		List<TodoTemplate> toDoTemplateList = todoDAO.fetchAllToDos();
		List<ToDoItem> toDos = new ArrayList<>();
		for (TodoTemplate toDo : toDoTemplateList) {
			ToDoItem item = new ToDoItem();
			item.setCrtdTs(toDo.getCrtdTs().toString());
			item.setCrtdUsr(toDo.getCrtdUsr());
			if (null != toDo.getLstUpdtdTs()) {
				item.setLstUpdtdTs(toDo.getLstUpdtdTs().toString());
			}
			item.setLstUpdtdUsr(toDo.getLstUpdtdUsr());
			item.setTmpltDesc(toDo.getTmpltDesc());
			item.setTmpltId(String.valueOf(toDo.getTmpltId()));
			item.setToDoName(toDo.getTodoNam());
			item.setTypeId(toDo.getTypId().longValue());
			toDos.add(item);
		}
		depotCrewMap = resourceDetailDAO.fetchAllResourceDetail();
		logger.debug("depotCrewMap >>>{}", depotCrewMap);
		if (depotCrewMap != null) {
			logger.debug("depotCrewMap size>>>{}", depotCrewMap.size());
		}
		ResourceDTO resourceDTO = new ResourceDTO();
		resourceDTO.setDepotCrewMap(depotCrewMap);

		MetadataDTO metadataDTO = new MetadataDTO();
		metadataDTO.setToDoItems(toDos);
		metadataDTO.setResourceDTO(resourceDTO);
		return metadataDTO;
	}

	@Override
	@Transactional
	public ViewToDoStatus fetchWorkOrdersForViewToDoStatus(WorkOrderSearchRequest query) throws P6BusinessException {

		List<Task> tasks = null;

		Map<String, List<au.com.wp.corp.p6.dto.ToDoAssignment>> mapOfToDoIdWorkOrders = new HashMap<String, List<au.com.wp.corp.p6.dto.ToDoAssignment>>();
		ExecutionPackage executionPackage = null;
		ViewToDoStatus returnedVal = new ViewToDoStatus();
		if (null != query && (null != query.getExecPckgName() && (!"".equals(query.getExecPckgName())))) {
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList<Task>(executionPackage.getTasks()); // multiple
			// tasks
		} else {
			tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query); // single
			// task
		}
		for (Task task : tasks) {
			String key = null;
			if (task.getExecutionPackage() != null) {
				key = task.getExecutionPackage().getExctnPckgNam();
				returnedVal.setExctnPckgName(key);
				returnedVal.setSchedulingComment(task.getExecutionPackage().getExecSchdlrCmt());
				returnedVal.setDeportComment(task.getExecutionPackage().getExecDeptCmt());
			} else {
				key = task.getTaskId();
				returnedVal.setExctnPckgName("");
				returnedVal.setDeportComment(task.getDeptCmt());
				returnedVal.setSchedulingComment(task.getSchdlrCmt());
			}
			logger.debug("Key for fetch todo >>>{}", key);
			// TO DO
			Set<TodoAssignment> toDoEntities = task.getTodoAssignments();

			List<au.com.wp.corp.p6.dto.ToDoAssignment> assignmentDTOs = new ArrayList<au.com.wp.corp.p6.dto.ToDoAssignment>();
			if (null != toDoEntities) {
				logger.debug("Size of ToDoAssignment for task>>>{}", toDoEntities.size());
			}
			logger.debug("task id for each entry {}", task.getTaskId());
			for (TodoAssignment assignment : toDoEntities) {
				au.com.wp.corp.p6.dto.ToDoAssignment assignmentDTO = new au.com.wp.corp.p6.dto.ToDoAssignment();
				String workOrderId = task.getTaskId();
				long todoId = assignment.getTodoAssignMentPK().getTodoId().longValue();
				String toDoName = todoDAO.getToDoName(todoId);
				logger.debug("work order associated to each todo {} {}", toDoName, workOrderId);

				assignmentDTO.setComment(assignment.getCmts());
				if (null != assignment.getReqdByDt()) {
					assignmentDTO.setReqByDate(dateUtils.toStringDD_MM_YYYY(assignment.getReqdByDt()));
				}
				assignmentDTO.setStatus(assignment.getStat());
				assignmentDTO.setSupportingDoc(assignment.getSuprtngDocLnk());
				assignmentDTO.setWorkOrderId(workOrderId);
				assignmentDTO.setToDoName(toDoName);
				if (mapOfToDoIdWorkOrders.containsKey(toDoName)) {
					/* global map update against each todo across all tasks */
					logger.debug("retrived todoname =={} as key in global map with  list of AssignmentDto {}", toDoName,
							mapOfToDoIdWorkOrders.get(toDoName).size());
					mapOfToDoIdWorkOrders.get(toDoName).add(assignmentDTO);

				} else {
					/* new entry added for this todo */
					logger.debug("added  todoname =={} as key in global map with  entry  of AssignmentDto {}", toDoName,
							workOrderId);
					List<au.com.wp.corp.p6.dto.ToDoAssignment> assignments = new ArrayList<au.com.wp.corp.p6.dto.ToDoAssignment>();
					assignments.add(assignmentDTO);

					mapOfToDoIdWorkOrders.put(toDoName, assignments);
				}
				assignmentDTOs.add(assignmentDTO);
			}

		}
		Map<String, ToDoAssignment> mapOfGroupedTodoRecord = getGroupedTodowithWorkOrders(mapOfToDoIdWorkOrders);
		List<ToDoAssignment> listOfTodoAssignments = new ArrayList<ToDoAssignment>(mapOfGroupedTodoRecord.values());
		returnedVal.setTodoAssignments(listOfTodoAssignments);
		return returnedVal;
	}

	/**
	 * Method to go through all the view to status's TodoAssignment entry and
	 * update the list of workorders
	 * 
	 * @param values
	 * @param mapOfToDoIdWorkOrders
	 */
	private Map<String, ToDoAssignment> getGroupedTodowithWorkOrders(
			Map<String, List<ToDoAssignment>> mapOfToDoIdWorkOrders) {

		Map<String, ToDoAssignment> todoMap = new HashMap<String, ToDoAssignment>();
		mapOfToDoIdWorkOrders.forEach((todoName, assignments) -> {
			logger.debug("Merging for ToDo name {}, total assignments records count {}", todoName, assignments.size());
			ToDoAssignment groupedTodoAssignment = groupTodoAssinmentRecord(assignments);
			String todo = groupedTodoAssignment.getToDoName();
			logger.debug("Adding to merged records for todo {} , merged {}", todo,
					groupedTodoAssignment.getWorkOrders().toArray());
			todoMap.put(todo, groupedTodoAssignment);
		});

		return todoMap;
	}

	/**
	 * To merge multiple ToDo records in single Todo records for display
	 * 
	 * @param assignments
	 * @return
	 */
	private ToDoAssignment groupTodoAssinmentRecord(List<ToDoAssignment> assignments) {
		ToDoAssignment singleMergedTodo = new ToDoAssignment();
		Set<String> workOrders = new HashSet<>();
		Set<String> requiredByDate = new HashSet<>();
		Set<String> status = new HashSet<String>();
		Set<String> comments = new HashSet<String>();
		Set<String> supDocLinks = new HashSet<String>();
		// String toDoName ;
		logger.debug("before starting loop for assignments size = {}", assignments);
		assignments.forEach(toDoAssignment -> {
			String toDoName = toDoAssignment.getToDoName();
			logger.debug("todo name {}", toDoName);
			singleMergedTodo.setToDoName(toDoName); // single todo name
			logger.debug("Grouping for ToDo = {}", toDoName);
			workOrders.add(toDoAssignment.getWorkOrderId());
			logger.debug("workOrder for this todo = {}", toDoAssignment.getWorkOrderId());
			String reqByDate = toDoAssignment.getReqByDate() == null ? "" : toDoAssignment.getReqByDate();
			logger.debug("reqByDate for this todo {}", reqByDate);
			String strStatus = toDoAssignment.getStatus() == null ? "" : toDoAssignment.getStatus();
			boolean isNotSameReqByDate = requiredByDate.add(reqByDate);
			logger.debug("isSameReqByDate to be added for this todo {}", isNotSameReqByDate);
			boolean isNotSameStatus = status.add(strStatus);
			logger.debug("isNotSameStatus to be added for this todo {}", isNotSameStatus);
			comments.add(toDoAssignment.getComment() == null ? "" : toDoAssignment.getComment());
			supDocLinks.add(toDoAssignment.getSupportingDoc() == null ? "" : toDoAssignment.getSupportingDoc());
		});

		singleMergedTodo.setWorkOrders(Arrays.asList(workOrders.toArray(new String[workOrders.size()])));
		singleMergedTodo.setTypeId(todoDAO.getTypeId(singleMergedTodo.getToDoName()));
		if ((requiredByDate.size() > 1) && (status.size() > 1)) {
			singleMergedTodo.setReqByDate("");
			singleMergedTodo.setStatus("");
		}

		singleMergedTodo.setReqByDate(requiredByDate.iterator().next());
		singleMergedTodo.setStatus(status.iterator().next());
		if (ArrayUtils.isNotEmpty(comments.toArray(new String[comments.size()]))) {
			singleMergedTodo.setComment(StringUtils.join(comments.toArray(new String[comments.size()]), ","));
		}
		if (ArrayUtils.isNotEmpty(supDocLinks.toArray(new String[supDocLinks.size()]))) {
			singleMergedTodo
			.setSupportingDoc(StringUtils.join(supDocLinks.toArray(new String[supDocLinks.size()]), ","));
		}

		return singleMergedTodo;
	}

	@Override
	@Transactional
	public WorkOrder saveToDo(WorkOrder workOrder) throws P6BusinessException {

		if (workOrder == null)
			throw new IllegalArgumentException("Work Order canot be null");
		List<ExecutionPackageDTO> executionPackageDTOList = new ArrayList<ExecutionPackageDTO>();
		ExecutionPackageDTO executionPackageDTO = null;
		List<WorkOrder> workOrderList = new ArrayList<>();
		for (String workOrderId : workOrder.getWorkOrders()) { // can be
			// conisdered
			// for add batch
			WorkOrder wo = new WorkOrder();
			wo.setWorkOrderId(workOrderId);
			Task task = prepareTaskFromWorkOrderId(workOrderId, workOrder); // can
			// be
			// fectehd
			// in
			// single
			// call
			if (null != task.getExecutionPackage()) {
				logger.debug("task.getExecutionPackage()>> {}", task.getExecutionPackage().getExctnPckgNam());
				executionPackageDTO = new ExecutionPackageDTO();
				executionPackageDTO.setExctnPckgName(task.getExecutionPackage().getExctnPckgNam());
				workOrderList.add(wo);
			}
			workOrderDAO.saveTask(task);

		}
		if (executionPackageDTO != null) {
			executionPackageDTO.setWorkOrders(workOrderList);
			executionPackageDTOList.add(executionPackageDTO);
			executionPackageService.setExecutionPackageDTDOFoP6(executionPackageDTOList);
		}
		return workOrder;
	}

	private Task prepareTaskFromWorkOrderId(String workOrderId, WorkOrder workOrder) throws P6BusinessException {
		Task dbTask = workOrderDAO.fetch(workOrderId);
		Task updatedTask = prepareTaskBean(dbTask, workOrder, workOrderId);
		prepareToDoAssignmentList(updatedTask, workOrder);
		return updatedTask;
	}

	private void prepareToDoAssignmentList(Task updatedTask, WorkOrder workOrder) {

		logger.debug("inside prepareToDoAssignmentList");
		if (updatedTask == null) {
			return;
		}

		List<ToDoItem> requestToDos = workOrder.getToDoItems();
		Set<TodoAssignment> newToDos = new HashSet<>();
		Set<TodoAssignment> deleteToDos = new HashSet<>();
		Set<TodoAssignment> dBToDos = updatedTask.getTodoAssignments();
		if (null != requestToDos && !requestToDos.isEmpty()) {
			logger.debug("requestToDos is not null");
			for (ToDoItem reqToDo : requestToDos) {
				if (reqToDo.getWorkOrders().contains(updatedTask.getTaskId())) {
					TodoAssignment todoAssignment = new TodoAssignment();
					todoAssignment.getTodoAssignMentPK().setTask(updatedTask);
					todoAssignment.getTodoAssignMentPK().setTodoId(todoDAO.getToDoId(reqToDo.getToDoName()));
					newToDos.add(todoAssignment);
				}
			}
			if (null != dBToDos && !dBToDos.isEmpty()) {
				logger.debug("updatedTask.getTodoAssignments() is not null");
				updatedTask.getTodoAssignments().retainAll(newToDos);
				updatedTask.getTodoAssignments().addAll(newToDos);
			} else {
				if (updatedTask.getTodoAssignments() != null) {
					updatedTask.getTodoAssignments().addAll(newToDos);
				} else {
					// this is only required for JUNIT
					updatedTask.setTodoAssignments(newToDos);
				}
			}

		} else {
			if (null != dBToDos) {

				for (TodoAssignment allDbToDo : dBToDos) {
					logger.debug("TODO to be deleted from DB=={}", allDbToDo.getTodoAssignMentPK().getTodoId());
					deleteToDos.add(allDbToDo);
				}
				updatedTask.getTodoAssignments().removeAll(deleteToDos);

			}
		}

		logger.debug("After merging to do assignments size: " + updatedTask.getTodoAssignments());
	}

	private Task prepareTaskBean(Task dbTask, WorkOrder workOrder, String workOrderId) {
		// create new Task if not there in DB
		if (dbTask == null) {
			logger.debug("Task don't exists in portal DB..");
			dbTask = new Task();
			dbTask.setTaskId(workOrderId);
		}

		dbTask.setCmts(workOrder.getSchedulingToDoComment());
		dbTask.setSchdlrCmt(workOrder.getSchedulingToDoComment());
		dbTask.setCrewId(workOrder.getCrewNames());
		dbTask.setLeadCrewId(workOrder.getLeadCrew());
		java.util.Date scheduleDate = null;
		if (null != workOrder.getScheduleDate()) {
			scheduleDate = dateUtils.toDateFromDD_MM_YYYY(workOrder.getScheduleDate());
		}
		dbTask.setSchdDt(scheduleDate);
		dbTask.setDepotId(workOrder.getDepotId());
		dbTask.setMatrlReqRef(workOrder.getMeterialReqRef());
		if (!org.springframework.util.StringUtils.isEmpty(workOrder.getExctnPckgName())) {
			ExecutionPackage executionPackage = executionPackageDao.fetch(workOrder.getExctnPckgName());
			if (null != executionPackage) {
				executionPackage.setActioned(ACTIONED_Y);
				executionPackage.setExecSchdlrCmt(workOrder.getExecutionPkgComment());
				executionPackage.setExecDeptCmt(workOrder.getDepotToDoComment());
				dbTask.setExecutionPackage(executionPackage);
				dbTask.setActioned(ACTIONED_N);
			} else {
				dbTask.setActioned(ACTIONED_Y);
				dbTask.setDeptCmt(workOrder.getDepotToDoComment());
			}
			logger.debug("Execution Package {}", workOrder.getExctnPckgName());
		} else {
			dbTask.setActioned(ACTIONED_Y);
		}
		String userName = "Test User";
		if (userTokenRequest != null && userTokenRequest.getUserPrincipal() != null) {
			userName = userTokenRequest.getUserPrincipal();
		}
		dbTask.setCrtdUsr(userName);
		dbTask.setLstUpdtdUsr(userName);
		return dbTask;
	}

	@Override
	@Transactional
	public List<WorkOrder> fetchWorkOrdersForAddUpdateToDo(WorkOrderSearchRequest query) throws P6BusinessException {

		List<Task> tasks = null;
		ExecutionPackage executionPackage = null;
		logger.debug("ExecPckgName >>>>{}", query.getExecPckgName());
		logger.debug("WorkOrderId >>>>{}", query.getWorkOrderId());
		if (null != query && null != query.getExecPckgName()) {
			executionPackage = executionPackageDao.fetch(query.getExecPckgName());
			tasks = new ArrayList<Task>(executionPackage.getTasks());
		} else {
			tasks = workOrderDAO.fetchWorkOrdersForViewToDoStatus(query);
		}

		Map<String, WorkOrder> workOrderMap = new HashMap<String, WorkOrder>();
		Map<String, Map<Long, ToDoItem>> workOrderToDoMap = new HashMap<String, Map<Long, ToDoItem>>();

		for (Task task : tasks) {

			String executionPkg = null;
			if (task.getExecutionPackage() != null) {
				executionPkg = task.getExecutionPackage().getExctnPckgNam();
			}

			WorkOrder workOrder = null;
			Map<Long, ToDoItem> toDoMap = null;
			if (!StringUtils.isEmpty(executionPkg) && workOrderMap.containsKey(executionPkg)) {
				workOrder = workOrderMap.get(executionPkg);
				workOrder.setCrewNames(workOrder.getCrewNames() + "," + task.getCrewId());
				workOrder.getWorkOrders().add(task.getTaskId());
				toDoMap = workOrderToDoMap.get(executionPkg);
			} else {
				workOrder = new WorkOrder();
				if (!StringUtils.isEmpty(executionPkg)) {
					workOrder.setExctnPckgName(executionPkg);
					workOrder.setExecutionPkgComment(executionPackage.getExecSchdlrCmt());
					workOrder.setDepotToDoComment(executionPackage.getExecDeptCmt());
				} else {
					executionPkg = task.getTaskId();
				}
				List<String> workOrders = new ArrayList<String>();
				workOrders.add(task.getTaskId());
				workOrder.setWorkOrders(workOrders);
				workOrder.setLeadCrew(task.getLeadCrewId());
				workOrder.setCrewNames(task.getCrewId());
				workOrder.setScheduleDate(task.getSchdDt().toString());
				workOrder.setSchedulingToDoComment(task.getSchdlrCmt());
				if (StringUtils.isEmpty(workOrder.getDepotToDoComment())) {
					workOrder.setDepotToDoComment(task.getDeptCmt());
				}
				toDoMap = new HashMap<Long, ToDoItem>();
				workOrderMap.put(executionPkg, workOrder);
			}

			if (task.getTodoAssignments() != null) {
				for (TodoAssignment todo : task.getTodoAssignments()) {
					Long todoId = todo.getTodoAssignMentPK().getTodoId().longValue();
					Task taskToDo = todo.getTodoAssignMentPK().getTask();
					if (tasks.contains(taskToDo)) {
						if (toDoMap.containsKey(todoId)) {
							toDoMap.get(todoId).getWorkOrders().add(taskToDo.getTaskId());
						} else {
							ToDoItem item = new ToDoItem();
							item.setTodoId(String.valueOf(todoId));
							item.setToDoName(todoDAO.getToDoName(todoId));
							List<String> workOrders = new ArrayList<String>();
							workOrders.add(taskToDo.getTaskId());
							item.setWorkOrders(workOrders);
							toDoMap.put(todoId, item);
						}
					}
				}
				workOrderToDoMap.put(executionPkg, toDoMap);
			}
		}

		List<WorkOrder> workOrders = new ArrayList<WorkOrder>(workOrderMap.values());
		for (WorkOrder workOrder : workOrders) {
			String executionPkg = workOrder.getExctnPckgName();
			if (StringUtils.isEmpty(executionPkg)) {
				executionPkg = workOrder.getWorkOrders().get(0);
			}
			workOrder.setToDoItems(new ArrayList<ToDoItem>(workOrderToDoMap.get(executionPkg).values()));
		}
		return workOrders;
	}

	@Override
	@Transactional
	public ViewToDoStatus saveViewToDoStatus(ViewToDoStatus workOrder) throws P6BusinessException {

		if (workOrder != null) {

			List<Task> taskList = new ArrayList<Task>();
			if (!StringUtils.isEmpty(workOrder.getExctnPckgName()) && !workOrder.getExctnPckgName().equals("PKG1")) {
				ExecutionPackage pkg = executionPackageDao.fetch(workOrder.getExctnPckgName());
				taskList.addAll(pkg.getTasks());
			} else {
				taskList.add(workOrderDAO.fetch(workOrder.getWorkOrders().get(0)));
			}

			for (Task task : taskList) {
				for (TodoAssignment todo : task.getTodoAssignments()) {
					for (au.com.wp.corp.p6.dto.ToDoAssignment assignmentDTO : workOrder.getTodoAssignments()) {

						if (!StringUtils.isEmpty(assignmentDTO.getToDoName())
								&& todoDAO.getToDoId(assignmentDTO.getToDoName()) != null
								&& todoDAO.getToDoId(assignmentDTO.getToDoName()).longValue() == todo
								.getTodoAssignMentPK().getTodoId().longValue()
								&& assignmentDTO.getWorkOrders().contains(task.getTaskId())) {

							try {
								mergeToDoAssignment(todo, assignmentDTO);
							} catch (ParseException e) {
								logger.error("Parsing date failed: ", e);
							}

						}
					}
				}
				workOrderDAO.saveTask(task);
			}
		}
		return workOrder;
	}

	private void mergeToDoAssignment(TodoAssignment assignment, ToDoAssignment assignmentDTO) throws ParseException {
		if (!("".equalsIgnoreCase(assignmentDTO.getReqByDate()))) {
			logger.debug("updating req by date {}", assignmentDTO.getReqByDate());
			assignment.setReqdByDt(dateUtils.toDateFromDD_MM_YYYY(assignmentDTO.getReqByDate()));
		}
		assignment.setCmts(assignmentDTO.getComment());
		assignment.setStat(assignmentDTO.getStatus());
		assignment.setSuprtngDocLnk(assignmentDTO.getSupportingDoc());

	}

	@Override
	public void clearApplicationMemory() {
		CacheManager.getDeletetedexecpkaglist().clear();
		CacheManager.getTasksforupdate().clear();
		CacheManager.getExecpkglistforupdate().clear();
		CacheManager.getDeletetedexecpkaglist().clear();
		listWOData = null;
		tasksInDb = null;

	}

}
